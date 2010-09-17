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
package org.serviceconnector.cln.service;

import org.serviceconnector.ctx.IContext;

/**
 * The Interface IService. Top interface for services.
 * 
 * @author JTraber
 */
public interface IService {

	/**
	 * Gets the context.
	 * 
	 * @return the context
	 */
	public abstract IContext getContext();

	/**
	 * Gets the session id.
	 * 
	 * @return the session id
	 */
	public abstract String getSessionId();
}
