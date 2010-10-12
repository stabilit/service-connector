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
package org.serviceconnector.api.cln;

import java.security.InvalidParameterException;

import org.apache.log4j.Logger;
import org.serviceconnector.Constants;
import org.serviceconnector.call.SCMPAttachCall;
import org.serviceconnector.call.SCMPCallFactory;
import org.serviceconnector.call.SCMPDetachCall;
import org.serviceconnector.call.SCMPInspectCall;
import org.serviceconnector.call.SCMPManageCall;
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.net.connection.ConnectionPool;
import org.serviceconnector.net.req.IRequester;
import org.serviceconnector.net.req.RequesterContext;
import org.serviceconnector.net.req.SCRequester;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.service.SCServiceException;
import org.serviceconnector.service.ServiceState;
import org.serviceconnector.util.ValidatorUtility;

/**
 * The Class SCClient. Client to an SC.
 * 
 * @author JTraber
 */
public class SCClient implements ISCClient {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(SCClient.class);

	/** The host of the SC. */
	private String host;
	/** The port of the SC. */
	private int port;
	/** The max connections to use in pool. */
	private int maxConnections;
	/** The keep alive interval. */
	private int keepAliveIntervalInSeconds;
	/** The connection pool. */
	private ConnectionPool connectionPool;
	/** Identifies low level component to use for communication default for clients is {netty.http}. */
	private String conType;
	/** The requester. */
	private IRequester requester;
	/** The context. */
	private SCContext scContext;

	private boolean attached;

	/**
	 * Instantiates a new SC client.
	 * 
	 * @param connectionType
	 *            the connection type
	 */
	public SCClient() {
		this.host = null;
		this.port = -1;
		this.conType = Constants.DEFAULT_CLIENT_CON;
		this.keepAliveIntervalInSeconds = Constants.DEFAULT_KEEP_ALIVE_INTERVAL;
		this.scContext = new SCContext(this);
		this.attached = false;
		this.maxConnections = Constants.DEFAULT_MAX_CONNECTIONS;
		this.connectionPool = null;
	}

	/** {@inheritDoc} */
	@Override
	public SCContext getSCContext() {
		return this.scContext;
	}

	/** {@inheritDoc} */
	@Override
	public synchronized void attach(String host, int port) throws Exception {
		this.attach(host, port, Constants.DEFAULT_KEEP_ALIVE_INTERVAL);
	}

