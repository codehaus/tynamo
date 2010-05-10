package tynamo_watchdog;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class Watchdog {
	public static final String SMTP_PORT = "smtp.port";
	public static final String SEND_EMAIL = "watchdog.sendemail";
	public static final String EMAIL_PATH = "watchdog.emailpath";
	public static final String COMMAND = "watchdog.command";

	private String emailRecipient;
	private Integer smtpPort;
	private String appName;
	private String hostname;

	public Watchdog(String appName, String emailRecipient, String smtpPort) {
		this.appName = appName;
		this.emailRecipient = emailRecipient;
		// FIXME catch numberFormatException
		this.smtpPort = smtpPort == null ? null : Integer.valueOf(smtpPort);
		hostname = System.getenv("HOSTNAME");
		if (hostname == null) hostname = "localhost.localdomain";
	}

	public static void main(String[] args) throws Exception {
		String appName = args.length > 0 ? args[0] : "dev/exploded";
		Thread.sleep(2000);
		InputStreamReader isr = new InputStreamReader(System.in);
		BufferedReader br = new BufferedReader(isr);
		String line;
		while ((line = br.readLine()) != null) {
			if ("0".equals(line)) System.exit(0);
			Thread.sleep(2000);
		}

		Watchdog watchdog = new Watchdog(appName, System.getProperty(SEND_EMAIL), System.getProperty(SMTP_PORT));
		watchdog.sendEmail();
	}

	boolean sendEmail() throws MessagingException {
		if (emailRecipient == null || emailRecipient.isEmpty()) return false;
		System.out.println("Sending email to: " + emailRecipient + " " + System.getProperty(SMTP_PORT));
		boolean debug = false;

		// Set the host smtp address
		Properties props = new Properties();
		props.put("mail.smtp.host", "127.0.0.1");
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

		msg.setSubject("Application " + appName + "failed!");
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
