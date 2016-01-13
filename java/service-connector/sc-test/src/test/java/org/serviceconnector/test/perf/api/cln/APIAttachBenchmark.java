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
package org.serviceconnector.test.perf.api.cln;

import org.junit.Assert;
import org.junit.Test;
import org.serviceconnector.TestConstants;
import org.serviceconnector.api.cln.SCClient;
import org.serviceconnector.net.ConnectionType;
import org.serviceconnector.test.perf.api.APIPerfSuperClientTest;

public class APIAttachBenchmark extends APIPerfSuperClientTest{

	/**
	 * Description: Attach/detach 10000 times to SC on localhost and tcp-connection type. Measure performance <br>
	 * Expectation: Performance better than 100 cycles/sec.
	 */
	@Test
	public void t_10000_tcp() throws Exception {
		client = new SCClient(TestConstants.HOST, TestConstants.PORT_SC0_TCP, ConnectionType.NETTY_TCP);
		int nr = 10000;
		int sleep = 0;
		long start = System.currentTimeMillis();
		for (int i = 0; i < nr; i++) {
			if (((i + 1) % 200) == 0)
				testLogger.info("Attach/detach nr. " + (i + 1) + "...");
			client.attach();
			Assert.assertEquals("Client is attached", true, client.isAttached());
			if (sleep > 0)
				Thread.sleep(sleep);
			client.detach();
			Assert.assertEquals("Client is detached", false, client.isAttached());
		}
		long stop = System.currentTimeMillis();
		long perf = nr * 1000 / (stop - start);
		testLogger.info(nr + " attach/detach performance=" + perf + " cycles/sec.");
		Assert.assertEquals(true, perf > 50);
	}

	/**
	 * Description: Attach/defach 10000 times to SC on localhost and http-connection type. Measure performance <br>
	 * Expectation: Performance better than 100 cycles/sec.
	 */
	@Test
	public void t_10000_http() throws Exception {
		client = new SCClient(TestConstants.HOST, TestConstants.PORT_SC0_HTTP, ConnectionType.NETTY_HTTP);
		int nr = 10000;
		int sleep = 0;
		long start = System.currentTimeMillis();
		for (int i = 0; i < nr; i++) {
			if (((i + 1) % 200) == 0)
				testLogger.info("Attach/detach nr. " + (i + 1) + "...");
			client.attach();
			Assert.assertEquals("Client is attached", true, client.isAttached());
			if (sleep > 0)
				Thread.sleep(sleep);
			client.detach();
			Assert.assertEquals("Client is detached", false, client.isAttached());
		}
		long stop = System.currentTimeMillis();
		long perf = nr * 1000 / (stop - start);
		testLogger.info(nr + " attach/detach performance=" + perf + " cycles/sec.");
		Assert.assertEquals(true, perf > 50);
	}


	/**
	 * Description: Attach 5000 clients then detach them all.<br>
	 * Expectation:	All clients are detached.
	 */
	@Test
	public void t_5000_clients_http() throws Exception {
		int nr = 5000;
		SCClient[] clients = new SCClient[nr];
		// create clients
		for (int i= 0; i < nr; i++) {
			clients[i] = new SCClient(TestConstants.HOST, TestConstants.PORT_SC0_HTTP, ConnectionType.NETTY_HTTP);
		}
		//attach
		long start = System.currentTimeMillis();
		for (int i= 0; i < nr; i++) {
			if (((i+1) % 200) == 0) testLogger.info("Attaching client nr. " + (i+1) );
			clients[i].attach();
			Assert.assertEquals("Client is not attached", true, clients[i].isAttached());
		}
		//detach
		for (int i = 0; i < nr; i++) {
			if (((i+1) % 200) == 0) testLogger.info("Detaching client nr. " + (i+1) + "...");
			clients[i].detach();
			Assert.assertEquals("Client is attached", false, clients[i].isAttached());
		}
		long stop = System.currentTimeMillis();
		long perf = nr * 1000 / (stop - start);
		testLogger.info(nr + " attach/detach performance=" + perf + " cycles/sec.");
		Assert.assertEquals(true, perf > 100);
		clients = null;
	}

	/**
	 * Description: Attach 5000 clients then detach them all.<br>
	 * Expectation:	All clients are detached.
	 */
	@Test
	public void t_5000_clients_tcp() throws Exception {
		int nr = 5000;
		SCClient[] clients = new SCClient[nr];
		// create clients
		for (int i= 0; i < nr; i++) {
			clients[i] = new SCClient(TestConstants.HOST, TestConstants.PORT_SC0_TCP, ConnectionType.NETTY_TCP);
		}
		//attach
		long start = System.currentTimeMillis();
		for (int i= 0; i < nr; i++) {
			if (((i+1) % 200) == 0) testLogger.info("Attaching client nr. " + (i+1) );
			clients[i].attach();
			Assert.assertEquals("Client is not attached", true, clients[i].isAttached());
		}
		//detach
		for (int i = 0; i < nr; i++) {
			if (((i+1) % 200) == 0) testLogger.info("Detaching client nr. " + (i+1) + "...");
			clients[i].detach();
			Assert.assertEquals("Client is attached", false, clients[i].isAttached());
		}
		long stop = System.currentTimeMillis();
		long perf = nr * 1000 / (stop - start);
		testLogger.info(nr + "attach/detach performance=" + perf + " cycles/sec.");
		Assert.assertEquals(true, perf > 100);
		clients = null;
	}
}
