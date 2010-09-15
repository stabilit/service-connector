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
package com.stabilit.sc.common.net;

import com.stabilit.sc.common.factory.IFactoryable;

/**
 * The Interface ICommunicationPoint abstracts any communication point (server, client).
 */
public interface ICommunicationPoint extends IFactoryable {

	/**
	 * Sets the host.
	 * 
	 * @param host
	 *            the host
	 */
	public void setHost(String host);

	/**
	 * Sets the port.
	 * 
	 * @param port
	 *            the port
	 */
	public void setPort(int port);
}
