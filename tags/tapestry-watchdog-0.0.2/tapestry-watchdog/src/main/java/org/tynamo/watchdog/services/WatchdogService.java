package org.tynamo.watchdog.services;

import java.io.IOException;
import java.net.URISyntaxException;

public interface WatchdogService {

	public void startWatchdog() throws IOException, URISyntaxException, InterruptedException;

	public abstract void dismissWatchdog() throws IOException;

	public abstract void alarmWatchdog() throws IOException;

}