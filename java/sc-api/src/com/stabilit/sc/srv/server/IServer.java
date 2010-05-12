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
package com.stabilit.sc.srv.server;

import com.stabilit.sc.factory.IFactoryable;
import com.stabilit.sc.srv.config.IServerConfigItem;
import com.stabilit.sc.srv.ctx.IServerContext;

/**
 * The Interface IServer.
 * 
 * @author JTraber
 */
public interface IServer extends IFactoryable {

	/**
	 * Gets the server context.
	 * 
	 * @return the server context
	 */
	public IServerContext getServerContext();

	/**
	 * Sets the server configuration.
	 * 
	 * @param serverConfig
	 *            the new server configuration
	 */
	public void setServerConfig(IServerConfigItem serverConfig);

	/**
	 * Creates the.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	public void create() throws Exception;

	/**
	 * Run async. Starts server in another thread.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	public void runAsync() throws Exception;

	/**
	 * Run sync. Starts server in incoming thread.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	public void runSync() throws Exception;

	/**
	 * Gets the server configuration.
	 * 
	 * @return the server configuration
	 */
	public IServerConfigItem getServerConfig();

	/**
	 * Destroys server.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	public void destroy() throws Exception;
}
