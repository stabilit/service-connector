/*
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
 */
package org.serviceconnector.test.perf.api.srv;

import org.junit.Assert;
import org.junit.Test;
import org.serviceconnector.TestConstants;
import org.serviceconnector.api.SCPublishMessage;
import org.serviceconnector.api.srv.SCPublishServerCallback;
import org.serviceconnector.api.srv.SCServer;
import org.serviceconnector.net.ConnectionType;
import org.serviceconnector.test.perf.api.APIPerfSuperServerTest;

public class APIPublishBenchmark extends APIPerfSuperServerTest {

	/**
	 * Description:	publish 100000 compressed messages à 128 bytes to the server.<br>
	 * Expectation:	passes
	 */
	@Test
	public void publish_100000_msg_compressed() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_SC_TCP, TestConstants.PORT_PUB_SRV_TCP, ConnectionType.NETTY_TCP); 
		server.startListener();
		publishServer = server.newPublishServer(TestConstants.pubServiceName1);
		SCPublishServerCallback cbk = new PubSrvCallback(publishServer);
		publishServer.register(10, 2, cbk);
		SCPublishMessage publishMessage = new SCPublishMessage(new byte[128]);
		publishMessage.setMask(TestConstants.mask);
		publishMessage.setCompressed(true);
		int nrMessages = 100000;
		long start = System.currentTimeMillis();
		long startPart = System.currentTimeMillis();
		long stopPart = 0;
		for (int i = 0; i < nrMessages; i++) {
			if (((i+1) % 1000) == 0) {
				stopPart = System.currentTimeMillis();
				testLogger.info("Publishing message nr. " + (i+1) + "... "+(1000000 / (stopPart - startPart))+ " msg/sec.");
				startPart = System.currentTimeMillis();
			}
			publishServer.publish(publishMessage);
		}
		long stop = System.currentTimeMillis();
		long perf = nrMessages * 1000 / (stop - start);
		testLogger.info(nrMessages + "msg à 128 byte performance : " + perf + " msg/sec.");
		Assert.assertEquals("Performence not fast enough, only"+ perf + " msg/sec.", true, perf > 1000);
	}

	/**
	 * Description:	publish 100000 uncompressed messages à 128 bytes to the server.<br>
	 * Expectation:	passes
	 */
	@Test
	public void publish_100000_msg_uncompressed() throws Exception {
		server = new SCServer(TestConstants.HOST, TestConstants.PORT_SC_TCP, TestConstants.PORT_PUB_SRV_TCP, ConnectionType.NETTY_TCP); 
		server.startListener();
		publishServer = server.newPublishServer(TestConstants.pubServiceName1);
		SCPublishServerCallback cbk = new PubSrvCallback(publishServer);
		publishServer.register(10, 2, cbk);
		SCPublishMessage publishMessage = new SCPublishMessage(new byte[128]);
		publishMessage.setMask(TestConstants.mask);
		publishMessage.setCompressed(false);
		int nrMessages = 100000;
		long start = System.currentTimeMillis();
		long startPart = System.currentTimeMillis();
		long stopPart = 0;
		for (int i = 0; i < nrMessages; i++) {
			if (((i+1) % 1000) == 0) {
				stopPart = System.currentTimeMillis();
				testLogger.info("Publishing message nr. " + (i+1) + "... "+(1000000 / (stopPart - startPart))+ " msg/sec.");
				startPart = System.currentTimeMillis();
			}
			publishServer.publish(publishMessage);
		}
		long stop = System.currentTimeMillis();
		long perf = nrMessages * 1000 / (stop - start);
		testLogger.info(nrMessages + "msg à 128 byte performance : " + perf + " msg/sec.");
		Assert.assertEquals("Performence not fast enough, only"+ perf + " msg/sec.", true, perf > 1200);
	}
}
