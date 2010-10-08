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

package org.serviceconnector.api.srv;

import java.security.InvalidParameterException;

/**
 * The Interface ISCPublishServer. Top interface for any publish service.
 * 
 * @author JTraber
 */
public interface ISCPublishServer extends ISCSessionServer {

	/**
	 * Publish data.
	 * 
	 * @param serviceName
	 *            the service name
	 * @param mask
	 *            the mask
	 * @param data
	 *            the data
	 * @throws Exception
	 *             the exception
	 */
	public abstract void publish(String serviceName, String mask, Object data) throws Exception;

	/**
	 * Register server for a service.
	 * 
	 * @param scHost
	 *            the sc host
	 * @param scPort
	 *            the sc port
	 * @param serviceName
	 *            the service name
	 * @param scCallback
	 *            the sc callback
	 * @throws Exception
	 *             the exception
	 * @throws InvalidParameterException
	 *             port is not within limits 0 to 0xFFFF, host unset
	 */
	public abstract void registerServer(String scHost, int scPort, String serviceName, int maxSessions,
			int maxConnections, ISCPublishServerCallback scCallback) throws Exception;
}
