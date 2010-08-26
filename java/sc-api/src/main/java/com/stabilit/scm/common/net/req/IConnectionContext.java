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

import com.stabilit.scm.common.ctx.IContext;
import com.stabilit.scm.srv.IIdleCallback;

/**
 * The Interface IConnectionContext. Represents context of a connection.
 * 
 * @author JTraber
 */
public interface IConnectionContext extends IContext {

	/**
	 * Gets the connection.
	 * 
	 * @return the connection
	 */
	public abstract IConnection getConnection();

	/**
	 * Gets the idle timeout.
	 * 
	 * @return the idle timeout
	 */
	public abstract int getIdleTimeout();

	/**
	 * Gets the idle callback.
	 * 
	 * @return the idle callback
	 */
	public abstract IIdleCallback getIdleCallback();
}
