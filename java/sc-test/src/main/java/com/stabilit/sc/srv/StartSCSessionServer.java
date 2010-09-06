package com.stabilit.sc.srv;

import org.apache.log4j.Logger;

import com.stabilit.sc.ctrl.util.TestEnvironmentController;
import com.stabilit.scm.common.service.ISCMessage;
import com.stabilit.scm.srv.ISCServer;
import com.stabilit.scm.srv.ISCSessionServerCallback;
import com.stabilit.scm.srv.SCServer;

public class StartSCSessionServer {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(StartSCSessionServer.class);

	private ISCServer scSrv = null;
	private String startFile = null;
	private String serviceName;
	private int port = 9000;
	private int maxCons = 10;

	public static void main(String[] args) throws Exception {
		StartSCSessionServer sessionServer = new StartSCSessionServer();
		try {
			sessionServer.port = Integer.parseInt(args[0]);
			sessionServer.setServiceName(args[1]);
			sessionServer.maxCons = Integer.parseInt(args[2]);
			sessionServer.startFile = args[3];
		} catch (Exception e) {
			logger.error("main", e);
		}
		sessionServer.runSessionServer();
	}

	public void runSessionServer() {
		try {
			this.scSrv = new SCServer();
			// connect to SC as server
			this.scSrv.setImmediateConnect(true);
			this.scSrv.startListener("localhost", 30000, 0);

			SrvCallback srvCallback = new SrvCallback(new SessionServerContext());
			this.scSrv.registerService("localhost", port, serviceName, 1000, getMaxCons(),
					srvCallback);

			//for testing whether the server already started 
			new TestEnvironmentController().createFile(startFile);
			
		} catch (Exception e) {
			logger.error("runSessionServer", e);
			this.shutdown();
		}
	}

	private void shutdown() {
		try {
			this.scSrv.deregisterService(serviceName);
		} catch (Exception e) {
			this.scSrv = null;
		}
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public void setMaxCons(int maxCons) {
		this.maxCons = maxCons;
	}

	public int getMaxCons() {
		return maxCons;
	}

	class SrvCallback implements ISCSessionServerCallback {

		private SessionServerContext outerContext;

		public SrvCallback(SessionServerContext context) {
			this.outerContext = context;
		}

		@Override
		public ISCMessage createSession(ISCMessage message) {
			logger.debug("SessionServer.SrvCallback.createSession()");
			return message;
		}

		@Override
		public void deleteSession(ISCMessage message) {
			logger.debug("SessionServer.SrvCallback.deleteSession()");
		}

		@Override
		public void abortSession(ISCMessage message) {
			logger.debug("SessionServer.SrvCallback.abortSession()");
		}

		@Override
		public ISCMessage execute(ISCMessage request) {
			Object data = request.getData();

			if (data != null) {
				// watch out for kill server message
				if (data.getClass() == String.class) {
					String dataString = (String) data;
					if (dataString.equals("kill server")) {
						try {
							KillThread kill = new KillThread(this.outerContext.getServer());
							kill.start();
						} catch (Exception e) {
							logger.error("execute", e);
						}
					}
				}
			}
			return request;
		}
	}

	private class SessionServerContext {
		public ISCServer getServer() {
			return scSrv;
		}
	}

	private class KillThread extends Thread {

		private ISCServer server;

		public KillThread(ISCServer server) {
			this.server = server;
		}

		@Override
		public void run() {
			// sleep before killing the server
			try {
				Thread.sleep(100);
				this.server.deregisterService(serviceName);
				this.server.destroyServer();
			} catch (Exception e) {
				logger.error("run", e);
			}
		}
	}
}
