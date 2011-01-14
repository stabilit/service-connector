package org.serviceconnector.cln;

import java.util.Arrays;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.serviceconnector.TestSessionServiceMessageCallback;
import org.serviceconnector.api.SCMessage;
import org.serviceconnector.api.cln.SCSessionService;

public class TestSessionClient extends TestAbstractClient {

	private int echoIntervalInSeconds;
	private int echoTimeoutInSeconds;
	private SCSessionService service;

	static {
		TestAbstractClient.logger = Logger.getLogger(TestSessionClient.class);
	}

	/**
	 * Main method if you like to start in debug mode.
	 * 
	 * @param args
	 *            [0] client name<br>
	 *            [1] SC host<br>
	 *            [2] SC port<br>
	 *            [3] connectionType ("netty.tcp" or "netty.http")<br>
	 *            [4] maxConnections<br>
	 *            [5] keepAliveIntervalSeconds (0 = disabled)<br>
	 *            [6] serviceName<br>
	 *            [7] echoIntervalInSeconds<br>
	 *            [8] echoTimeoutInSeconds<br>
	 *            [9] methodsToInvoke
	 */
	public static void main(String[] args) throws Exception {
		logger.log(Level.OFF, "TestSessionClient is starting ...");
		for (int i = 0; i < args.length; i++) {
			logger.log(Level.OFF, "args[" + i + "]:" + args[i]);
		}
		TestSessionClient testClient = new TestSessionClient();
		testClient.setClientName(args[0]);
		testClient.setHost(args[1]);
		testClient.setPort(Integer.parseInt(args[2]));
		testClient.setConnectionType(args[3]);
		testClient.setMaxConnections(Integer.parseInt(args[4]));
		testClient.setKeepAliveIntervalSeconds(Integer.parseInt(args[5]));
		testClient.setServiceName(args[6]);
		testClient.setEchoIntervalInSeconds(Integer.parseInt(args[7]));
		testClient.setEchoTimeoutInSeconds(Integer.parseInt(args[8]));
		testClient.setMethodsToInvoke(Arrays.asList(args[9].split("\\|")));
		testClient.run();
	}

	public void p_createSession() throws Exception {
		service = client.newSessionService(this.serviceName);
		service.setEchoIntervalInSeconds(this.echoIntervalInSeconds);
		service.setEchoTimeoutInSeconds(this.echoTimeoutInSeconds);
		service.createSession(new SCMessage(), new TestSessionServiceMessageCallback(service));
	}

	public void p_execute1000() throws Exception {
		for (int i = 0; i < 1000; i++) {
			service.execute(new SCMessage());
		}
	}

	public void p_execute100000() throws Exception {
		for (int i = 0; i < 100000; i++) {
			service.execute(new SCMessage());
			if (i % 10000 == 0) {
				logger.log(Level.OFF, this.clientName + " sent message number " + i);
			}
		}
	}

	public void p_deleteSession() throws Exception {
		service.deleteSession();
	}

	public void f_execute1000MessagesAndExit() throws Exception {
		this.p_initAttach();
		this.p_createSession();
		this.p_execute1000();
		this.p_deleteSession();
		this.p_detach();
		this.p_exit();
	}

	public void f_execute100000MessagesAndExit() throws Exception {
		this.p_initAttach();
		this.p_createSession();
		this.p_execute100000();
		this.p_deleteSession();
		this.p_detach();
		this.p_exit();
	}

	public void setEchoIntervalInSeconds(int echoIntervalInSeconds) {
		this.echoIntervalInSeconds = echoIntervalInSeconds;
	}

	public void setEchoTimeoutInSeconds(int echoTimeoutInSeconds) {
		this.echoTimeoutInSeconds = echoTimeoutInSeconds;
	}
}
