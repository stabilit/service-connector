package vmstarters;

import com.stabilit.scm.common.service.ISCMessage;
import com.stabilit.scm.srv.ISCServer;
import com.stabilit.scm.srv.ISCSessionServerCallback;
import com.stabilit.scm.srv.SCServer;

public class StartSCServer {

	private ISCServer scSrv = null;
	private String serviceName;

	public static void main(String[] args) throws Exception {
		StartSCServer sessionServer = new StartSCServer();
		sessionServer.runSessionServer();
	}

	public void runSessionServer() {
		this.scSrv = new SCServer();
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

	class SrvCallback implements ISCSessionServerCallback {

		private SessionServerContext outerContext;

		public SrvCallback(SessionServerContext context) {
			this.outerContext = context;
		}

		@Override
		public ISCMessage createSession(ISCMessage message) {
			System.out.println("SessionServer.SrvCallback.createSession()");
			return message;
		}

		@Override
		public void deleteSession(ISCMessage message) {
			System.out.println("SessionServer.SrvCallback.deleteSession()");
		}

		@Override
		public void abortSession(ISCMessage message) {
			System.out.println("SessionServer.SrvCallback.abortSession()");
		}

		@Override
		public ISCMessage execute(ISCMessage request) {
			Object data = request.getData();
			// watch out for kill server message
			if (data.getClass() == String.class) {
				String dataString = (String) data;
				if (dataString.equals("kill server")) {
					try {
						KillThread kill = new KillThread(this.outerContext.getServer());
						kill.start();
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
						e.printStackTrace();
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
			// sleep for 2 seconds before killing the server
			try {
				Thread.sleep(2000);
				this.server.deregisterService(serviceName);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