	/** {@inheritDoc} */
	@Override
	public synchronized void attach(String host, int port, int keepAliveIntervalInSeconds) throws Exception {
		if (this.attached) {
			throw new SCServiceException(
					"already attached before - detach first, attaching in sequence is not allowed.");
		}
		if (host == null) {
			throw new InvalidParameterException("host must be set.");
		}
		ValidatorUtility.validateInt(0, port, 0xFFFF, SCMPError.HV_WRONG_PORTNR);
		ValidatorUtility.validateInt(0, keepAliveIntervalInSeconds, 3600, SCMPError.HV_WRONG_KEEPALIVE_INTERVAL);
		this.port = port;
		this.host = host;
		this.keepAliveIntervalInSeconds = keepAliveIntervalInSeconds;
		this.connectionPool = new ConnectionPool(host, port, this.conType, keepAliveIntervalInSeconds);
		this.connectionPool.setMaxConnections(this.maxConnections);
		// keep always one connection active from client to SC
		this.connectionPool.setMinConnections(1);
		this.scContext.setConnectionPool(this.connectionPool);
		this.requester = new SCRequester(new RequesterContext(this.connectionPool, null));
		SCMPAttachCall attachCall = (SCMPAttachCall) SCMPCallFactory.ATTACH_CALL.newInstance(this.requester);
		ServiceCallback callback = new ServiceCallback(true);
		try {
			attachCall.invoke(callback, Constants.DEFAULT_OPERATION_TIMEOUT_SECONDS * Constants.SEC_TO_MILLISEC_FACTOR);
		} catch (Exception e) {
			this.connectionPool.destroy();
			throw new SCServiceException("attach to " + host + ":" + port + " failed", e);
		}
		SCMPMessage reply = callback.getMessageSync();
		if (reply.isFault()) {
			this.connectionPool.destroy();
			throw new SCServiceException("attach to " + host + ":" + port + " failed : "
					+ reply.getHeader(SCMPHeaderAttributeKey.SC_ERROR_TEXT));

		}
		this.attached = true;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isAttached() {
		return this.attached;
	}

	/** {@inheritDoc} */
	@Override
	public synchronized void detach() throws Exception {
		if (this.attached == false) {
			// detach not possible - client not attached just ignore
			return;
		}
		ServiceCallback callback = new ServiceCallback(true);
		try {
			SCMPDetachCall detachCall = (SCMPDetachCall) SCMPCallFactory.DETACH_CALL.newInstance(this.requester);
			try {
				detachCall.invoke(callback, Constants.DEFAULT_OPERATION_TIMEOUT_SECONDS
						* Constants.SEC_TO_MILLISEC_FACTOR);
			} catch (Exception e) {
				throw new SCServiceException("detach client failed", e);
			}
			SCMPMessage reply = callback.getMessageSync();
			if (reply.isFault()) {
				throw new SCServiceException("detach client failed : "
						+ reply.getHeader(SCMPHeaderAttributeKey.SC_ERROR_TEXT));
			}
		} finally {
			this.attached = false;
			// destroy connection pool
			this.connectionPool.destroy();
		}
	}

	/**
	 * Gets the connection type. Default {netty.http}
	 * 
	 * @return the connection type in use
	 */
	@Override
	public String getConnectionType() {
		return conType;
	}

	/**
	 * Sets the connection type. Should only be used if you really need to change low level technology careful.
	 * 
	 * @param conType
	 *            the new connection type, identifies low level communication technology
	 */
	public void setConnectionType(String conType) {
		this.conType = conType;
	}

	/** {@inheritDoc} */
	@Override
	public String getHost() {
		return host;
	}

	/** {@inheritDoc} */
	@Override
	public int getPort() {
		return port;
	}

	/** {@inheritDoc} */
	@Override
	public int getKeepAliveIntervalInSeconds() {
		return this.keepAliveIntervalInSeconds;
	}

	/** {@inheritDoc} */
	@Override
	public IFileService newFileService(String serviceName) throws Exception {
		if (serviceName == null) {
			throw new InvalidParameterException("service name must be set");
		}
		if (this.attached == false) {
			throw new SCServiceException("newFileService not possible - client not attached.");
		}
		return null;
	}

	/** {@inheritDoc} */
	@Override
	public ISessionService newSessionService(String serviceName) throws Exception {
		if (serviceName == null) {
			throw new InvalidParameterException("service name must be set");
		}
		if (this.attached == false) {
			throw new SCServiceException("newSessionService not possible - client not attached.");
		}
		return new SCSessionService(serviceName, this.scContext);
	}

	/** {@inheritDoc} */
	@Override
	public IPublishService newPublishService(String serviceName) throws Exception {
		if (serviceName == null) {
			throw new InvalidParameterException("service name must be set");
		}
		if (this.attached == false) {
			throw new SCServiceException("newPublishService not possible - client not attached.");
		}
		return new SCPublishService(serviceName, this.scContext);
	}

	/** {@inheritDoc} */
	@Override
	public void setMaxConnections(int maxConnections) throws SCMPValidatorException {
		ValidatorUtility.validateInt(1, maxConnections, SCMPError.HV_WRONG_MAX_CONNECTIONS);
		this.maxConnections = maxConnections;
	}

	/** {@inheritDoc} */
	@Override
	public int getMaxConnections() {
		return this.maxConnections;
	}

	/** {@inheritDoc} */
	@Override
	public void disableService(String serviceName) throws SCServiceException {
		if (this.attached == false) {
			// disableService not possible - client not attached
			throw new SCServiceException("client not attached - disableService not possible.");
		}
		String body = this.manageCall(Constants.DISABLE + Constants.EQUAL_SIGN + serviceName);
		if (body != null) {
			throw new SCServiceException(body.toString());
		}
	}

	/** {@inheritDoc} */
	@Override
	public void enableService(String serviceName) throws SCServiceException {
		if (this.attached == false) {
			// enableService not possible - client not attached
			throw new SCServiceException("client not attached - enableService not possible.");
		}
		String body = this.manageCall(Constants.ENABLE + Constants.EQUAL_SIGN + serviceName);
		if (body != null) {
			throw new SCServiceException(body.toString());
		}
	}

	/** {@inheritDoc} */
	@Override
	public boolean isServiceEnabled(String serviceName) throws SCServiceException {
		if (this.attached == false) {
			// isServiceEnabled not possible - client not attached
			throw new SCServiceException("client not attached - isServiceEnabled not possible.");
		}
		String body = this.inspectCall(Constants.STATE + Constants.EQUAL_SIGN + serviceName);
		if (ServiceState.ENABLED.toString().equalsIgnoreCase(body)) {
			return true;
		}
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public String workload(String serviceName) throws SCServiceException {
		if (this.attached == false) {
			// isServiceEnabled not possible - client not attached
			throw new SCServiceException("client not attached - isServiceEnabled not possible.");
		}
		return this.inspectCall(Constants.SESSIONS + Constants.EQUAL_SIGN + serviceName);
	}

	/** {@inheritDoc} */
	@Override
	public void killSC() throws SCServiceException {
		if (this.attached == false) {
			// killSC not possible - client not attached
			throw new SCServiceException("client not attached - killSC not possible.");
		}
		this.manageCall(Constants.KILL);
	}

	private String inspectCall(String instruction) throws SCServiceException {
		SCMPInspectCall inspectCall = (SCMPInspectCall) SCMPCallFactory.INSPECT_CALL.newInstance(this.requester);
		ServiceCallback callback = new ServiceCallback(true);
		try {
			inspectCall.setRequestBody(instruction);
			inspectCall
					.invoke(callback, Constants.DEFAULT_OPERATION_TIMEOUT_SECONDS * Constants.SEC_TO_MILLISEC_FACTOR);
		} catch (Exception e) {
			this.connectionPool.destroy();
			throw new SCServiceException("kill SC failed", e);
		}
		if (instruction.equalsIgnoreCase(Constants.KILL)) {
			// kill sc doesn't reply a message
			return null;
		}
		SCMPMessage reply = callback.getMessageSync();
		if (reply.isFault()) {
			throw new SCServiceException("inspect failed : " + reply.getHeader(SCMPHeaderAttributeKey.SC_ERROR_TEXT));
		}
		return (String) reply.getBody();
	}

	/**
	 * Process a manage call.
	 * 
	 * @param instruction
	 *            the instruction
	 * @throws SCServiceException
	 *             the SC service exception
	 */
	private String manageCall(String instruction) throws SCServiceException {
		SCMPManageCall manageCall = (SCMPManageCall) SCMPCallFactory.MANAGE_CALL.newInstance(this.requester);
		ServiceCallback callback = new ServiceCallback(true);
		try {
			manageCall.setRequestBody(instruction);
			manageCall.invoke(callback, Constants.DEFAULT_OPERATION_TIMEOUT_SECONDS * Constants.SEC_TO_MILLISEC_FACTOR);
		} catch (Exception e) {
			this.connectionPool.destroy();
			throw new SCServiceException("kill SC failed", e);
		}
		if (instruction.equalsIgnoreCase(Constants.KILL)) {
			// kill sc doesn't reply a message
			return null;
		}
		SCMPMessage reply = callback.getMessageSync();
		if (reply.isFault()) {
			throw new SCServiceException("manage failed : " + reply.getHeader(SCMPHeaderAttributeKey.SC_ERROR_TEXT));
		}
		return (String) reply.getBody();
	}
}
