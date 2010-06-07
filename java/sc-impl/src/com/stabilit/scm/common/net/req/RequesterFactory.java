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
package com.stabilit.scm.common.net.req;

import com.stabilit.scm.cln.req.IRequester;
import com.stabilit.scm.common.conf.IRequesterConfigItem;
import com.stabilit.scm.common.conf.RequeserConfig;
import com.stabilit.scm.factory.Factory;
import com.stabilit.scm.factory.IFactoryable;

/**
 * A factory for creating Requester objects.
 */
public class RequesterFactory extends Factory {

	/**
	 * Instantiates a new requester factory.
	 */
	public RequesterFactory() {
		Requester req = new Requester();
		this.factoryMap.put(DEFAULT, req);
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
		IRequester req = (IRequester) factoryInstance;
		req.setRequesterConfig(clientConfig);
		return req;
	}

	/**
	 * New instance.
	 * 
	 * @param host
	 *            the host
	 * @param port
	 *            the port
	 * @param con
	 *            the configuration
	 * @param numberOfThreads
	 *            the number of threads
	 * @return the requester
	 */
	public IRequester newInstance(String host, int port, String con, int numberOfThreads) {
		IFactoryable factoryInstance = this.newInstance();
		IRequester req = (IRequester) factoryInstance;
		IRequesterConfigItem clientConfigItem = new RequeserConfig().new RequesterConfigItem(host, port, con, numberOfThreads);
		req.setRequesterConfig(clientConfigItem);
		return req;
	}
}
