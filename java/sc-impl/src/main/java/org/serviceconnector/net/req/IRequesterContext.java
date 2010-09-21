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
package org.serviceconnector.net.req;

import org.serviceconnector.ctx.IContext;
import org.serviceconnector.scmp.SCMPMessageId;


/**
 * The Interface IRequesterContext. Interface for requester context. Needs to have access to connectionPool and current
 * messagedId.
 * 
 * @author JTraber
 */
public interface IRequesterContext extends IContext {

	/**
	 * Gets the connection pool.
	 * 
	 * @return the connection pool
	 */
	public abstract IConnectionPool getConnectionPool();

	/**
	 * Gets the message id.
	 * 
	 * @return the message id
	 */
	public abstract SCMPMessageId getSCMPMessageId();
}