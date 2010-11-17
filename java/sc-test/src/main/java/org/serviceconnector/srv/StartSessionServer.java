package org.serviceconnector.srv;

import java.io.File;
import java.io.FileWriter;

import org.apache.log4j.Logger;
import org.serviceconnector.api.SCMessage;
import org.serviceconnector.api.SCMessageFault;
import org.serviceconnector.api.srv.SCSessionServer;
import org.serviceconnector.api.srv.SCSessionServerCallback;
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.ctrl.util.ProcessesController;
import org.serviceconnector.ctrl.util.TestConstants;
import org.serviceconnector.ctrl.util.ThreadSafeCounter;

public class StartSessionServer {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(StartSessionServer.class);

	private SCSessionServer scSrv = null;
	private String startFile = null;
	private String[] serviceNames;
	private int port = TestConstants.PORT_TCP;
	private int listenerPort = TestConstants.PORT_LISTENER;
	private int maxCons = 10;
	private ThreadSafeCounter ctr;

	public static void main(String[] args) throws Exception {
		StartSessionServer sessionServer = new StartSessionServer();
		sessionServer.runSessionServer(args);
	}

	public void runSessionServer(String[] args) {
		try {
			this.scSrv = new SCSessionServer();
			try {
				this.listenerPort = Integer.parseInt(args[0]);
				this.port = Integer.parseInt(args[1]);
				this.maxCons = Integer.parseInt(args[2]);
				this.startFile = args[3];
				this.serviceNames = new String[args.length - 4];
				System.arraycopy(args, 4, serviceNames, 0, args.length - 4);
			} catch (Exception e) {
				logger.error("incorrect parameters", e);
				shutdown();
			}
			ctr = new ThreadSafeCounter();

			// connect to SC as server
			this.scSrv.setImmediateConnect(true);
			this.scSrv.startListener(TestConstants.HOST, listenerPort, 0);

			SrvCallback srvCallback = new SrvCallback(new SessionServerContext());

			for (int i = 0; i < serviceNames.length; i++) {
				this.scSrv.registerServer(TestConstants.HOST, port, serviceNames[i], 1000, maxCons, srvCallback);
			}

			// for testing whether the server already started
			new ProcessesController().createFile(startFile);

			String processName = java.lang.management.ManagementFactory.getRuntimeMXBean().getName();
			long pid = Long.parseLong(processName.split("@")[0]);
			FileWriter fw = null;
			try {
				File pidFile = new File(startFile);
				fw = new FileWriter(pidFile);
				fw.write("pid: " + pid);
				fw.flush();
				fw.close();
			} finally {
				if (fw != null) {
					fw.close();
				}
			}

		} catch (Exception e) {
			logger.error("runSessionServer", e);
			this.shutdown();
		}
	}

	private void shutdown() {
		try {
			for (int i = 0; i < serviceNames.length; i++) {
				this.scSrv.deregisterServer(serviceNames[i]);
			}
		} catch (Exception e) {
			this.scSrv = null;
		}
	}

	class SrvCallback extends SCSessionServerCallback {

		private SessionServerContext outerContext;

		public SrvCallback(SessionServerContext context) {
			this.outerContext = context;
		}

		@Override
		public SCMessage createSession(SCMessage message, int operationTimeoutInMillis) {
			logger.info("SessionServer.SrvCallback.createSession()\n" + message.getData());
			if (message.getData() != null && message.getData() instanceof String) {
				String dataString = (String) message.getData();
				if (dataString.equals("reject")) {
					SCMessageFault response = new SCMessageFault();
					response.setCompressed(message.isCompressed());
					response.setData(message.getData());
					response.setMessageInfo(message.getMessageInfo());
					try {
						response.setAppErrorCode(0);
						response.setAppErrorText("\"This is the app error text\"");
					} catch (SCMPValidatorException e) {
						logger.error("rejecting create session", e);
					}
					logger.info("rejecting session");
					return response;
				}
			}
			return message;
		}

		@Override
		public void deleteSession(SCMessage message, int operationTimeoutInMillis) {
			logger.trace("SessionServer.SrvCallback.deleteSession()");
		}

		@Override
		public void abortSession(SCMessage message, int operationTimeoutInMillis) {
			logger.trace("SessionServer.SrvCallback.abortSession()");
		}

		@Override
		public SCMessage execute(SCMessage request, int operationTimeoutInMillis) {
			ctr.increment();

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
					} else if (dataString.equals("executed")) {
						ctr.decrement();
						return new SCMessage(String.valueOf(ctr.value()));
					} else if (dataString.startsWith("timeout")) {
						int millis = Integer.parseInt(dataString.split(" ")[1]);
						try {
							logger.info("Sleeping " + dataString.split(" ")[1] + "ms in order to timeout.");
							Thread.sleep(millis);
						} catch (InterruptedException e) {
							logger.error("sleep in execute", e);
						}
					} else if (dataString.startsWith("register")) {
						String serviceName = dataString.split(" ")[1];
						boolean alreadyPresentService = false;
						for (int i = 0; i < serviceNames.length; i++) {
							if (serviceName.equals(serviceNames[i])) {
								alreadyPresentService = true;
								break;
							}
						}
						if (!alreadyPresentService) {
							if (!scSrv.isRegistered(serviceName)) {
								try {
									scSrv.registerServer(TestConstants.HOST, port, serviceName, 1000, maxCons,
											new SrvCallback(new SessionServerContext()));
									String[] services = new String[serviceNames.length + 1];
									System.arraycopy(serviceNames, 0, services, 0, serviceNames.length);
									services[serviceNames.length] = serviceName;
									serviceNames = services;
								} catch (Exception e) {
									logger.error("register server " + serviceName, e);
								}
							}
						}
					} else if (dataString.startsWith("deregister")) {
						String serviceName = dataString.split(" ")[1];

						if (scSrv.isRegistered(serviceName)) {
							try {
								scSrv.deregisterServer(serviceName);
								String[] services = new String[serviceNames.length - 1];
								boolean alreadyDeleted = false;
								for (int i = 0; i < serviceNames.length; i++) {
									if (serviceName.equals(serviceNames[i])) {
										alreadyDeleted = true;
									} else if (alreadyDeleted) {
										services[i - 1] = serviceNames[i];
									} else {
										services[i] = serviceNames[i];
									}
								}
								serviceNames = services;
							} catch (Exception e) {
								logger.error("deregister server " + serviceName, e);
							}
						}
					}
				}
			}
			return request;
		}
	}

	private class SessionServerContext {
		public SCSessionServer getServer() {
			return scSrv;
		}
	}

	private class KillThread extends Thread {

		private SCSessionServer server;

		public KillThread(SCSessionServer server) {
			this.server = server;
		}

		@Override
		public void run() {
			// sleep before killing the server
			try {
				Thread.sleep(100);
				for (int i = 0; i < serviceNames.length; i++) {
					this.server.deregisterServer(serviceNames[i]);
				}
				this.server.destroy();
			} catch (Exception e) {
				logger.error("run", e);
			}
		}
	}
}
