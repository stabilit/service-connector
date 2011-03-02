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

import org.apache.log4j.Logger;
import org.serviceconnector.Constants;
import org.serviceconnector.api.SCServiceException;
import org.serviceconnector.call.SCMPInspectCall;
import org.serviceconnector.call.SCMPManageCall;
import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.net.ConnectionType;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.util.URLString;

/**
 * Management client to an SC.
 * 
 * @author JTrnka
 */
public class SCMgmtClient extends SCClient {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = Logger.getLogger(SCMgmtClient.class);

	/**
	 * Instantiates a new SC management client.
	 * 
	 * @param host
	 *            the host
	 * @param port
	 *            the port
	 */
	public SCMgmtClient(String host, int port) {
		super(host, port);
	}

	/**
	 * Instantiates a new SC management client.
	 * 
	 * @param host
	 *            the host
	 * @param port
	 *            the port
	 * @param connectionType
	 *            the connection type
	 */
	public SCMgmtClient(String host, int port, ConnectionType connectionType) {
		super(host, port, connectionType);
	}

	/**
	 * Disable service on SC with default operation timeout.
	 * 
	 * @param serviceName
	 *            the service name
	 * @throws SCServiceException
	 *             client not attached<br>
	 *             manage call to SC failed<br>
	 *             body not null after manage call<br>
	 * @throws UnsupportedEncodingException
	 *             encoding of request URL failed<br>
	 */
	public void disableService(String serviceName) throws SCServiceException, UnsupportedEncodingException {
		this.disableService(Constants.DEFAULT_OPERATION_TIMEOUT_SECONDS, serviceName);
	}

	/**
	 * Disable service on SC.
	 * 
	 * @param operationTimeoutSeconds
	 *            the allowed time in seconds to complete the operation
	 * @param serviceName
	 *            the service name
	 * @throws SCServiceException
	 *             client not attached<br>
	 *             manage call to SC failed<br>
	 *             body not null after manage call<br>
	 * @throws UnsupportedEncodingException
	 *             encoding of request URL failed<br>
	 */
	public void disableService(int operationTimeoutSeconds, String serviceName) throws SCServiceException,
			UnsupportedEncodingException {
		if (this.attached == false) {
			// disableService not possible - client not attached
			throw new SCServiceException("Client not attached - disableService not possible.");
		}
		String urlString = URLString.toURLRequestString(Constants.CC_CMD_DISABLE, Constants.SERVICE_NAME, serviceName);
		String body = this.manageCall(operationTimeoutSeconds, urlString);
		if (body != null) {
			throw new SCServiceException(body);
		}
	}

	/**
	 * Disable service on SC with default operation timeout.
	 * 
	 * @param serviceName
	 *            the service name
	 * @throws SCServiceException
	 *             client not attached<br>
	 *             manage call to SC failed<br>
	 *             body not null after manage call<br>
	 * @throws UnsupportedEncodingException
	 *             encoding of request failed<br>
	 */
	public void enableService(String serviceName) throws SCServiceException, UnsupportedEncodingException {
		this.enableService(Constants.DEFAULT_OPERATION_TIMEOUT_SECONDS, serviceName);
	}

	/**
	 * Enable service on SC.
	 * 
	 * @param operationTimeoutSeconds
	 *            the allowed time in seconds to complete the operation
	 * @param serviceName
	 *            the service name
	 * @throws SCServiceException
	 *             client not attached<br>
	 *             manage call to SC failed<br>
	 *             body not null after manage call<br>
	 * @throws UnsupportedEncodingException
	 *             encoding of request failed<br>
	 */
	public void enableService(int operationTimeoutSeconds, String serviceName) throws SCServiceException,
			UnsupportedEncodingException {
		if (this.attached == false) {
			// enableService not possible - client not attached
			throw new SCServiceException("Client not attached - enableService not possible.");
		}
		String urlString = URLString.toURLRequestString(Constants.CC_CMD_ENABLE, Constants.SERVICE_NAME, serviceName);
		String body = this.manageCall(operationTimeoutSeconds, urlString);
		if (body != null) {
			throw new SCServiceException(body);
		}
	}

	/**
	 * Checks if service is enabled on SC with default operation timeout.
	 * 
	 * @param serviceName
	 *            the service name
	 * @return the uRL response string
	 * @throws SCServiceException
	 *             client not attached<br>
	 *             the inspect call failed<br>
	 *             error message received from SC<br>
	 * @throws UnsupportedEncodingException
	 *             encoding of request URL failed<br>
	 */
	public URLString isServiceEnabled(String serviceName) throws SCServiceException, UnsupportedEncodingException {
		return this.isServiceEnabled(Constants.DEFAULT_OPERATION_TIMEOUT_SECONDS, serviceName);
	}

