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
package com.stabilit.sc.server;

import com.stabilit.sc.factory.IFactoryable;
import com.stabilit.sc.srv.server.Server;

/**
 * The Class SCServer. Defines behavior of server in context of Service Connector.
 * 
 * @author JTraber
 */
public class SCServer extends Server {

	/**
	 * Instantiates a new sC server.
	 */
	public SCServer() {
	}

	/**
	 * Creates an SC server.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Override
	public void create() throws Exception {
		super.create();
	}

	/**
	 * New instance.
	 * 
	 * @return the factoryable
	 */
	@Override
	public IFactoryable newInstance() {
		return new SCServer();
	}
}
