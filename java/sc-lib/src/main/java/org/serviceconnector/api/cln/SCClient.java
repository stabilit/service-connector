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

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.serviceconnector.Constants;
import org.serviceconnector.api.SCServiceException;
import org.serviceconnector.api.SCSubscribeMessage;
import org.serviceconnector.call.SCMPAttachCall;
import org.serviceconnector.call.SCMPDetachCall;
import org.serviceconnector.call.SCMPInspectCall;
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.conf.RemoteNodeConfiguration;
import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.net.ConnectionType;
import org.serviceconnector.net.req.SCRequester;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.util.URLString;
import org.serviceconnector.util.ValidatorUtility;

/**
 * The class SCClient. Client to a SC.
 * Communication between client and SC starts with attaching the client to the SC. Settings like keep alive interval or max number
 * of connections must be set before attaching the client. After successful attaching the client API user may create session,
 * publish or file services. More information available in specific classes.<br />
 * <br />
 * State Diagram<br />
 * 
 * <pre>
 *        ||
 *        \/
 *    |---------|              |----------|
 *    | initial |----attach--->| attached |
 *    |         |<---detach----|          |
 *    |---------|              |----------|
 *        ||
 *        \/
 * </pre>
 * 
 * Never forget at the end of a communication to detach the client of the SC. Even in case of an error the detach must be done!
 * Like closing a file or a stream. The detach operation does detach client from SC and releases local resources (connections,
 * timers, threads) if not used by another client.
 */
public class SCClient {

	/** The Constant LOGGER. */
	@SuppressWarnings("unused")
	private static final Logger LOGGER = Logger.getLogger(SCClient.class);

	/** The host of the SC. */
	private String host;
	/** The port of the SC. */
	private int port;
	/** The max connections to use in pool which connects to SC. Default = 100. */
	private int maxConnections;
	/**
	 * The keep alive interval. Interval in seconds between two subsequent keepAlive requests (KRQ). The keepAlive message is solely
	 * used to refresh the firewall timeout on the network path. KeepAlive message is only sent on an idle connection. The value = 0
	 * means no keep alive messages will be sent. Default = 60.
	 */
	private int keepAliveIntervalSeconds;
	/** The keep alive timeout in seconds. Time to wait for the reply of a keep alive sent to the SC. Default = 10. */
	private int keepAliveTimeoutSeconds;
	/** The connection type used to connect to SC. {netty.http/netty.tcp}. Default netty.tcp */
	private ConnectionType connectionType;
	/** The requester. */
	protected SCRequester requester;
	/** The attached flag. Indicates if a SCClient is already attached to SC */
	protected boolean attached;

	private boolean activeGuardian;

	private SCPublishService cacheGuardian;

	/**
	 * Instantiates a new SC client with default connection type.
	 * 
	 * @param host
	 *            the host
	 * @param port
	 *            the port
	 */
	public SCClient(String host, int port) {
		this(host, port, ConnectionType.DEFAULT_CLIENT_CONNECTION_TYPE);
	}

	/**
	 * Instantiates a new SC client.
	 * 
	 * @param host
	 *            the host
	 * @param port
	 *            the port
	 * @param connectionType
	 *            the connection type
	 */
	public SCClient(String host, int port, ConnectionType connectionType) {
		this.host = host;
		this.port = port;
		this.connectionType = connectionType;
		this.keepAliveIntervalSeconds = Constants.DEFAULT_KEEP_ALIVE_INTERVAL_SECONDS;
		this.keepAliveTimeoutSeconds = Constants.DEFAULT_KEEP_ALIVE_OTI_SECONDS;
		this.attached = false;
		this.activeGuardian = false;
		this.maxConnections = Constants.DEFAULT_MAX_CONNECTION_POOL_SIZE;
		this.cacheGuardian = null;
	}

	/**
	 * Attach client to SC with default operation timeout. <br />
	 * Attach starts the communication to the SC. Once an attach is called a detach must be done at the end of
	 * communication.
	 * 
	 * @throws SCMPValidatorException
	 *             port is not within limits 0 to 0xFFFF<br />
	 *             host is missing<br />
	 * @throws SCServiceException
	 *             instance already attached before<br />
	 *             attach to host failed<br />
	 *             error message received from SC <br />
	 */
	public synchronized void attach() throws SCServiceException, SCMPValidatorException {
		this.attach(Constants.DEFAULT_OPERATION_TIMEOUT_SECONDS);
	}

