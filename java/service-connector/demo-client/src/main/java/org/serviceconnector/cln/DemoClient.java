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
package org.serviceconnector.cln;

import org.serviceconnector.api.cln.SCMgmtClient;
import org.serviceconnector.net.ConnectionType;

/**
 * The Class DemoClient.
 */
public class DemoClient {

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 * @throws Exception the exception
	 */
	public static void main(String[] args) throws Exception {

		DemoSessionClient demoSessionClient = new DemoSessionClient();
		DemoPublishClient demoPublishClient = new DemoPublishClient();
		// DemoFileClient demoFileClient = new DemoFileClient();

		demoSessionClient.run();
		demoPublishClient.run();
		// demoFileClient.start();
		// sleep to assure deregister from server to SC is done!
		Thread.sleep(500);
		SCMgmtClient client = new SCMgmtClient("localhost", 9000, ConnectionType.NETTY_TCP);
		client.attach();
		client.killSC();
	}
}
