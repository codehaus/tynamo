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

	public static final String STOP_MESSAGE = Watchdog.class.getSimpleName();

	private String emailRecipient;
	private String smtpHost;
	private Integer smtpPort;
	private String appName;
	private String hostname;

	public Watchdog(String appName, String emailRecipient, String smtpHost, String smtpPort) {
		this.appName = appName;
		this.emailRecipient = emailRecipient;
		this.smtpHost = smtpHost;
		// FIXME catch numberFormatException
		this.smtpPort = smtpPort == null ? null : Integer.valueOf(smtpPort);
		hostname = System.getenv("HOSTNAME");
		if (hostname == null) hostname = "localhost.localdomain";
	}

	public static void main(String[] args) throws Exception {
		// With no arguments, print out the help and exit
		List<String> arguments = new ArrayList<String>(Arrays.asList(args));

		if (args.length <= 0 || arguments.contains("--help")) {
			System.out.println("Tynamo watchdog. This application is designed to run as a child process ");
			return;
		}

		String appName = args.length > 0 ? args[0] : "dev/exploded";
		Thread.sleep(5000);
		int available = 0;
		byte[] bytes = new byte[STOP_MESSAGE.getBytes().length];
		try {
			while ((available = System.in.available()) > 0) {
				if (available >= STOP_MESSAGE.getBytes().length) System.exit(0);
				// skip() didn't seem to work for standard input
				System.in.read(bytes, 0, available);
				Thread.sleep(10000);
			}
		} catch (IOException e) {
			System.err.println("Parent process stopped at " + (new Date()));
		}
		Watchdog watchdog = new Watchdog(appName, System.getProperty(SEND_EMAIL), System.getProperty(SMTP_HOST), System.getProperty(SMTP_PORT));
		watchdog.sendEmail();
	}

	boolean sendEmail() throws MessagingException {
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

		msg.setSubject("Application " + appName + " has failed!");
		StringBuilder sb = new StringBuilder();
		sb.append("Master application '");
		sb.append(appName);
		sb.append("' at ");
		sb.append(hostname);
		sb.append(" was lost at ");
		sb.append(new Date());
		sb.append("\n");
		sb.append("Action taken: email sent to '");
		sb.append("'\n");
		sb.append(addressFrom);
		msg.setText(sb.toString());
		Transport.send(msg);
		return true;
	}
}