	/**
	 * Attach client to SC.<br />
	 * Attach starts the communication to the SC. Once an attach is called a detach must be done at the end of
	 * communication.
	 * 
	 * @param operationTimeoutSeconds
	 *            the allowed time in seconds to complete the operation
	 * @throws SCMPValidatorException
	 *             port is not within limits 0 to 0xFFFF<br />
	 *             host is missing<br />
	 * @throws SCServiceException
	 *             instance already attached before<br />
	 *             attach to host failed<br />
	 *             error message received from SC <br />
	 */
	public synchronized void attach(int operationTimeoutSeconds) throws SCServiceException, SCMPValidatorException {
		// 1. checking preconditions and validate
		if (this.attached) {
			throw new SCServiceException("SCClient already attached.");
		}
		if (host == null) {
			throw new SCMPValidatorException("Host is missing.");
		}
		ValidatorUtility.validateInt(Constants.MIN_PORT_VALUE, this.port, Constants.MAX_PORT_VALUE, SCMPError.HV_WRONG_PORTNR);
		// 2. initialize call & invoke
		synchronized (AppContext.communicatorsLock) {
			AppContext.init();
			RemoteNodeConfiguration remoteNodeConf = new RemoteNodeConfiguration(this.port + "client", this.host, this.port,
					this.connectionType.getValue(), this.keepAliveIntervalSeconds, 0, this.maxConnections);
			this.requester = new SCRequester(remoteNodeConf, this.keepAliveTimeoutSeconds * Constants.SEC_TO_MILLISEC_FACTOR);
			SCServiceCallback callback = new SCServiceCallback(true);
			SCMPAttachCall attachCall = new SCMPAttachCall(this.requester);
			try {
				attachCall.invoke(callback, operationTimeoutSeconds * Constants.SEC_TO_MILLISEC_FACTOR);
			} catch (Exception e) {
				this.requester.destroy();
				// release resources
				AppContext.destroy();
				throw new SCServiceException("Attach to " + host + ":" + port + " failed. ", e);
			}
			// 3. receiving reply and error handling
			SCMPMessage reply = callback.getMessageSync(operationTimeoutSeconds * Constants.SEC_TO_MILLISEC_FACTOR);
			if (reply.isFault()) {
				this.requester.destroy();
				// release resources
				AppContext.destroy();
				SCServiceException ex = new SCServiceException("Attach to " + host + ":" + port + " failed.");
				ex.setSCErrorCode(reply.getHeaderInt(SCMPHeaderAttributeKey.SC_ERROR_CODE));
				ex.setSCErrorText(reply.getHeader(SCMPHeaderAttributeKey.SC_ERROR_TEXT));
				throw ex;
			}
			// 4. post process, reply to client
			this.attached = true;
			AppContext.attachedCommunicators.incrementAndGet();
		}
	}

	/**
	 * Checks if client is attached to SC.
	 * 
	 * @return true, if is attached
	 */
	public synchronized boolean isAttached() {
		return this.attached;
	}

	/**
	 * Detach client from SC with default operation timeout.<br />
	 * Detach end the communication to the SC. Resources (connections, timers, threads) are released if not used by another client.
	 * 
	 * @throws SCServiceException
	 *             detach to host failed<br />
	 *             error message received from SC<br />
	 */
	public synchronized void detach() throws SCServiceException {
		this.detach(Constants.DEFAULT_OPERATION_TIMEOUT_SECONDS);
	}

