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
package com.stabilit.sc.srv.client;

import com.stabilit.sc.cln.client.IClient;
import com.stabilit.sc.cln.config.ClientConfig;
import com.stabilit.sc.cln.config.IClientConfigItem;
import com.stabilit.sc.factory.Factory;
import com.stabilit.sc.factory.IFactoryable;

/**
 * A factory for creating SCClient objects. Provides access to concrete instances of SC clients.
 * 
 * @author JTraber
 */
public class SCClientFactory extends Factory {

	/**
	 * Instantiates a new SCClientFactory.
	 */
	public SCClientFactory() {
		IClient client = new SCClient();
		this.factoryMap.put(DEFAULT, client);
	}

	/**
	 * New instance.
	 * 
	 * @param host
	 *            the host
	 * @param port
	 *            the port
	 * @param con
	 *            the connection defines concrete client implementation
	 * @return the i client
	 */
	public IClient newInstance(String host, int port, String con) {
		IFactoryable factoryInstance = this.newInstance();
		IClient client = (IClient) factoryInstance;
		IClientConfigItem clientConfigItem = new ClientConfig().new ClientConfigItem(host, port, con);
		client.setClientConfig(clientConfigItem);
		return client;
	}
}