	/**
	 * Checks if service is enabled on SC.
	 * 
	 * @param operationTimeout
	 *            the operation timeout
	 * @param serviceName
	 *            the service name
	 * @return the uRL response string
	 * @throws SCServiceException
	 *             client not attached<br>
	 *             the inspect call failed<br>
	 *             error message received from SC<br>
	 * @throws UnsupportedEncodingException
	 *             encoding of request URL failed<br>
	 */
	public URLString isServiceEnabled(int operationTimeout, String serviceName) throws SCServiceException,
			UnsupportedEncodingException {
		if (this.attached == false) {
			throw new SCServiceException("Client not attached - isServiceEnabled not possible.");
		}
		String urlString = URLString.toURLRequestString(Constants.CC_CMD_STATE, Constants.SERVICE_NAME, serviceName);
		String body = this.inspectCall(operationTimeout, urlString);
		try {
			URLString urlResponse = new URLString();
			urlResponse.parseResponseURLString(body);
			return urlResponse;
		} catch (UnsupportedEncodingException e) {
			throw new SCServiceException(e.toString());
		}
	}

	/**
	 * Returns the number of available and allocated sessions for given service name. Uses default operation timeout to complete
	 * operation.
	 * 
	 * @param serviceName
	 *            the service name
	 * @return string containing the available and allocated sessions, e.g. "4/2".
	 * @throws SCServiceException
	 *             client not attached<br>
	 *             the inspect call failed<br>
	 *             error message received from SC<br>
	 * @throws UnsupportedEncodingException
	 *             encoding of request URL failed<br>
	 */
	public URLString getWorkload(String serviceName) throws SCServiceException, UnsupportedEncodingException {
		return this.getWorkload(Constants.DEFAULT_OPERATION_TIMEOUT_SECONDS, serviceName);
	}

	/**
	 * Returns the number of available and allocated sessions for given service name.
	 * 
	 * @param operationTimeoutSeconds
	 *            the allowed time in seconds to complete the operation
	 * @param serviceName
	 *            the service name
	 * @return string containing the available and allocated sessions, e.g. "4/2".
	 * @throws SCServiceException
	 *             client not attached<br>
	 *             the inspect call failed<br>
	 *             error message received from SC<br>
	 * @throws UnsupportedEncodingException
	 *             encoding of request URL failed<br>
	 */
	public URLString getWorkload(int operationTimeoutSeconds, String serviceName) throws SCServiceException,
			UnsupportedEncodingException {
		if (this.attached == false) {
			throw new SCServiceException("Client not attached - isServiceEnabled not possible.");
		}
		String urlString = URLString.toURLRequestString(Constants.CC_CMD_SESSIONS, Constants.SERVICE_NAME, serviceName);
		String body = this.inspectCall(operationTimeoutSeconds, urlString);
		try {
			URLString urlResponse = new URLString();
			urlResponse.parseResponseURLString(body);
			return urlResponse;
		} catch (UnsupportedEncodingException e) {
			throw new SCServiceException(e.toString());
		}
	}

	/**
	 * inspects the cache for given service name and cacheId. Uses default operation timeout to complete
	 * operation.
	 * 
	 * @param serviceName
	 *            the service name
	 * @param cacheId
	 *            the cache id
	 * @return the uRL string
	 * @throws SCServiceException
	 *             client not attached<br>
	 *             the inspect call failed<br>
	 *             error message received from SC<br>
	 * @throws UnsupportedEncodingException
	 *             encoding of request URL failed<br>
	 */
	public URLString inspectCache(String serviceName, String cacheId) throws SCServiceException, UnsupportedEncodingException {
		return this.inspectCache(Constants.DEFAULT_OPERATION_TIMEOUT_SECONDS, serviceName, cacheId);
	}

	/**
	 * Inspects the cache for given service name and cacheId.
	 * 
	 * @param operationTimeoutSeconds
	 *            the allowed time in seconds to complete the operation
	 * @param serviceName
	 *            the service name
	 * @param cacheId
	 *            the cache id
	 * @return the uRL string
	 * @throws SCServiceException
	 *             client not attached<br>
	 *             the inspect call failed<br>
	 *             error message received from SC<br>
	 * @throws UnsupportedEncodingException
	 *             encoding of request URL failed<br>
	 */
	public URLString inspectCache(int operationTimeoutSeconds, String serviceName, String cacheId) throws SCServiceException,
			UnsupportedEncodingException {
		if (this.attached == false) {
			throw new SCServiceException("Client not attached - inspectCache not possible.");
		}
		String urlString = URLString.toURLRequestString(Constants.CC_CMD_INSPECT_CACHE, Constants.SERVICE_NAME, serviceName,
				Constants.CACHE_ID, cacheId);
		String body = this.inspectCall(operationTimeoutSeconds, urlString);
		try {
			URLString urlResponse = new URLString();
			urlResponse.parseResponseURLString(body);
			return urlResponse;
		} catch (UnsupportedEncodingException e) {
			throw new SCServiceException(e.toString());
		}
	}

	/**
	 * Clears the cache. Uses default operation timeout to complete operation.
	 * 
	 * @throws SCServiceException
	 *             client not attached<br>
	 *             body not null after manage call<br>
	 *             manage call to SC failed<br>
	 * @throws UnsupportedEncodingException
	 *             encoding of request URL failed<br>
	 */
	public void clearCache() throws SCServiceException, UnsupportedEncodingException {
		this.clearCache(Constants.DEFAULT_OPERATION_TIMEOUT_SECONDS);
	}

