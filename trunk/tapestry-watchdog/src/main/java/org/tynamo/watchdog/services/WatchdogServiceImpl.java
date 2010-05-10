package org.tynamo.watchdog.services;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.jar.JarFile;

import org.apache.tapestry5.ioc.annotations.Symbol;
import org.tynamo.watchdog.StreamGobbler;

import tynamo_watchdog.Watchdog;

public class WatchdogServiceImpl implements WatchdogService {

	private Process watchdog;
	private WatchdogLeash watchdogLeash;

	BufferedWriter bw;
	private Integer smtpPort;
	private String sendEmail;

	public WatchdogServiceImpl(@Symbol(Watchdog.SMTP_PORT) final Integer smtpPort, @Symbol(Watchdog.SEND_EMAIL) String sendEmail) {
		this.smtpPort = smtpPort;
		this.sendEmail = sendEmail;
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
	public synchronized void startWatchdog() throws IOException, URISyntaxException {
		File watchdogFolder = prepareWatchdog();
		String appName = whoAmI();
		// FIXME if appName is empty, assume javamail is in classpath as well
		if (appName.isEmpty()) appName = "dev/exploded";

		// String targetPath = "file://" + getClass().getClassLoader().getResource("").getFile() + "..";

		final String packageName = Watchdog.class.getPackage().getName();
		String[] args = new String[10];
		args[0] = "java";
		args[1] = "-D" + Watchdog.SEND_EMAIL + "=" + sendEmail;
		args[2] = "-D" + Watchdog.SMTP_PORT + "=" + smtpPort;
		args[3] = "-Xmx16m";
		args[4] = "-Xmx32m";
		args[5] = "-XX:MaxPermSize=16m";
		args[6] = "-cp";
		args[7] = "." + File.pathSeparator + packageName + File.separator + WatchdogModule.javamailSpec + ".jar" + File.pathSeparator
				+ packageName + File.separator + WatchdogModule.javamailProvider + ".jar";
		args[8] = Watchdog.class.getName();
		args[9] = appName;

		// You *have* to start with inherited environment or set at least some of the most critical
		// environment variables manually (such as SystemRoot), otherwise I got
		// Unrecognized Windows Sockets error: 10106: errors
		watchdog = Runtime.getRuntime().exec(args, null, watchdogFolder);

		(new StreamGobbler(watchdog.getErrorStream(), "WATCHDOG ERROR")).start();
		(new StreamGobbler(watchdog.getInputStream(), "WATCHDOG OUTPUT")).start();

		OutputStreamWriter writer = new OutputStreamWriter(watchdog.getOutputStream());
		bw = new BufferedWriter(writer);

		watchdogLeash = new WatchdogLeash();
		watchdogLeash.start();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.tynamo.watchdog.services.WatchdogService#dismissWatchdog()
	 */
	public void dismissWatchdog() throws IOException {
		bw.write("0");
		bw.newLine();
		bw.flush();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.tynamo.watchdog.services.WatchdogService#alarmWatchdog()
	 */
	public void alarmWatchdog() throws IOException {
		bw.close();
	}

	class WatchdogLeash extends Thread {

		public WatchdogLeash() {
			setDaemon(true);
		}

		@Override
		public void run() {
			try {
				while (true) {
					bw.newLine();
					sleep(1000);
				}
			} catch (IOException e) {
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
