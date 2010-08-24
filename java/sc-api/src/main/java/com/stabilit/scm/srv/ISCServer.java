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
package com.stabilit.scm.srv;

import java.security.InvalidParameterException;

import com.stabilit.scm.common.service.ISC;

/**
 * The Interface ISCServer. Top interface for any kind of server.
 * 
 * @author JTraber
 */
public interface ISCServer extends ISC {

	/**
	 * Register service on SC.
	 * 
	 * @param scHost
	 *            the sc host
	 * @param scPort
	 *            the sc port
	 * @param serviceName
	 *            the service name
	 * @param keepAliveIntervalInSeconds
	 *            the keep alive interval in seconds
	 * @param scCallback
	 *            the sc callback
	 * @throws Exception
	 *             the exception
	 *             @throws InvalidParameterException
	 *             port is not within limits 0 to 0xFFFF, host unset
	 */
	public abstract void registerService(String scHost, int scPort, String serviceName, int keepAliveIntervalInSeconds,
			ISCServerCallback scCallback) throws Exception;

	/**
	 * Deregister service from SC.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	public abstract void deregisterService() throws Exception;

	/**
	 * Gets the max sessions.
	 * 
	 * @return the max sessions
	 */
	public abstract int getMaxSessions();

	/**
	 * Gets the port of SC.
	 * 
	 * @return the SC port
	 */
	public abstract int getSCPort();

	/**
	 * Gets the host of the SC.
	 * 
	 * @return the SC host
	 */
	public abstract String getSCHost();

	/**
	 * Sets the immediate connect. Affects connecting behavior from SC. If immediateConnect is set SC establishes
	 * connection to server at the time registerService is received.
	 * 
	 * @param immediateConnect
	 *            the new immediate connect
	 */
	public abstract void setImmediateConnect(boolean immediateConnect);

	/**
	 * Checks if is immediate connect.
	 * 
	 * @return true, if is immediate connect
	 */
	public abstract boolean isImmediateConnect();

	/**
	 * Start server.
	 * 
	 * @param host
	 *            the host to bind the listener
	 * @param port
	 *            the port to bin the listener
	 * @param maxSessions
	 *            the max sessions
	 * @throws Exception
	 *             the exception
	 * @throws InvalidParameterException
	 *             port is not within limits 0 to 0xFFFF, host unset
	 */
	public abstract void startListener(String host, int port, int maxSessions) throws Exception;

	/**
	 * Checks if is started.
	 * 
	 * @return true, if is started
	 */
	public abstract boolean listening();

	/**
	 * Stop listening.
	 */
	public abstract void stopListening();

	/**
	 * Registered.
	 * 
	 * @return true, if successful
	 */
	public abstract boolean registered();

	/** {@inheritDoc} */
	@Override
	public abstract int getKeepAliveIntervalInSeconds();
}
