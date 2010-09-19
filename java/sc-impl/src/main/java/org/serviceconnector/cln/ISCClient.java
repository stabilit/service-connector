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
package org.serviceconnector.cln;

import java.security.InvalidParameterException;

import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.sc.service.ISC;
import org.serviceconnector.sc.service.ISCContext;
import org.serviceconnector.sc.service.SCServiceException;


/**
 * The Interface ISCClient. Interface for any kind of client.
 * 
 * @author JTraber
 */
public interface ISCClient extends ISC {

	/**
	 * Attach client to SC.
	 * 
	 * @param host
	 *            the host
	 * @param port
	 *            the port
	 * @throws Exception
	 *             the exception
	 * @throws InvalidParameterException
	 *             scPort is not within limits 0 to 0xFFFF, scHost unset
	 */
	public abstract void attach(String host, int port) throws Exception;

	/**
	 * Attach client to SC.
	 * 
	 * @param host
	 *            the host
	 * @param port
	 *            the port
	 * @param keepAliveIntervalInSeconds
	 *            the keep alive interval in seconds
	 * @throws Exception
	 *             the exception
	 * @throws InvalidParameterException
	 *             port is not within limits 0 to 0xFFFF, host unset<br>
	 *             keepAliveIntervalInSeconds not within limits 0 to 3600
	 */
	public abstract void attach(String host, int port, int keepAliveIntervalInSeconds) throws Exception;

	/**
	 * Checks if client is attached to SC.
	 * 
	 * @return true, if is attached
	 */
	public abstract boolean isAttached();

	/**
	 * Detach from SC.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	public abstract void detach() throws Exception;

	/**
	 * Gets the host.
	 * 
	 * @return the host
	 */
	public abstract String getHost();

	/**
	 * Gets the port.
	 * 
	 * @return the port
	 */
	public abstract int getPort();

	/**
	 * Creates a new file service.
	 * 
	 * @param serviceName
	 *            the service name of the file service to use
	 * @return the file service
	 */
	public abstract IFileService newFileService(String serviceName) throws Exception;

	/**
	 * Creates a new publish service.
	 * 
	 * @param serviceName
	 *            the service name of the publish service to use
	 * @return the publish service
	 */
	public abstract IPublishService newPublishService(String serviceName) throws Exception;

	/**
	 * Creates a new session service.
	 * 
	 * @param serviceName
	 *            the service name of the session service to use
	 * @return the session service
	 */
	public abstract ISessionService newSessionService(String serviceName) throws Exception;

	/**
	 * Sets the max connections. If client is already connected to the SC and max connections is lower than default
	 * value or value set earlier connection pool is not reducing the connections immediately.
	 * 
	 * @param maxConnections
	 *            the new max connections used by connection pool.
	 * @throws InvalidParameterException
	 *             maxConnections smaller one
	 */
	public abstract void setMaxConnections(int maxConnections) throws SCMPValidatorException;

	/**
	 * Enable service on SC.
	 * 
	 * @param serviceName
	 *            the service name
	 */
	public abstract void enableService(String serviceName) throws SCServiceException;

	/**
	 * Disable service on SC.
	 * 
	 * @param serviceName
	 *            the service name
	 */
	public abstract void disableService(String serviceName) throws SCServiceException;

	/**
	 * Checks if service is enabled on SC.
	 * 
	 * @param serviceName
	 *            the service name
	 * @return true, if is service enabled
	 */
	public abstract boolean isServiceEnabled(String serviceName) throws SCServiceException;

	/**
	 * Workload. Returns the number of available and allocated sessions for given service name. e.g 4/2.
	 * 
	 * @param serviceName
	 *            the service name
	 * @return the string
	 * @throws SCServiceException
	 *             the SC service exception
	 */
	public abstract String workload(String serviceName) throws SCServiceException;

	/**
	 * Kill sc.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	public abstract void killSC() throws Exception;

	/**
	 * Gets the max connections.
	 * 
	 * @return the max connections used in pool
	 */
	public abstract int getMaxConnections();

	/**
	 * Gets the context.
	 * 
	 * @return the context
	 */
	public abstract ISCContext getContext();

	/** {@inheritDoc} */
	@Override
	public abstract String getConnectionType();

	/** {@inheritDoc} */
	@Override
	public abstract int getKeepAliveIntervalInSeconds();
}
