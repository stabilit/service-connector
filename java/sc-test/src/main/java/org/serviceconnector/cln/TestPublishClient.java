package org.serviceconnector.cln;

import java.util.Arrays;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class TestPublishClient extends TestAbstractClient {
	
	static {
		TestAbstractClient.logger = Logger.getLogger(TestPublishClient.class);
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
		logger.log(Level.OFF, "TestPublishClient is starting ...");
		for (int i = 0; i < args.length; i++) {
			logger.log(Level.OFF, "args[" + i + "]:" + args[i]);
		}
		TestPublishClient testClient = new TestPublishClient();
		testClient.setClientName(args[0]);
		testClient.setHost(args[1]);
		testClient.setPort(Integer.parseInt(args[2]));
		testClient.setConnectionType(args[3]);
		testClient.setMaxConnections(Integer.parseInt(args[4]));
		testClient.setKeepAliveIntervalSeconds(Integer.parseInt(args[5]));
		testClient.setServiceName(args[6]);
//		testClient.setEchoIntervalInSeconds(Integer.parseInt(args[7]));
//		testClient.setEchoTimeoutInSeconds(Integer.parseInt(args[8]));
		testClient.setMethodsToInvoke(Arrays.asList(args[9].split("\\|")));
		testClient.run();
	}
	
}
