/*-----------------------------------------------------------------------------*
 *                                                                             *
 *       Copyright � 2010 STABILIT Informatik AG, Switzerland                  *
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
package com.stabilit.scm.common.net.req;

import com.stabilit.scm.common.conf.IRequesterConfigItem;
import com.stabilit.scm.common.conf.RequeserConfig;
import com.stabilit.scm.common.factory.Factory;
import com.stabilit.scm.common.factory.IFactoryable;

/**
 * A factory for creating Requester objects.
 */
public class RequesterFactory extends Factory {

	/**
	 * Instantiates a new requester factory.
	 */
	public RequesterFactory() {
		Requester requester = new Requester();
		this.factoryMap.put(DEFAULT, requester);
	}

	/**
	 * New instance.
	 * 
	 * @param clientConfig
	 *            the client configuration
	 * @return the requester
	 */
	public IRequester newInstance(IRequesterConfigItem clientConfig) {
		IFactoryable factoryInstance = this.newInstance();
		IRequester requester = (IRequester) factoryInstance;
		requester.setRequesterConfig(clientConfig);
		return requester;
	}

	/**
	 * New instance.
	 * 
	 * @param host
	 *            the host
	 * @param port
	 *            the port
	 * @param config
	 *            the configuration
	 * @param numberOfThreads
	 *            the number of threads
	 * @return the requester
	 */
	public IRequester newInstance(String host, int port, String config, int numberOfThreads) {
		IFactoryable factoryInstance = this.newInstance();
		IRequester requester = (IRequester) factoryInstance;
		IRequesterConfigItem clientConfigItem = new RequeserConfig().new RequesterConfigItem(host, port, config, numberOfThreads);
		requester.setRequesterConfig(clientConfigItem);
		return requester;
	}
}
