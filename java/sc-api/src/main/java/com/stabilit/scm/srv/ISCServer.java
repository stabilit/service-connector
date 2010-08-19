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
	 * @param serviceName
	 *            the service name
	 * @param scCallback
	 *            the sc callback
	 * @throws Exception
	 *             the exception
	 * @throws InvalidParameterException
	 *             serviceName unset, callback unset
	 */
	public abstract void registerService(String serviceName, ISCServerCallback scCallback) throws Exception;

	/**
	 * Deregister service from SC.
	 * 
	 * @param serviceName
	 *            the service name
	 * @throws Exception
	 *             the exception
	 * @throws InvalidParameterException
	 *             serviceName unset
	 */
	public abstract void deregisterService(String serviceName) throws Exception;

	/**
	 * Sets the max sessions server can handle.
	 * 
	 * @param maxSessions
	 *            the new max sessions
	 * @throws InvalidParameterException
	 *             max session is smaller than one
	 */
	public abstract void setMaxSessions(int maxSessions);

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
	 * Sets the SC port.
	 * 
	 * @param scPort
	 *            the new SC port
	 * @throws InvalidParameterException
	 *             port is not within limits 1 to 0xFFFF
	 */
	public abstract void setSCPort(int scPort);

	/**
	 * Gets the host of the SC.
	 * 
	 * @return the SC host
	 */
	public abstract String getSCHost();

	/**
	 * Sets the SC host.
	 * 
	 * @param scHost
	 *            the new SC host
	 * @throws InvalidParameterException
	 *             host unset
	 */
	public abstract void setSCHost(String scHost);

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
	 * @throws Exception
	 *             the exception
	 * @throws InvalidParameterException
	 *             port is not within limits 1 to 0xFFFF, host unset
	 */
	public abstract void startServer(String host, int port) throws Exception;

	/**
	 * Start server.
	 * 
	 * @param host
	 *            the host to bind the listener
	 * @param port
	 *            the port to bin the listener
	 * @throws Exception
	 *             the exception
	 * @throws InvalidParameterException
	 *             port is not within limits 1 to 0xFFFF, host unset<br>
	 *             keepAliveIntervalInSeconds not within limits 1 to 3600
	 */
	public abstract void startServer(String host, int port, int keepAliveIntervalInSeconds) throws Exception;

	/**
	 * Checks if is started.
	 * 
	 * @return true, if is started
	 */
	public abstract boolean isStarted();

	/** {@inheritDoc} */
	@Override
	public abstract int getKeepAliveIntervalInSeconds();
}