	/**
	 * Detach client from SC.<br />
	 * Detach end the communication to the SC. Resources (connections, timers, threads) are released if not used by another client.
	 * 
	 * @param operationTimeoutSeconds
	 *            the allowed time in seconds to complete the operation
	 * @throws SCServiceException
	 *             detach to host failed<br />
	 *             error message received from SC<br />
	 */
	public synchronized void detach(int operationTimeoutSeconds) throws SCServiceException {
		// 1. checking preconditions and initialize
		if (this.attached == false) {
			// client is not attached just ignore
			return;
		}
		// 2. initialize call & invoke
		try {
			SCServiceCallback callback = new SCServiceCallback(true);
			SCMPDetachCall detachCall = new SCMPDetachCall(this.requester);
			try {
				detachCall.invoke(callback, operationTimeoutSeconds * Constants.SEC_TO_MILLISEC_FACTOR);
			} catch (Exception e) {
				throw new SCServiceException("Detach client failed. ", e);
			}
			// 3. receiving reply and error handling
			SCMPMessage reply = callback.getMessageSync(operationTimeoutSeconds * Constants.SEC_TO_MILLISEC_FACTOR);
			if (reply.isFault()) {
				SCServiceException ex = new SCServiceException("Detach client failed.");
				ex.setSCErrorCode(reply.getHeaderInt(SCMPHeaderAttributeKey.SC_ERROR_CODE));
				ex.setSCErrorText(reply.getHeader(SCMPHeaderAttributeKey.SC_ERROR_TEXT));
				throw ex;
			}
		} finally {
			// 4. post process, reply to client
			this.attached = false;
			// destroy connection pool
			this.requester.destroy();
			synchronized (AppContext.communicatorsLock) {
				AppContext.attachedCommunicators.decrementAndGet();
				// release resources
				AppContext.destroy();
			}
		}
	}

	/**
	 * Creates a new file service.
	 * 
	 * @param serviceName
	 *            the service name of the file service to use
	 * @throws SCMPValidatorException
	 *             serviceName not within length limits 1 to 32 bytes <br />
	 * @throws SCServiceException
	 *             called method after attach
	 * @return the file service
	 */
	public SCFileService newFileService(String serviceName) throws SCServiceException, SCMPValidatorException {
		if (this.attached == false) {
			throw new SCServiceException("Creating a new file service not possible - client not attached.");
		}
		if (serviceName == null) {
			throw new SCMPValidatorException("Service name must be set.");
		}
		ValidatorUtility
				.validateStringLengthTrim(1, serviceName, Constants.MAX_LENGTH_SERVICENAME, SCMPError.HV_WRONG_SERVICE_NAME);
		return new SCFileService(this, serviceName, this.requester);
	}

	/**
	 * Creates a new session service.
	 * 
	 * @param serviceName
	 *            the service name of the session service to use
	 * @throws SCMPValidatorException
	 *             serviceName not within length limits 1 to 32 bytes <br />
	 * @throws SCServiceException
	 *             called method after attach
	 * @return the session service
	 */
	public SCSessionService newSessionService(String serviceName) throws SCServiceException, SCMPValidatorException {
		if (this.attached == false) {
			throw new SCServiceException("Creating a new session service not possible - client not attached.");
		}
		if (serviceName == null) {
			throw new SCMPValidatorException("Service name must be set.");
		}
		ValidatorUtility
				.validateStringLengthTrim(1, serviceName, Constants.MAX_LENGTH_SERVICENAME, SCMPError.HV_WRONG_SERVICE_NAME);
		return new SCSessionService(this, serviceName, this.requester);
	}

	/**
	 * Creates a new publish service.
	 * 
	 * @param serviceName
	 *            the service name of the publish service to use
	 * @throws SCMPValidatorException
	 *             serviceName not within length limits 1 to 32 bytes <br />
	 * @throws SCServiceException
	 *             called method after attach
	 * @return the publish service
	 */
	public synchronized SCPublishService newPublishService(String serviceName) throws SCServiceException, SCMPValidatorException {
		if (this.attached == false) {
			throw new SCServiceException("Creating a new publish service not possible - client not attached.");
		}
		if (serviceName == null) {
			throw new SCMPValidatorException("Service name must be set.");
		}
		ValidatorUtility
				.validateStringLengthTrim(1, serviceName, Constants.MAX_LENGTH_SERVICENAME, SCMPError.HV_WRONG_SERVICE_NAME);
		return new SCPublishService(this, serviceName, this.requester);
	}

	public synchronized void startCacheGuardian(String guardianName, SCSubscribeMessage subscribeMessage,
			SCMessageCallback guardianCallback) throws SCServiceException, SCMPValidatorException {
		this.startCacheGuardian(Constants.DEFAULT_OPERATION_TIMEOUT_SECONDS, guardianName, subscribeMessage, guardianCallback);
	}

