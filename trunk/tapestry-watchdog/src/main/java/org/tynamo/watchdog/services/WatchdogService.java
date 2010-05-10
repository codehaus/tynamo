package org.tynamo.watchdog.services;

import java.io.IOException;
import java.net.URISyntaxException;

public interface WatchdogService {

	public abstract void startWatchdog() throws IOException, URISyntaxException;

	public abstract void dismissWatchdog() throws IOException;

	public abstract void alarmWatchdog() throws IOException;

}