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
package com.stabilit.scm.sc.req;

import com.stabilit.scm.common.conf.IRequesterConfigItem;
import com.stabilit.scm.common.conf.RequesterConfig;
import com.stabilit.scm.common.factory.Factory;
import com.stabilit.scm.common.factory.IFactoryable;
import com.stabilit.scm.common.net.req.IRequester;

/**
 * A factory for creating SCRequester objects. Provides access to concrete instances of SC requesters.
 * 
 * @author JTraber
 */
public class SCRequesterFactory extends Factory {

	/**
	 * Instantiates a new SCRequesterFactory.
	 */
	public SCRequesterFactory() {
		IRequester requester = new SCRequester();
		this.baseInstances.put(DEFAULT, requester);
	}

	/**
	 * New instance.
	 * 
	 * @param host
	 *            the host
	 * @param port
	 *            the port
	 * @param connection
	 *            the connection defines concrete client implementation
	 * @param numberOfThreads
	 *            the number of threads
	 * @return the requester
	 */
	public IRequester newInstance(String host, int port, String connection, int numberOfThreads) {
		IFactoryable factoryInstance = this.newInstance();
		IRequester requester = (IRequester) factoryInstance;
		IRequesterConfigItem requesterConfigItem = new RequesterConfig().new RequesterConfigItem(host, port, connection,
				numberOfThreads);
		requester.setRequesterConfig(requesterConfigItem);
		return requester;
	}
}