	public synchronized void startCacheGuardian(int operationTimeoutSeconds, String guardianName,
			SCSubscribeMessage subscribeMessage, SCMessageCallback guardianCallback) throws SCServiceException,
			SCMPValidatorException {
		if (this.attached == false) {
			throw new SCServiceException("Starting a Cache Guardian not possible - client not attached.");
		}
		if (this.activeGuardian == true) {
			throw new SCServiceException("Cache Guardian already started.");
		}
		if (guardianName == null) {
			throw new SCMPValidatorException("Cache Guardian name must be set.");
		}
		ValidatorUtility.validateStringLengthTrim(1, guardianName, Constants.MAX_LENGTH_SERVICENAME,
				SCMPError.HV_WRONG_SERVICE_NAME);

		this.cacheGuardian = new SCPublishService(this, guardianName, this.requester);
		this.cacheGuardian.subscribe(operationTimeoutSeconds, subscribeMessage, guardianCallback);
		this.activeGuardian = true;
	}

	public synchronized void changeCacheGuardian(SCSubscribeMessage scSubscribeMessage) throws SCMPValidatorException,
			SCServiceException {
		this.changeGuardian(Constants.DEFAULT_OPERATION_TIMEOUT_SECONDS, scSubscribeMessage);
	}

	public synchronized void changeGuardian(int operationTimeoutSeconds, SCSubscribeMessage scSubscribeMessage)
			throws SCMPValidatorException, SCServiceException {
		this.cacheGuardian.changeSubscription(operationTimeoutSeconds, scSubscribeMessage);
	}

	public synchronized void stopCacheGuardian() throws SCServiceException, SCMPValidatorException {
		this.stopCacheGuardian(Constants.DEFAULT_OPERATION_TIMEOUT_SECONDS);
	}

	public synchronized void stopCacheGuardian(int operationTimeoutSeconds) throws SCServiceException, SCMPValidatorException {
		if (this.attached == false) {
			throw new SCServiceException("Stopping an Cache Guardian not possible - client not attached.");
		}
		if (this.activeGuardian == false) {
			// no active guardian to stop, ignore
			return;
		}
		try {
			this.cacheGuardian.unsubscribe(operationTimeoutSeconds);
		} finally {
			this.activeGuardian = false;
		}
	}

	/**
	 * Gets the SC version. Version of the SC which the client is currently connected to.
	 * 
	 * @return the SC version
	 * @throws SCServiceException
	 *             client not attached<br />
	 *             the inspect call failed<br />
	 *             error message received from SC<br />
	 * @throws UnsupportedEncodingException
	 *             the unsupported encoding exception
	 */
	public String getSCVersion() throws SCServiceException, UnsupportedEncodingException {
		if (this.attached == false) {
			throw new SCServiceException("Client not attached - getStateOfServices not possible.");
		}
		String urlString = URLString.toURLRequestString(Constants.CC_CMD_SC_VERSION);
		String body = this.inspectCall(Constants.DEFAULT_OPERATION_TIMEOUT_SECONDS, urlString);
		URLString urlResponse = new URLString();
		urlResponse.parseResponseURLString(body);
		return urlResponse.getParameterMap().get(Constants.CC_CMD_SC_VERSION);
	}

	/**
	 * Checks if service is enabled on SC with default operation timeout.
	 * 
	 * @param serviceName
	 *            the service name
	 * @return true, if is service enabled
	 * @throws SCServiceException
	 *             client not attached<br />
	 *             the inspect call failed<br />
	 *             error message received from SC<br />
	 * @throws UnsupportedEncodingException
	 *             encoding of request URL failed<br />
	 */
	public boolean isServiceEnabled(String serviceName) throws SCServiceException, UnsupportedEncodingException {
		return this.isServiceEnabled(Constants.DEFAULT_OPERATION_TIMEOUT_SECONDS, serviceName);
	}

