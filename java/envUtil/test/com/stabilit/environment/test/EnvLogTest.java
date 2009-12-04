package com.stabilit.environment.test;

import org.junit.Test;

import com.stabilit.environment.log.EnvLog;

public class EnvLogTest {

	@Test
	public void createLogFile() {
		EnvLog logFile = new EnvLog(
				"C:\\stabilit\\projects\\EUREX\\SC\\dev\\eclipse_workspace\\envUtil\\log\\envLog.txt");
		logFile.createLogFile();
	}
}
