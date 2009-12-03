package com.stabilit.environment.test;

import org.junit.Test;

import com.stabilit.environment.log.EnvLogFile;

public class EnvLogFileTest {

	@Test
	public void createLogFile() {
		EnvLogFile logFile = new EnvLogFile(
				"C:\\stabilit\\projects\\EUREX\\SC\\dev\\eclipse_workspace\\envUtil\\log\\envLog.txt");
		logFile.createLogFile();
	}
}