	/**
	 * Checks if service is enabled on SC.<br />
	 * Operation only possible if client gets successfully attached before.
	 * 
	 * @param operationTimeout
	 *            the allowed time in seconds to complete the operation
	 * @param serviceName
	 *            the service name
	 * @return true, if is service enabled
	 * @throws SCServiceException
	 *             client not attached<br />
	 *             the inspect call failed<br />
	 *             error message received from SC<br />
	 * @throws UnsupportedEncodingException
	 *             encoding of request URL failed<br />
	 */
	public boolean isServiceEnabled(int operationTimeout, String serviceName) throws SCServiceException,
			UnsupportedEncodingException {
		if (this.attached == false) {
			throw new SCServiceException("Client not attached - isServiceEnabled not possible.");
		}
		String urlString = URLString.toURLRequestString(Constants.CC_CMD_STATE, Constants.SERVICE_NAME, serviceName);
		try {
			String body = this.inspectCall(operationTimeout, urlString);
			URLString urlResponse = new URLString();
			urlResponse.parseResponseURLString(body);
			String value = urlResponse.getParamValue(serviceName);
			if (value != null && value.equals(Constants.STATE_ENABLED)) {
				return true;
			}
			return false;
		} catch (SCServiceException e) {
			if (e.getSCErrorCode() == SCMPError.SERVICE_NOT_FOUND.getErrorCode()) {
				return false;
			}
			throw e;
		} catch (UnsupportedEncodingException e) {
			throw new SCServiceException(e.toString());
		}
	}

	/**
	 * Gets the state of services with default operation timeout.<br />
	 * Operation only possible if client gets successfully attached before.
	 * 
	 * @param serviceNamePattern
	 *            the service name pattern
	 * @return map containing serviceName and state enabled/disabled.
	 * @throws SCServiceException
	 *             client not attached<br />
	 *             the inspect call failed<br />
	 *             error message received from SC<br />
	 * @throws UnsupportedEncodingException
	 *             encoding of request URL failed<br />
	 */
	public Map<String, String> getStateOfServices(String serviceNamePattern) throws SCServiceException,
			UnsupportedEncodingException {
		return this.getStateOfServices(Constants.DEFAULT_OPERATION_TIMEOUT_SECONDS, serviceNamePattern);
	}

	/**
	 * Gets the state of services. If service is not found, map will be empty.<br />
	 * Operation only possible if client gets successfully attached before.
	 * 
	 * @param operationTimeout
	 *            the allowed time in seconds to complete the operation
	 * @param serviceNamePattern
	 *            the service name pattern
	 * @return map containing serviceName and state enabled/disabled.
	 * @throws SCServiceException
	 *             client not attached<br />
	 *             the inspect call failed<br />
	 *             error message received from SC<br />
	 * @throws UnsupportedEncodingException
	 *             encoding of request URL failed<br />
	 */
	public Map<String, String> getStateOfServices(int operationTimeout, String serviceNamePattern) throws SCServiceException,
			UnsupportedEncodingException {
		if (this.attached == false) {
			throw new SCServiceException("Client not attached - getStateOfServices not possible.");
		}
		String urlString = URLString.toURLRequestString(Constants.CC_CMD_STATE, Constants.SERVICE_NAME, serviceNamePattern);
		try {
			String body = this.inspectCall(operationTimeout, urlString);
			URLString urlResponse = new URLString();
			urlResponse.parseResponseURLString(body);
			return urlResponse.getParameterMap();
		} catch (SCServiceException serviceEx) {
			return new HashMap<String, String>();
		} catch (UnsupportedEncodingException e) {
			throw new SCServiceException(e.toString());
		}
	}

	/**
	 * Gets the configuration of a service with default operation timeout. Use only specific service names - passing regex patterns
	 * for service name is not allowed.
	 * 
	 * @param operationTimeout
	 *            the allowed time in seconds to complete the operation
	 * @param serviceName
	 *            the service name
	 * @return the service configuration
	 * @throws SCServiceException
	 *             client not attached<br />
	 *             the inspect call failed<br />
	 *             error message received from SC<br />
	 *             service not found on SC<br />
	 * @throws UnsupportedEncodingException
	 *             encoding of request URL failed<br />
	 */
	public Map<String, String> getServiceConfiguration(String serviceName) throws SCServiceException, UnsupportedEncodingException {
		return this.getServiceConfiguration(Constants.DEFAULT_OPERATION_TIMEOUT_SECONDS, serviceName);
	}

