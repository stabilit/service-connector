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
import org.serviceconnector.service.ISC;


/**
 * The Interface IServiceContext. Super interface for a service context.
 */
public interface IServiceContext extends IContext {

	/**
	 * Gets the service connector which uses the service.
	 * 
	 * @return the service connector
	 */
	public abstract ISC getServiceConnector();

	/**
	 * Gets the service of this context.
	 * 
	 * @return the service
	 */
	public abstract Service getService();
}
