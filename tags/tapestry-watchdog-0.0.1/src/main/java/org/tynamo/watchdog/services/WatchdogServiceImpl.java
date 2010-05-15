package org.tynamo.watchdog.services;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.jar.JarFile;

import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.internal.InternalConstants;
import org.apache.tapestry5.ioc.annotations.EagerLoad;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.slf4j.Logger;
import org.tynamo.watchdog.StreamGobbler;

import tynamo_watchdog.Watchdog;

@EagerLoad
public class WatchdogServiceImpl implements WatchdogService {

	private Process watchdog;
	private WatchdogLeash watchdogLeash;
	private volatile boolean watchdogAlarmed;

	OutputStream watchdogOutputStream;
	private final String appPackageName;
	private final String smtpHost;
	private final Integer smtpPort;
	private final String sendEmail;
	private final Logger logger;

	public WatchdogServiceImpl(Logger logger, @Symbol(SymbolConstants.PRODUCTION_MODE) boolean productionMode,
			@Inject @Symbol(InternalConstants.TAPESTRY_APP_PACKAGE_PARAM) final String appPackageName,
			@Inject @Symbol(Watchdog.SMTP_HOST) final String smtpHost, @Symbol(Watchdog.SMTP_PORT) final Integer smtpPort,
			@Inject @Symbol(Watchdog.SEND_EMAIL) String sendEmail) throws IOException, URISyntaxException, InterruptedException {
		this.logger = logger;
		this.appPackageName = appPackageName;
		this.smtpHost = smtpHost;
		this.smtpPort = smtpPort;
		this.sendEmail = sendEmail;
		if (productionMode) startWatchdog();
	}

	/**
	 * Extract a resource from jar, mark it for deletion upon exit, and return its location.
	 */
	File extractFromJar(URL resource, File watchdogFolder) throws IOException {
		// put this jar in a file system so that we can load jars from there
		String fileName = resource.getPath().substring(resource.getPath().lastIndexOf("/"));

		File file = new File(watchdogFolder, fileName);
		try {
			file.createNewFile();
		} catch (IOException e) {
			String tmpdir = System.getProperty("java.io.tmpdir");
			IOException x = new IOException("Watchdog failed to create a temporary file in " + tmpdir);
			x.initCause(e);
			throw x;
		}
		InputStream is = resource.openStream();
		try {
			OutputStream os = new FileOutputStream(file);
			try {
				copyStream(is, os);
			} finally {
				os.close();
			}
		} finally {
			is.close();
		}

		file.deleteOnExit();
		return file;
	}

	private static void copyStream(InputStream in, OutputStream out) throws IOException {
		byte[] buf = new byte[8192];
		int len;
		while ((len = in.read(buf)) > 0)
			out.write(buf, 0, len);
	}

