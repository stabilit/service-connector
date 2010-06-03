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
package com.stabilit.scm.server;

import com.stabilit.scm.factory.Factory;
import com.stabilit.scm.factory.IFactoryable;
import com.stabilit.scm.srv.config.IServerConfigItem;
import com.stabilit.scm.srv.res.IResponder;
import com.stabilit.scm.srv.res.Responder;

/**
 * A factory for creating SCServer objects. Provides access to concrete instances of SC servers.
 * 
 * @author JTraber
 */
public class SCServerFactory extends Factory {

	/**
	 * Instantiates a new SCServerFactory.
	 */
	public SCServerFactory() {
		Responder server = new SCServer();
		this.factoryMap.put(DEFAULT, server);
	}

	/**
	 * New instance.
	 * 
	 * @param serverConfig
	 *            the server configuration
	 * @return the i server
	 */
	public IResponder newInstance(IServerConfigItem serverConfig) {
		IFactoryable factoryInstance = this.newInstance();
		IResponder server = (IResponder) factoryInstance;
		server.setServerConfig(serverConfig);
		return server;
	}
}
