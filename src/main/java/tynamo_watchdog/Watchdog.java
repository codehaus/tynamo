package tynamo_watchdog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class Watchdog {
	public static final String SMTP_HOST = "smtp.host";
	public static final String SMTP_PORT = "smtp.port";
	public static final String SEND_EMAIL = "watchdog.sendemail";
	public static final String EMAIL_PATH = "watchdog.emailpath";
	public static final String COMMAND = "watchdog.command";
	public static final String KEEPALIVE_INTERVAL = "watchdog.keepalive";
	public static final String FINALALARM_DELAY = "watchdog.alarmdelay";

	public static final String STOP_MESSAGE = Watchdog.class.getSimpleName();

	private String emailRecipient;
	private String smtpHost;
	private Integer smtpPort;
	private String appName;
	private String hostname;

	private long lastOk;
	private long keepAliveInterval = 5000L;
	private long finalAlarmDelay = 60000L;
	private boolean warningSent;

	public Watchdog(String appName, String emailRecipient, String smtpHost, String smtpPort, Long keepAliveInterval, Long finalAlarmDelay) {
		this.appName = appName;
		this.emailRecipient = emailRecipient;
		this.smtpHost = smtpHost;
		if (keepAliveInterval != null) this.keepAliveInterval = keepAliveInterval;
		if (finalAlarmDelay != null) this.finalAlarmDelay = finalAlarmDelay;
		// FIXME catch numberFormatException
		this.smtpPort = smtpPort == null ? null : Integer.valueOf(smtpPort);
		hostname = System.getenv("HOSTNAME");
		if (hostname == null) hostname = "localhost.localdomain";
		lastOk = System.currentTimeMillis();
	}

	public static void main(String[] args) throws Exception {
		// With no arguments, print out the help and exit
		List<String> arguments = new ArrayList<String>(Arrays.asList(args));

		if (args.length <= 0 || arguments.contains("--help")) {
			System.out.println("Tynamo watchdog. This application is designed to run as a child process ");
			return;
		}

		String appName = args.length > 0 ? args[0] : "dev/exploded";
		sleep(5000);
		String value = System.getProperty(KEEPALIVE_INTERVAL);
		Long keepAliveInterval = null;
		try {
			keepAliveInterval = Long.valueOf(value);
		} catch (NumberFormatException e) {
		}
		value = System.getProperty(FINALALARM_DELAY);
		Long finalAlarmDelay = null;
		try {
			finalAlarmDelay = Long.valueOf(value);
		} catch (NumberFormatException e) {
		}

		Watchdog watchdog = new Watchdog(appName, System.getProperty(SEND_EMAIL), System.getProperty(SMTP_HOST), System.getProperty(SMTP_PORT),
				keepAliveInterval, finalAlarmDelay);
		watchdog.go();
	}

	public void go() {
		try {
			while (lastOk + finalAlarmDelay > System.currentTimeMillis())
				makeRounds();
		} catch (IOException e) {
			System.err.println("Parent process stopped at " + (new Date()));
		}
		// Exited either because of exception thrown or because exceeded finalAlarmDelay
		// BY default, send the application lost email. makeRounds() will System.exit immediately if STOP_MESSAGE is
		// received
		sendApplicationLostEmail();
	}

	private static void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
		}
	}

	/**
	 * makeRounds() will make system exit immediately if STOP_MESSAGE is received
	 * 
	 * @throws IOException
	 */
	public void makeRounds() throws IOException {
		int available = 0;
		byte[] bytes = new byte[STOP_MESSAGE.getBytes().length];

		while ((available = System.in.available()) > 0) {
			if (available >= STOP_MESSAGE.getBytes().length) System.exit(0);
			// skip() didn't seem to work for standard input
			System.in.read(bytes, 0, available);
			lastOk = System.currentTimeMillis();
			warningSent = false;
			// Normally, read at half the rate of the writes
			sleep(keepAliveInterval * 2);
		}
		// Send the first warning
		if (!warningSent) sendRunningSlowEmail();
		warningSent = true;
		sleep(keepAliveInterval);
	}

	void sendRunningSlowEmail() {
		String subject = "Application " + appName + " is running slow!";
		StringBuilder sb = new StringBuilder();
		sb.append("Master application '");
		sb.append(appName);
		sb.append("' at ");
		sb.append(hostname);
		sb.append(" has missed sending some alive signals. The last OK was received at ");
		sb.append(new Date(lastOk));
		sb.append(". \n");
		sb.append("This may indicate the application has dead-locked, been unexpectedly terminated or is running out of resources. \n");
		sb.append("Action taken: email sent to '");
		sb.append(emailRecipient);
		sb.append("', still monitoring\n");
		try {
			sendEmail(subject, sb);
		} catch (MessagingException e) {
			System.err.println("Couldn't send warning email because of: " + e.getMessage());
		}
	}

	void sendApplicationLostEmail() {
		String subject = "Application " + appName + " has failed!";
		StringBuilder sb = new StringBuilder();
		sb.append("Master application '");
		sb.append(appName);
		sb.append("' at ");
		sb.append(hostname);
		sb.append(" was lost at ");
		sb.append(new Date());
		sb.append("\n");
		sb.append("Action taken: email sent to '");
		sb.append(emailRecipient);
		sb.append("'\n");

		try {
			sendEmail(subject, sb);
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	boolean sendEmail(String subject, StringBuilder content) throws MessagingException {
		if (emailRecipient == null || emailRecipient.isEmpty()) return false;
		System.out.println("Sending email to: " + emailRecipient + " " + System.getProperty(SMTP_PORT));
		boolean debug = false;

		// Set the host smtp address
		Properties props = new Properties();
		props.put("mail.smtp.host", smtpHost);
		props.put("mail.smtp.port", String.valueOf(smtpPort));
		props.put("mail.smtp.debug", "true");

		// create some properties and get the default Session
		Session session = Session.getDefaultInstance(props, null);
		session.setDebug(debug);

		// create a message
		Message msg = new MimeMessage(session);

		// set the from and to addresses
		InternetAddress addressFrom = new InternetAddress("watchdog@" + hostname);
		msg.setFrom(addressFrom);

		InternetAddress[] addressTo = new InternetAddress[1];
		addressTo[0] = new InternetAddress(emailRecipient);
		msg.setRecipients(Message.RecipientType.TO, addressTo);

		msg.setSubject(subject);
		msg.setText(content.toString());
		Transport.send(msg);
		return true;
	}
}