	/**
	 * Gets the configuration of a service. Use only specific service names - passing regex patterns for service name is not
	 * allowed.
	 * 
	 * @param operationTimeout
	 *            the allowed time in seconds to complete the operation
	 * @param serviceName
	 *            the service name
	 * @return the service configuration
	 * @throws SCServiceException
	 *             client not attached<br />
	 *             the inspect call failed<br />
	 *             error message received from SC<br />
	 *             service not found on SC<br />
	 * @throws UnsupportedEncodingException
	 *             encoding of request URL failed<br />
	 */
	public Map<String, String> getServiceConfiguration(int operationTimeout, String serviceName) throws SCServiceException,
			UnsupportedEncodingException {
		if (this.attached == false) {
			throw new SCServiceException("Client not attached - getServiceConfiguration not possible.");
		}
		String urlString = URLString.toURLRequestString(Constants.CC_CMD_SERVICE_CONF, Constants.SERVICE_NAME, serviceName);
		try {
			String body = this.inspectCall(operationTimeout, urlString);
			URLString urlResponse = new URLString();
			urlResponse.parseResponseURLString(body);
			return urlResponse.getParameterMap();
		} catch (UnsupportedEncodingException e) {
			throw new SCServiceException(e.toString());
		}
	}

	/**
	 * Returns the number of available and allocated sessions for given service name. Uses default operation timeout to complete
	 * operation.<br />
	 * Operation only possible if client gets successfully attached before.
	 * 
	 * @param serviceName
	 *            the service name
	 * @return map containing serviceName and the available/allocated sessions, e.g. "4/2".
	 * @throws SCServiceException
	 *             client not attached<br />
	 *             the inspect call failed<br />
	 *             error message received from SC<br />
	 * @throws UnsupportedEncodingException
	 *             encoding of request URL failed<br />
	 */
	public Map<String, String> getWorkload(String serviceName) throws SCServiceException, UnsupportedEncodingException {
		return this.getWorkload(Constants.DEFAULT_OPERATION_TIMEOUT_SECONDS, serviceName);
	}

	/**
	 * Returns the number of available and allocated sessions for given service name. <br />
	 * Operation only possible if client gets successfully attached before.
	 * 
	 * @param operationTimeoutSeconds
	 *            the allowed time in seconds to complete the operation
	 * @param serviceName
	 *            the service name
	 * @return map containing serviceName and the available/allocated sessions, e.g. "4/2".
	 * @throws SCServiceException
	 *             client not attached<br />
	 *             the inspect call failed<br />
	 *             error message received from SC<br />
	 * @throws UnsupportedEncodingException
	 *             encoding of request URL failed<br />
	 */
	public Map<String, String> getWorkload(int operationTimeoutSeconds, String serviceName) throws SCServiceException,
			UnsupportedEncodingException {
		if (this.attached == false) {
			throw new SCServiceException("Client not attached - isServiceEnabled not possible.");
		}
		String urlString = URLString.toURLRequestString(Constants.CC_CMD_SESSIONS, Constants.SERVICE_NAME, serviceName);
		String body = this.inspectCall(operationTimeoutSeconds, urlString);
		try {
			URLString urlResponse = new URLString();
			urlResponse.parseResponseURLString(body);
			return urlResponse.getParameterMap();
		} catch (UnsupportedEncodingException e) {
			throw new SCServiceException(e.toString());
		}
	}

	/**
	 * inspects the cache for given service name and cacheId. Uses default operation timeout to complete
	 * operation.<br />
	 * Operation only possible if client gets successfully attached before.
	 * 
	 * @param cacheId
	 *            the cache id
	 * @return map containing ("return", "success|notfound"), (Constants.CACHE_ID, cacheId), ("cacheState", cacheState),
	 *         ("cacheSize", size), ("cacheExpiration", expirationDateTime)
	 * @throws SCServiceException
	 *             client not attached<br />
	 *             the inspect call failed<br />
	 *             error message received from SC<br />
	 * @throws UnsupportedEncodingException
	 *             encoding of request URL failed<br />
	 */
	public Map<String, String> inspectCache(String cacheId) throws SCServiceException, UnsupportedEncodingException {
		return this.inspectCache(Constants.DEFAULT_OPERATION_TIMEOUT_SECONDS, cacheId);
	}

