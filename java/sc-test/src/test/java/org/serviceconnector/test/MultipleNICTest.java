package org.serviceconnector.test;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.Enumeration;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;
import org.serviceconnector.Constants;
import org.serviceconnector.SC;
import org.serviceconnector.call.SCMPAttachCall;
import org.serviceconnector.call.SCMPCallFactory;
import org.serviceconnector.call.SCMPDetachCall;
import org.serviceconnector.net.ConnectionType;
import org.serviceconnector.net.connection.ConnectionPool;
import org.serviceconnector.net.req.IRequester;
import org.serviceconnector.net.req.Requester;
import org.serviceconnector.net.req.RequesterContext;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.scmp.SCMPMsgType;
import org.serviceconnector.test.sc.SCTest;
import org.serviceconnector.util.SynchronousCallback;
import org.serviceconnector.util.ValidatorUtility;

public class MultipleNICTest {

	@BeforeClass
	public static void startUp() throws Exception {
		SC.main(new String[] { Constants.CLI_CONFIG_ARG, "sc.properties" });
	}

	@Test
	public void testMultipleNIC() throws Exception {
		Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
		for (NetworkInterface netint : Collections.list(nets)) {
			Enumeration<InetAddress> inetAdresses = netint.getInetAddresses();
			for (InetAddress inetAddress : Collections.list(inetAdresses)) {
				try {
					ConnectionPool cp = new ConnectionPool(inetAddress.getHostAddress(), 7000, ConnectionType.NETTY_HTTP.getValue());

					IRequester req = new Requester(new RequesterContext(cp, null));
					SCMPAttachCall attachCall = (SCMPAttachCall) SCMPCallFactory.ATTACH_CALL.newInstance(req);

					TestCallback callback = new TestCallback();
					attachCall.invoke(callback, 1000);
					SCMPMessage result = callback.getMessageSync();
					SCTest.checkReply(result);
					/*********************************** Verify attach response msg **********************************/
					Assert.assertNull(result.getBody());
					Assert.assertNull(result.getMessageSequenceNr());
					Assert.assertEquals(SCMPMsgType.ATTACH.getValue(), result.getHeader(SCMPHeaderAttributeKey.MSG_TYPE));
					Assert.assertNotNull(ValidatorUtility.validateLocalDateTime(result
							.getHeader(SCMPHeaderAttributeKey.LOCAL_DATE_TIME)));

					SCMPDetachCall detachCall = (SCMPDetachCall) SCMPCallFactory.DETACH_CALL.newInstance(req);
					detachCall.invoke(callback, 1000);
					SCTest.checkReply(callback.getMessageSync());
					cp.destroy();
				} catch (Exception e) {
					// connection failed
					Assert.fail("Connection to NIC : " + inetAddress.getHostAddress() + " failed!");
				}
			}
		}
	}

	private class TestCallback extends SynchronousCallback {
		// nothing to implement in this case - everything is done by super-class
	}
}
