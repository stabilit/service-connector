package com.stabilit.scm.srv;

import com.stabilit.scm.sc.LoggerConfigurator;
import com.stabilit.scm.srv.ps.PublishServer;
import com.stabilit.scm.srv.rr.SessionServer;

public class SessionAndPublishServer {

	public static void main(String[] args) {
		SessionServer sessionServer = new SessionServer();
		PublishServer publishServer = new PublishServer();
		
		LoggerConfigurator loggerConfigurator = new LoggerConfigurator("log4j");
		loggerConfigurator.addAllLoggers();
		
		sessionServer.runSessionServer();
		publishServer.runPublishServer();
	}
}
