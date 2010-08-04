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
package com.stabilit.scm.srv;

import com.stabilit.scm.common.service.ISC;

/**
 * The Interface ISCServer. Top interface for any kind of server.
 * 
 * @author JTraber
 */
public interface ISCServer extends ISC {

	/**
	 * Register service.
	 * 
	 * @param serviceName
	 *            the service name
	 * @param scCallback
	 *            the sc callback
	 * @throws Exception
	 *             the exception
	 */
	public abstract void registerService(String serviceName, ISCServerCallback scCallback) throws Exception;

	/**
	 * Deregister service.
	 * 
	 * @param serviceName
	 *            the service name
	 * @throws Exception
	 *             the exception
	 */
	public abstract void deregisterService(String serviceName) throws Exception;

	/**
	 * Sets the max sessions.
	 * 
	 * @param maxSessions
	 *            the new max sessions
	 */
	public abstract void setMaxSessions(int maxSessions);

	/**
	 * Gets the max sessions.
	 * 
	 * @return the max sessions
	 */
	public abstract int getMaxSessions();

	/**
	 * Sets the keep alive interval.
	 * 
	 * @param keepAliveInterval
	 *            the new keep alive interval
	 */
	public abstract void setKeepAliveInterval(int keepAliveInterval);

	/**
	 * Sets the running port number.
	 * 
	 * @param runningPort
	 *            the new running port number
	 */
	public abstract void setRunningPortNr(int runningPort);

	/**
	 * Sets the immediate connect.
	 * 
	 * @param immediateConnect
	 *            the new immediate connect
	 */
	public abstract void setImmediateConnect(boolean immediateConnect);

	/**
	 * Start server.
	 * 
	 * @param host
	 *            the host
	 * @throws Exception
	 *             the exception
	 */
	public abstract void startServer(String host) throws Exception;
}