	/**
	 * Inspects the cache for given service name and cacheId.<br />
	 * Operation only possible if client gets successfully attached before.
	 * 
	 * @param operationTimeoutSeconds
	 *            the allowed time in seconds to complete the operation
	 * @param cacheId
	 *            the cache id
	 * @return map containing ("return", "success|notfound"), (Constants.CACHE_ID, cacheId), ("cacheState", cacheState),
	 *         ("cacheSize", size), ("cacheExpiration", expirationDateTime)
	 * @throws SCServiceException
	 *             client not attached<br />
	 *             the inspect call failed<br />
	 *             error message received from SC<br />
	 * @throws UnsupportedEncodingException
	 *             encoding of request URL failed<br />
	 */
	public Map<String, String> inspectCache(int operationTimeoutSeconds, String cacheId) throws SCServiceException,
			UnsupportedEncodingException {
		if (this.attached == false) {
			throw new SCServiceException("Client not attached - inspectCache not possible.");
		}
		String urlString = URLString.toURLRequestString(Constants.CC_CMD_INSPECT_CACHE, Constants.CACHE_ID, cacheId);
		String body = this.inspectCall(operationTimeoutSeconds, urlString);
		try {
			URLString urlResponse = new URLString();
			urlResponse.parseResponseURLString(body);
			return urlResponse.getParameterMap();
		} catch (UnsupportedEncodingException e) {
			throw new SCServiceException(e.toString());
		}
	}

	/**
	 * Inspect call.
	 * 
	 * @param operationTimeoutSeconds
	 *            the allowed time in seconds to complete the operation
	 * @param instruction
	 *            the instruction
	 * @return the string
	 * @throws SCServiceException
	 *             the inspect call to SC failed<br />
	 *             error message received from SC<br />
	 */
	private String inspectCall(int operationTimeoutSeconds, String instruction) throws SCServiceException {
		SCMPInspectCall inspectCall = new SCMPInspectCall(this.requester);
		SCServiceCallback callback = new SCServiceCallback(true);
		try {
			inspectCall.setRequestBody(instruction);
			inspectCall.invoke(callback, operationTimeoutSeconds * Constants.SEC_TO_MILLISEC_FACTOR);
		} catch (Exception e) {
			this.requester.destroy();
			throw new SCServiceException("Inspect request failed. ", e);
		}
		if (instruction.equalsIgnoreCase(Constants.CC_CMD_KILL)) {
			// on KILL SC cannot reply a message
			return null;
		}
		SCMPMessage reply = callback.getMessageSync(operationTimeoutSeconds * Constants.SEC_TO_MILLISEC_FACTOR);
		if (reply.isFault()) {
			SCServiceException ex = new SCServiceException("Inspect failed.");
			ex.setSCErrorCode(reply.getHeaderInt(SCMPHeaderAttributeKey.SC_ERROR_CODE));
			ex.setSCErrorText(reply.getHeader(SCMPHeaderAttributeKey.SC_ERROR_TEXT));
			throw ex;
		}
		return (String) reply.getBody();
	}

	/**
	 * Gets the connection type.
	 * 
	 * @return the connection type in use
	 */
	public ConnectionType getConnectionType() {
		return this.connectionType;
	}

	/**
	 * Gets the SC host.
	 * 
	 * @return the SC host
	 */
	public String getHost() {
		return this.host;
	}

	/**
	 * Gets the SC port.
	 * 
	 * @return the SC port
	 */
	public int getPort() {
		return this.port;
	}

	/**
	 * Sets the keep alive interval in seconds. Interval in seconds between two subsequent keepAlive requests (KRQ). The keepAlive
	 * message is solely used to refresh the firewall timeout on the network path. KeepAlive message is only sent on an idle
	 * connection. The value = 0 means no keep alive messages will be sent. <br />
	 * Setting the attribute only possible if client is not attached yet.
	 * 
	 * @param keepAliveIntervalSeconds
	 *            Example: 300
	 * @throws SCMPValidatorException
	 *             keepAliveIntervalSeconds not within limits 0 to 3600 <br />
	 * @throws SCServiceException
	 *             called method after attach
	 */
	public void setKeepAliveIntervalSeconds(int keepAliveIntervalSeconds) throws SCServiceException, SCMPValidatorException {
		// validate in this case its a local needed information
		ValidatorUtility.validateInt(Constants.MIN_KPI_VALUE, keepAliveIntervalSeconds, Constants.MAX_KPI_VALUE,
				SCMPError.HV_WRONG_KEEPALIVE_INTERVAL);
		if (this.attached == true) {
			throw new SCServiceException("Can not set property, client is already attached.");
		}
		this.keepAliveIntervalSeconds = keepAliveIntervalSeconds;
	}

