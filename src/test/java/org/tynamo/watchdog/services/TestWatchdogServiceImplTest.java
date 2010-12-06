package org.tynamo.watchdog.services;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.net.URISyntaxException;

import javax.mail.MessagingException;

import org.slf4j.LoggerFactory;
import org.subethamail.wiser.Wiser;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class TestWatchdogServiceImplTest {

	private int smtpPort = 1025;
	private static Wiser smtpServer;
	WatchdogServiceImpl watchdogService;

	public TestWatchdogServiceImplTest() throws IOException, URISyntaxException, InterruptedException {
		watchdogService = new WatchdogServiceImpl(LoggerFactory.getLogger(TestWatchdogServiceImplTest.class), false, "unittestapp",
				"localhost", 1025, "test@test.com", 5000, 30000);
	}

	@BeforeClass
	void startSmtpService() {
		smtpServer = new Wiser();
		smtpServer.setPort(smtpPort);
		smtpServer.start();
	}

	@AfterClass
	public void stopSmtpService() {
		smtpServer.stop();
	}

	@Test
	public void emailSentWhenProcessLost() throws InterruptedException, IOException, URISyntaxException, MessagingException {
		watchdogService.startWatchdog();
		Thread.sleep(6000);
		watchdogService.alarmWatchdog();
		Thread.sleep(11000);
		assertTrue(smtpServer.getMessages().size() > 0);
		System.out.println(smtpServer.getMessages().get(0).getMimeMessage().getContent());
		smtpServer.stop();
	}

	@Test
	public void noEmailsWhenWatchdogDismissed() throws InterruptedException, IOException, URISyntaxException {
		watchdogService.startWatchdog();
		// SmtpServer cannot be reliably stopped and restarted so resort to some trickery
		int previousReceivedEmailCount = smtpServer.getMessages().size();
		Thread.sleep(1000);
		watchdogService.dismissWatchdog();
		watchdogService.alarmWatchdog();
		assertEquals(previousReceivedEmailCount, smtpServer.getMessages().size());
	}

	@Test
	public void prepareWatchdog() throws IOException, URISyntaxException {
		watchdogService.prepareWatchdog();
	}

	@Test
	public void whoAmi() throws IOException, URISyntaxException {
		assertNotNull(watchdogService.whoAmI());
	}

	// Comment out just so it doesn't break the automated build
	// @Test
	public void javaProcessExec() throws IOException, URISyntaxException, InterruptedException {
		Process process = Runtime.getRuntime().exec("java -version");
		assertEquals(0, process.waitFor());
	}

}
