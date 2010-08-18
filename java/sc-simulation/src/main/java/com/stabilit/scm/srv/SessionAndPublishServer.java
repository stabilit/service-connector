package com.stabilit.scm.srv;

import com.stabilit.scm.sc.LoggerConfigurator;

public class SessionAndPublishServer {

	public static void main(String[] args) {
		DemoSessionServer sessionServer = new DemoSessionServer();
		DemoPublishServer publishServer = new DemoPublishServer();
		
		LoggerConfigurator loggerConfigurator = new LoggerConfigurator("log4j");
		loggerConfigurator.addAllLoggers();
		
		sessionServer.runSessionServer();
		publishServer.runPublishServer();
	}
}