	File prepareWatchdog() throws IOException {
		File tempFile = File.createTempFile("forwatchdog", "test");
		tempFile.deleteOnExit();
		File watchdogFolder = new File(tempFile.getParentFile(), Watchdog.class.getPackage().getName());
		watchdogFolder.mkdir();

		extractFromJar(Watchdog.class.getResource(Watchdog.class.getSimpleName() + ".class"), watchdogFolder);
		extractFromJar(Watchdog.class.getResource(WatchdogModule.javamailSpec + ".jar"), watchdogFolder);
		extractFromJar(Watchdog.class.getResource(WatchdogModule.javamailProvider + ".jar"), watchdogFolder);
		return watchdogFolder.getParentFile();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.tynamo.watchdog.services.WatchdogService#startWatchdog()
	 */
	public synchronized void startWatchdog() throws IOException, URISyntaxException, InterruptedException {
		File watchdogFolder = prepareWatchdog();
		// whoamI gives us the watchdog jar when libs are loaded separately which is less than ideal
		// So don't use this at all, use appPackageName instead
		// String appName = whoAmI();
		// if (appName.isEmpty()) appName = "dev/exploded";

		Process testJavaProcess = Runtime.getRuntime().exec("java -version");
		// TODO You could also read the output and make sure java is at least 1.5

		try {
			if (testJavaProcess.waitFor() != 0) {
				logger.error("Couldn't execute java in given environment - is java on PATH? Cannot start the watchdog");
				return;
			}
		} catch (IllegalThreadStateException e) {
			logger.error("Testing java execution didn't return immediately. Report this issue to Tynamo.org");
		}

		final String packageName = Watchdog.class.getPackage().getName();
		String[] args = new String[11];
		args[0] = "java";
		args[1] = "-D" + Watchdog.SEND_EMAIL + "=" + sendEmail;
		args[2] = "-D" + Watchdog.SMTP_HOST + "=" + smtpHost;
		args[3] = "-D" + Watchdog.SMTP_PORT + "=" + smtpPort;
		// With -Xms4m, at least 64-bit 1.6 jvm you get:
		// Error occurred during initialization of VM
		// Too small initial heap for new size specified
		args[4] = "-Xms8m";
		args[5] = "-Xmx16m";
		args[6] = "-XX:MaxPermSize=16m";
		args[7] = "-cp";
		args[8] = "." + File.pathSeparator + packageName + File.separator + WatchdogModule.javamailSpec + ".jar" + File.pathSeparator
				+ packageName + File.separator + WatchdogModule.javamailProvider + ".jar";
		args[9] = Watchdog.class.getName();
		args[10] = appPackageName;

		StringBuilder command = new StringBuilder();
		for (String value : args) {
			command.append(value);
			command.append(" ");
		}

		logger.info("Starting watchdog with command: " + command.toString());

		// You *have* to start with inherited environment or set at least some of the most critical
		// environment variables manually (such as SystemRoot), otherwise I got
		// Unrecognized Windows Sockets error: 10106: errors
		watchdog = Runtime.getRuntime().exec(args, null, watchdogFolder);

		(new StreamGobbler(watchdog.getErrorStream(), "WATCHDOG ERROR")).start();
		(new StreamGobbler(watchdog.getInputStream(), "WATCHDOG OUTPUT")).start();

		watchdogOutputStream = watchdog.getOutputStream();

		// Intentionally try to cause an exception to see if the process is still alive and kicking
		try {
			int exitCode = watchdog.exitValue();
			logger.error("Watchdog failed to start: the process exited immediately with exit code " + exitCode);
			return;
		} catch (IllegalThreadStateException e) {
			// Ignore, process hasn't exited
		}

		watchdogLeash = new WatchdogLeash();
		watchdogLeash.start();

		// TODO Make adding shutdownhook configurable
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				try {
					logger.warn("Dismissing watchdog before controlled JVM shutdown");
					dismissWatchdog();
				} catch (IOException e) {
					logger.warn("Couldn't controllably dismiss the watchdog. Is watchdog still alive?");
				}
				try {
					int exitCode = watchdog.exitValue();
					logger.error("Watchdog has already exited with exit code " + exitCode);
				} catch (IllegalThreadStateException e) {
					// Ignore, can't do anything about the watchdog process
				}
			}
		});

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.tynamo.watchdog.services.WatchdogService#dismissWatchdog()
	 */
	public void dismissWatchdog() throws IOException {
		watchdogOutputStream.write(Watchdog.STOP_MESSAGE.getBytes());
		watchdogOutputStream.flush();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.tynamo.watchdog.services.WatchdogService#alarmWatchdog()
	 */
	public void alarmWatchdog() throws IOException {
		watchdogOutputStream.close();
	}

	class WatchdogLeash extends Thread {

		public WatchdogLeash() {
			setDaemon(true);
		}

		@Override
		public void run() {
			try {
				while (true) {
					watchdogOutputStream.write(0);
					watchdogOutputStream.flush();
					sleep(5000);
				}
			} catch (IOException e) {
				// Alarming the watchdog manually triggers the IOException
				if (watchdogAlarmed) return;
				logger.warn("IO exception occurred while communicating with the watchdog process. Was the watchdog killed? Releasing the leash");
				try {
					logger.info("Watchdog process exited with exit code " + watchdog.exitValue());
				} catch (IllegalThreadStateException e1) {
					// Ignore, process hasn't exited
				}
			} catch (InterruptedException e) {
			}
		}
	}

	/**
	 * Figures out the URL of <tt>war</tt>.
	 */
	public String whoAmI() throws IOException, URISyntaxException {
		// There is no portable way to find where the locally cached copy
		// of war/jar is; JDK 6 is too smart. (See HUDSON-2326 - this code was adapted from Hudson.)
		try {
			URL classFile = Watchdog.class.getResource(Watchdog.class.getSimpleName() + ".class");
			JarFile jf = ((JarURLConnection) classFile.openConnection()).getJarFile();
			String fileName = jf.getName();
			fileName = fileName.substring(fileName.lastIndexOf("/") + 1);
		} catch (Exception x) {
			System.err.println("ZipFile.name trick did not work, using fallback: " + x);
		}
		URL classFile = Watchdog.class.getProtectionDomain().getCodeSource().getLocation();
		String fileName = classFile.toString();
		return fileName.substring(fileName.lastIndexOf("/") + 1);
	}
}
