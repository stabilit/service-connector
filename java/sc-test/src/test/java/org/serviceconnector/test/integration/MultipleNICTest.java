package org.serviceconnector.test.integration;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.Enumeration;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.serviceconnector.TestCallback;
import org.serviceconnector.TestConstants;
import org.serviceconnector.TestUtil;
import org.serviceconnector.call.SCMPAttachCall;
import org.serviceconnector.call.SCMPCallFactory;
import org.serviceconnector.call.SCMPDetachCall;
import org.serviceconnector.ctrl.util.ProcessCtx;
import org.serviceconnector.ctrl.util.ProcessesController;
import org.serviceconnector.log.Loggers;
import org.serviceconnector.net.ConnectionType;
import org.serviceconnector.net.req.IRequester;
import org.serviceconnector.net.req.Requester;
import org.serviceconnector.net.req.RequesterContext;

public class MultipleNICTest {

	/** The Constant testLogger. */
	private static final Logger testLogger = Logger.getLogger(Loggers.TEST.getValue());
	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(MultipleNICTest.class);

	private static ProcessesController ctrl;
	private static ProcessCtx scCtx;
	private int threadCount = 0;

	@BeforeClass
	public static void beforeAllTests() throws Exception {
		ctrl = new ProcessesController();
		scCtx = ctrl.startSC(TestConstants.log4jSCProperties, TestConstants.SCNoInterfacesProperties);
	}

	@Before
	public void beforeOneTest() throws Exception {
		threadCount = Thread.activeCount();
	}

	@After
	public void afterOneTest() throws Exception {
		testLogger.info("Number of threads :" + Thread.activeCount() + " created :" + (Thread.activeCount() - threadCount));
	}

	@AfterClass
	public static void afterAllTests() throws Exception {
		try {
			ctrl.stopSC(scCtx);
			scCtx = null;
		} catch (Exception e) {
		}
		ctrl = null;
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
					IRequester req = new Requester(new RequesterContext(inetAddress.getHostAddress(), TestConstants.PORT_SC_HTTP,
							ConnectionType.NETTY_HTTP.getValue(), 0));
					SCMPAttachCall attachCall = (SCMPAttachCall) SCMPCallFactory.ATTACH_CALL.newInstance(req);

					attachCall.invoke(cbk, 1000);
					TestUtil.checkReply(cbk.getMessageSync(1000));

					SCMPDetachCall detachCall = (SCMPDetachCall) SCMPCallFactory.DETACH_CALL.newInstance(req);
					detachCall.invoke(cbk, 1000);
					TestUtil.checkReply(cbk.getMessageSync(1000));
					req.destroy();
				} catch (Exception e) {
					Assert.fail("Connection to NIC : " + inetAddress.getHostAddress() + " failed!");
				}
			}
		}
	}
}