	/**
	 * Clears the cache for given service name.
	 * 
	 * @param operationTimeoutSeconds
	 *            the allowed time in seconds to complete the operation
	 * @throws SCServiceException
	 *             client not attached<br>
	 *             body not null after manage call<br>
	 *             manage call to SC failed<br>
	 * @throws UnsupportedEncodingException
	 *             encoding of request URL failed<br>
	 */
	public void clearCache(int operationTimeoutSeconds) throws SCServiceException, UnsupportedEncodingException {
		if (this.attached == false) {
			throw new SCServiceException("Client not attached - clearCache not possible.");
		}
		String urlString = URLString.toURLRequestString(Constants.CC_CMD_CLEAR_CACHE);
		String body = this.manageCall(operationTimeoutSeconds, urlString);
		if (body != null) {
			throw new SCServiceException(body);
		}
	}

	/**
	 * Request dump of SC. Uses default operation timeout to complete operation.
	 * 
	 * @throws SCServiceException
	 *             client not attached<br>
	 *             manage call to SC failed<br>
	 * @throws UnsupportedEncodingException
	 *             encoding of request URL failed<br>
	 */
	public void dump() throws SCServiceException, UnsupportedEncodingException {
		this.dump(Constants.DEFAULT_OPERATION_TIMEOUT_SECONDS);
	}

	/**
	 * Request dump of SC.
	 * 
	 * @param operationTimeoutSeconds
	 *            the allowed time in seconds to complete the operation
	 * @throws SCServiceException
	 *             client not attached<br>
	 *             manage call to SC failed<br>
	 * @throws UnsupportedEncodingException
	 *             encoding of request URL failed<br>
	 */
	public void dump(int operationTimeoutSeconds) throws SCServiceException, UnsupportedEncodingException {
		if (this.attached == false) {
			throw new SCServiceException("Client not attached - dump not possible.");
		}
		String urlString = URLString.toURLRequestString(Constants.CC_CMD_DUMP);
		this.manageCall(operationTimeoutSeconds, urlString);
	}

	/**
	 * Kill SC.
	 * 
	 * @throws SCServiceException
	 *             client not attached<br>
	 *             manage call to SC failed<br>
	 * @throws UnsupportedEncodingException
	 *             encoding of request URL failed<br>
	 */
	public void killSC() throws SCServiceException, UnsupportedEncodingException {
		if (this.attached == false) {
			throw new SCServiceException("Client not attached - killSC not possible.");
		}
		String urlString = URLString.toURLRequestString(Constants.CC_CMD_KILL);
		this.manageCall(Constants.DEFAULT_OPERATION_TIMEOUT_SECONDS, urlString);
		try {
			// sleep to assure kill is sent
			Thread.sleep(1000);
		} catch (Exception e) {
			// ignore exception
			LOGGER.trace("Kill SC failed.", e);
		}
		this.attached = false;
		// destroy connection pool
		this.requester.destroy();
		synchronized (AppContext.communicatorsLock) {
			AppContext.attachedCommunicators.decrementAndGet();
			// release resources
			AppContext.destroy();
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
	 *             the inspect call to SC failed<br>
	 *             error message received from SC<br>
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
			ex.setSCErrorCode(reply.getHeader(SCMPHeaderAttributeKey.SC_ERROR_CODE));
			ex.setSCErrorText(reply.getHeader(SCMPHeaderAttributeKey.SC_ERROR_TEXT));
			throw ex;
		}
		return (String) reply.getBody();
	}

	/**
	 * Process a manage call.
	 * 
	 * @param operationTimeoutSeconds
	 *            the allowed time in seconds to complete the operation
	 * @param instruction
	 *            the instruction
	 * @return the string
	 * @throws SCServiceException
	 *             manage call to SC failed<br>
	 *             error message received from SC<br>
	 */
	private String manageCall(int operationTimeoutSeconds, String instruction) throws SCServiceException {
		SCMPManageCall manageCall = new SCMPManageCall(this.requester);
		SCServiceCallback callback = new SCServiceCallback(true);
		try {
			manageCall.setRequestBody(instruction);
			manageCall.invoke(callback, operationTimeoutSeconds * Constants.SEC_TO_MILLISEC_FACTOR);
		} catch (Exception e) {
			this.requester.destroy();
			throw new SCServiceException(instruction + " SC failed.", e);
		}
		if (instruction.equalsIgnoreCase(Constants.CC_CMD_KILL)) {
			// kill SC doesn't reply a message
			return null;
		}
		SCMPMessage reply = callback.getMessageSync(operationTimeoutSeconds * Constants.SEC_TO_MILLISEC_FACTOR);
		if (reply.isFault()) {
			SCServiceException ex = new SCServiceException("Manage failed.");
			ex.setSCErrorCode(reply.getHeader(SCMPHeaderAttributeKey.SC_ERROR_CODE));
			ex.setSCErrorText(reply.getHeader(SCMPHeaderAttributeKey.SC_ERROR_TEXT));
			throw ex;
		}
		return (String) reply.getBody();
	}
}
