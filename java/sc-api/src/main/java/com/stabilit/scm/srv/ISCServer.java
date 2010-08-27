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
	 * @param scCallback
	 *            the sc callback
	 * @throws Exception
	 *             the exception
	 * @throws InvalidParameterException
	 *             port is not within limits 0 to 0xFFFF, host unset
	 */
	public abstract void registerService(String scHost, int scPort, String serviceName, int maxSessions,
			int maxConnections, ISCServerCallback scCallback) throws Exception;

	/**
	 * Deregister service from SC.
	 * 
	 * @param serviceName
	 *            the service name
	 * @throws Exception
	 *             the exception
	 */
	public abstract void deregisterService(String serviceName) throws Exception;

	/**
	 * Gets the max sessions.
	 * 
	 * @param serviceName
	 *            the service name
	 * @return the max sessions
	 */
	public abstract int getMaxSessions(String serviceName);

	/**
	 * Gets the max connections.
	 * 
	 * @param serviceName
	 *            the service name
	 * @return the max connections
	 */
	public abstract int getMaxConnections(String serviceName);

	/**
	 * Gets the port of SC.
	 * 
	 * @param serviceName
	 *            the service name
	 * @return the SC port
	 */
	public abstract int getSCPort(String serviceName);

	/**
	 * Gets the host of the SC.
	 * 
	 * @param serviceName
	 *            the service name
	 * @return the SC host
	 */
	public abstract String getSCHost(String serviceName);

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
	 * @param keepAliveIntervalInSeconds
	 *            the keep alive interval in seconds
	 * @throws Exception
	 *             the exception
	 * @throws InvalidParameterException
	 *             port is not within limits 0 to 0xFFFF, host unset<br>
	 *             keepAliveIntervalInSeconds not within limits 0 to 3600
	 */
	public abstract void startListener(String host, int port, int keepAliveIntervalInSeconds) throws Exception;

	/**
	 * Checks if is listening.
	 * 
	 * @return true, if is listening
	 */
	public abstract boolean isListening();

	/**
	 * Stop listening.
	 */
	public abstract void destroyServer();

	/**
	 * Checks if is registered.
	 * 
	 * @return true, if is registered
	 */
	public abstract boolean isRegistered(String serviceName);

	/** {@inheritDoc} */
	@Override
	public abstract int getKeepAliveIntervalInSeconds();
}
