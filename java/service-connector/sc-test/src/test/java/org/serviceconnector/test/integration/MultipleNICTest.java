/*-----------------------------------------------------------------------------*
 *                                                                             *
 *       Copyright © 2010 STABILIT Informatik AG, Switzerland                  *
 *                                                                             *
 *  Licensed under the Apache License, Version 2.0 (the "License");            *
 *  you may not use this file except in compliance with the License.           *
 *  You may obtain a copy of the License at                                    *
 *                                                                             *
 *  http://www.apache.org/licenses/LICENSE-2.0                                 *
 *                                                                             *
 *  Unless required by applicable law or agreed to in writing, software        *
 *  distributed under the License is distributed on an "AS IS" BASIS,          *
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   *
 *  See the License for the specific language governing permissions and        *
 *  limitations under the License.                                             *
 *-----------------------------------------------------------------------------*/
package org.serviceconnector.test.integration;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.Enumeration;

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

import junit.framework.Assert;

public class MultipleNICTest extends IntegrationSuperTest {

	@Override
	@Before
	public void beforeOneTest() throws Exception {
		AppContext.init();
		testLogger.info(">> " + name.getMethodName() + " <<");
		threadCount = Thread.activeCount();
		scCtx = ctrl.startSC(TestConstants.SC0, TestConstants.logbackSC0, TestConstants.SCNoInterfacesProperties);
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
					IRequester req = new Requester(new RemoteNodeConfiguration(TestConstants.RemoteNodeName, TestConstants.HOST, TestConstants.PORT_SC0_HTTP,
							ConnectionType.NETTY_HTTP.getValue(), 0, 0, 1));
					SCMPAttachCall attachCall = new SCMPAttachCall(req);
					attachCall.invoke(cbk, 1000);
					TestUtil.checkReply(cbk.getMessageSync(1000));

					SCMPDetachCall detachCall = new SCMPDetachCall(req);
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