	/**
	 * Gets the keep alive interval in seconds.
	 * 
	 * @return the keep alive interval in seconds
	 */
	public int getKeepAliveIntervalSeconds() {
		return this.keepAliveIntervalSeconds;
	}

	/**
	 * Sets the keep alive timeout in seconds. Time in seconds a keep alive request waits to be confirmed. If no confirmation is
	 * received single connection gets closed.
	 * 
	 * @param keepAliveTimeoutSeconds
	 *            time to wait for completion of a keep alive request
	 *            Example: 10
	 * @throws SCMPValidatorException
	 *             keepAliveTimeoutSeconds > 1 and < 3600<br />
	 * @throws SCServiceException
	 *             called method after attach
	 */
	public void setKeepAliveTimeoutSeconds(int keepAliveTimeoutSeconds) throws SCMPValidatorException, SCServiceException {
		// validate in this case its a local needed information
		ValidatorUtility.validateInt(1, keepAliveTimeoutSeconds, Constants.MAX_KP_TIMEOUT_VALUE,
				SCMPError.HV_WRONG_KEEPALIVE_TIMEOUT);
		if (this.attached) {
			throw new SCServiceException("Can not set property, client is already attached.");
		}
		this.keepAliveTimeoutSeconds = keepAliveTimeoutSeconds;
	}

	/**
	 * Gets the keep alive timeout seconds.
	 * 
	 * @return the keep alive timeout seconds
	 */
	public int getKeepAliveTimeoutSeconds() {
		return this.keepAliveTimeoutSeconds;
	}

	/**
	 * Sets the TCP keep alive. True to enable sending of TCP keep alive (if underlying OS properly configures keep alive). False to
	 * disable sending.
	 * 
	 * @param tcpKeepAlive
	 *            the new TCP keep alive
	 * @throws SCServiceException
	 *             called method after attach
	 */
	public void setTCPKeepAlive(boolean tcpKeepAlive) throws SCServiceException {
		if (this.attached) {
			throw new SCServiceException("Can not set property, client is already attached.");
		}
		// sets the TCP keep alive for initiating connections in basic configuration
		AppContext.getBasicConfiguration().setTcpKeepAliveInitiator(tcpKeepAlive);
	}

	/**
	 * Gets the TCP keep alive.
	 * 
	 * @return the TCP keep alive
	 *         TRUE - TCP keep alive is enabled
	 *         FALSE - TCP keep alive is disabled
	 */
	public Boolean getTCPKeepAlive() {
		// returns TCP keep alive for initiating connections from basic configuration
		return AppContext.getBasicConfiguration().getTcpKeepAliveInitiator();
	}

	/**
	 * Sets the max connections of the pool which is connecting to SC. <br />
	 * Setting the attribute only possible if client is not attached yet.
	 * 
	 * @param maxConnections
	 *            the new max connections used by connection pool.
	 * @throws SCMPValidatorException
	 *             maxConnections smaller two<br />
	 * @throws SCServiceException
	 *             called method after attach
	 */
	public void setMaxConnections(int maxConnections) throws SCServiceException, SCMPValidatorException {
		if (this.attached == true) {
			throw new SCServiceException("Can not set property, client is already attached.");
		}
		// maxConnections must be at least 2 - echo call and execute/send might be parallel
		ValidatorUtility.validateInt(2, maxConnections, SCMPError.HV_WRONG_MAX_CONNECTIONS);
		this.maxConnections = maxConnections;
	}

	/**
	 * Gets the max connections.
	 * 
	 * @return the max connections used in pool
	 */
	public int getMaxConnections() {
		return this.maxConnections;
	}
}
