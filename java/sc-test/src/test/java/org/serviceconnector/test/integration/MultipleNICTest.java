package org.serviceconnector.test.integration;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.Enumeration;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.serviceconnector.TestCallback;
import org.serviceconnector.TestConstants;
import org.serviceconnector.TestUtil;
import org.serviceconnector.call.SCMPAttachCall;
import org.serviceconnector.call.SCMPDetachCall;
import org.serviceconnector.conf.RemoteNodeConfiguration;
import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.net.ConnectionType;
import org.serviceconnector.net.req.IRequester;
import org.serviceconnector.net.req.Requester;

public class MultipleNICTest extends IntegrationSuperTest {

	@Before
	public void beforeOneTest() throws Exception {
		AppContext.init();
		testLogger.info(">> " + name.getMethodName() + " <<");
		threadCount = Thread.activeCount();
		scCtx = ctrl.startSC(TestConstants.SC0, TestConstants.log4jSC0Properties, TestConstants.SCNoInterfacesProperties);
	}
	
	/**
	 * Description: Connects to all available NIC on the current PC<br>
	 * Expectation: passes
	 */
	@Test
	public void t01_ConnectToMultipleNIC() throws Exception {
		Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
		TestCallback cbk = new TestCallback();

		for (NetworkInterface netint : Collections.list(nets)) {
			Enumeration<InetAddress> inetAdresses = netint.getInetAddresses();
			for (InetAddress inetAddress : Collections.list(inetAdresses)) {
				try {
					IRequester req = new Requester(new RemoteNodeConfiguration(TestConstants.RemoteNodeName, TestConstants.HOST,
							TestConstants.PORT_SC_HTTP, ConnectionType.NETTY_HTTP.getValue(), 0, 1));
					SCMPAttachCall attachCall = new SCMPAttachCall(req);
					attachCall.invoke(cbk, 1000);
					TestUtil.checkReply(cbk.getMessageSync(1000));

					SCMPDetachCall detachCall = new SCMPDetachCall(req);
					detachCall.invoke(cbk, 1000);
					TestUtil.checkReply(cbk.getMessageSync(1000));
					req.destroy();
				} catch (Exception e) {
					Assert.fail("Connection to NIC : " + inetAddress.getHostAddress() + " failed!");  // TODO JOT ##testing läuft bei mir
				}
			}
		}
	}
}
