package org.tynamo.watchdog.services;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.URISyntaxException;
import java.util.Iterator;

import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.dumbster.smtp.SimpleSmtpServer;
import com.dumbster.smtp.SmtpMessage;

public class TestWatchdogServiceImplTest {

	private int smtpPort = 1025;
	private static SimpleSmtpServer smtpServer;
	WatchdogServiceImpl watchdogService;

	public TestWatchdogServiceImplTest() throws IOException, URISyntaxException, InterruptedException {
		watchdogService = new WatchdogServiceImpl(LoggerFactory.getLogger(TestWatchdogServiceImplTest.class), false, "unittestapp",
				"localhost", 1025, "test@test.com");
	}

	@BeforeClass
	void startSmtpService() {
		// NOTE The problem is that if Process.destroy() is called (like with
		// Eclipse red button to terminate)
		// the shutdownhook is not called and the smtpserver's ServerSocket is
		// not properly closed
		// However, if try to accept and timeout, it seems to clear the socket,
		// so do that always before
		// instantiating the real server
		// smtpServer = new SimpleSmtpServer(smtpPort);
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(smtpPort);
			serverSocket.setReuseAddress(true);
			serverSocket.setSoTimeout(1500);
			serverSocket.accept();
			serverSocket.close();
		} catch (IOException e) {
			// Ignore
			if (serverSocket != null) try {
				serverSocket.close();
				serverSocket = null;
			} catch (IOException e1) {
				// ignore
			}
		}
		smtpServer = SimpleSmtpServer.start(smtpPort);
	}

	@AfterClass
	public void stopSmtpService() {
		if (!smtpServer.isStopped()) smtpServer.stop();
	}

	@SuppressWarnings("unchecked")
	@Test
	public void emailSentWhenProcessLost() throws InterruptedException, IOException, URISyntaxException {
		watchdogService.startWatchdog();
		Thread.sleep(6000);
		watchdogService.alarmWatchdog();
		Thread.sleep(11000);
		assertTrue(smtpServer.getReceivedEmailSize() > 0);
		Iterator<SmtpMessage> messages = smtpServer.getReceivedEmail();
		SmtpMessage message = messages.next();
		System.out.println(message.getBody());
		smtpServer.stop();
	}

	@Test
	public void noEmailsWhenWatchdogDismissed() throws InterruptedException, IOException, URISyntaxException {
		watchdogService.startWatchdog();
		// SmtpServer cannot be reliably stopped and restarted so resort to some trickery
		int previousReceivedEmailCount = smtpServer.getReceivedEmailSize();
		Thread.sleep(1000);
		watchdogService.dismissWatchdog();
		watchdogService.alarmWatchdog();
		assertEquals(previousReceivedEmailCount, smtpServer.getReceivedEmailSize());
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
