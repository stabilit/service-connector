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
import org.serviceconnector.util.URLCallString;
import org.serviceconnector.util.URLParameterString;

/**
 * Management client to an SC.
 * 
 * @author JTrnka
 */
public class SCMgmtClient extends SCClient {

	/** The Constant logger. */
	@SuppressWarnings("unused")
	private final static Logger logger = Logger.getLogger(SCMgmtClient.class);

	public SCMgmtClient(String host, int port) {
		super(host, port);
	}

	public SCMgmtClient(String host, int port, ConnectionType connectionType) {
		super(host, port, connectionType);
	}

	/**
	 * Disable service on SC.
	 * 
	 * @param serviceName
	 * @throws SCServiceException
	 */
	public void disableService(String serviceName) throws SCServiceException {
		this.disableService(Constants.DEFAULT_OPERATION_TIMEOUT_SECONDS, serviceName);
	}

	/**
	 * Disable service on SC.
	 * 
	 * @param operationTimeoutSeconds
	 *            the allowed time in seconds to complete the operation until it stops
	 * @param serviceName
	 * @throws SCServiceException
	 */
	public void disableService(int operationTimeout, String serviceName) throws SCServiceException {
		if (this.attached == false) {
			// disableService not possible - client not attached
			throw new SCServiceException("Client not attached - disableService not possible.");
		}
		String body = this.manageCall(operationTimeout, Constants.CC_CMD_DISABLE + Constants.EQUAL_SIGN + serviceName);
		if (body != null) {
			throw new SCServiceException(body);
		}
	}

	/**
	 * Disable service on SC.
	 * 
	 * @param serviceName
	 * @throws SCServiceException
	 */
	public void enableService(String serviceName) throws SCServiceException {
		this.enableService(Constants.DEFAULT_OPERATION_TIMEOUT_SECONDS, serviceName);
	}

	/**
	 * Enable service on SC.
	 * 
	 * @param operationTimeoutSeconds
	 *            the allowed time in seconds to complete the operation until it stops
	 * @param serviceName
	 *            the service name
	 */
	public void enableService(int operationTimeout, String serviceName) throws SCServiceException {
		if (this.attached == false) {
			// enableService not possible - client not attached
			throw new SCServiceException("Client not attached - enableService not possible.");
		}
		String body = this.manageCall(operationTimeout, Constants.CC_CMD_ENABLE + Constants.EQUAL_SIGN + serviceName);
		if (body != null) {
			throw new SCServiceException(body);
		}
	}

	/**
	 * Checks if service is enabled on SC.
	 * 
	 * @param serviceName
	 *            the service name
	 * @return true, if is service enabled
	 */
	public boolean isServiceEnabled(String serviceName) throws SCServiceException {
		return this.isServiceEnabled(Constants.DEFAULT_OPERATION_TIMEOUT_SECONDS, serviceName);
	}

	/**
	 * Checks if service is enabled on SC.
	 * 
	 * @param operationTimeoutSeconds
	 *            the allowed time in seconds to complete the operation until it stops
	 * @param serviceName
	 *            the service name
	 * @return true, if is service enabled
	 */
	public boolean isServiceEnabled(int operationTimeout, String serviceName) throws SCServiceException {
		if (this.attached == false) {
			throw new SCServiceException("Client not attached - isServiceEnabled not possible.");
		}
		String body = this.inspectCall(operationTimeout, Constants.CC_CMD_STATE + Constants.EQUAL_SIGN + serviceName);
		if (Constants.CC_CMD_ENABLE.equalsIgnoreCase(body)) {
			return true;
		}
		return false;
	}

	/**
	 * Returns the number of available and allocated sessions for given service name.
	 * 
	 * @param serviceName
	 *            the service name
	 * @return string containing the available and allocated sessions, e.g. "4/2".
	 * @throws SCServiceException
	 *             the SC service exception
	 */
	public String getWorkload(String serviceName) throws SCServiceException {
		return this.getWorkload(Constants.DEFAULT_OPERATION_TIMEOUT_SECONDS, serviceName);
	}

	/**
	 * Returns the number of available and allocated sessions for given service name.
	 * 
	 * @param operationTimeoutSeconds
	 *            the allowed time in seconds to complete the operation until it stops
	 * @param serviceName
	 *            the service name
	 * @return string containing the available and allocated sessions, e.g. "4/2".
	 * @throws SCServiceException
	 *             the SC service exception
	 */
	public String getWorkload(int operationTimeout, String serviceName) throws SCServiceException {
		if (this.attached == false) {
			throw new SCServiceException("Client not attached - isServiceEnabled not possible.");
		}
		return this.inspectCall(operationTimeout, Constants.CC_CMD_SESSIONS + Constants.EQUAL_SIGN + serviceName);
	}

	/**
	 * inspects the cache for given service name and cacheId.
	 * 
	 * @param serviceName
	 *            the service name
	 * @param cacheId
	 *            the cache id
	 * @throws SCServiceException
	 *             the SC service exception
	 */
	public URLParameterString inspectCache(String serviceName, String cacheId) throws SCServiceException {
		return this.inspectCache(Constants.DEFAULT_OPERATION_TIMEOUT_SECONDS, serviceName, cacheId);
	}

	/**
	 * inspects the cache for given service name and cacheId.
	 * 
	 * @param operationTimeoutSeconds
	 *            the allowed time in seconds to complete the operation until it stops
	 * @param serviceName
	 *            the service name
	 * @param cacheId
	 *            the cache id
	 * @throws SCServiceException
	 *             the SC service exception
	 */
	public URLParameterString inspectCache(int operationTimeout, String serviceName, String cacheId) throws SCServiceException {
		if (this.attached == false) {
			throw new SCServiceException("Client not attached - inspectCache not possible.");
		}
		URLCallString callString = new URLCallString(Constants.CC_CMD_INSPECT_CACHE, serviceName, cacheId);
		String body = this.inspectCall(Constants.DEFAULT_OPERATION_TIMEOUT_SECONDS, callString.toString());
		if (body == null) {
			throw new SCServiceException(body);
		}
		try {
			return new URLParameterString(body);
		} catch (UnsupportedEncodingException e) {
			throw new SCServiceException(e.toString());
		}
	}

	/**
	 * Clears the cache.
	 * 
	 * @throws SCServiceException
	 *             the SC service exception
	 */
	public void clearCache() throws SCServiceException {
		this.clearCache(Constants.DEFAULT_OPERATION_TIMEOUT_SECONDS);
	}

	/**
	 * Clears the cache for given service name.
	 * 
	 * @param operationTimeoutSeconds
	 *            the allowed time in seconds to complete the operation until it stops
	 * @throws SCServiceException
	 *             the SC service exception
	 */
	public void clearCache(int operationTimeout) throws SCServiceException {
		if (this.attached == false) {
			throw new SCServiceException("Client not attached - clearCache not possible.");
		}
		String body = this.manageCall(operationTimeout, Constants.CC_CMD_CLEAR_CACHE);
		if (body != null) {
			throw new SCServiceException(body);
		}
	}

	/**
	 * Request dump.
	 * 
	 * @throws SCServiceException
	 *             the SC service exception
	 */
	public void dump() throws SCServiceException {
		this.dump(Constants.DEFAULT_OPERATION_TIMEOUT_SECONDS);
	}

	/**
	 * Request dump.
	 * 
	 * @param operationTimeoutSeconds
	 *            the allowed time in seconds to complete the operation until it stops
	 * @throws SCServiceException
	 *             the SC service exception
	 */
	public void dump(int operationTimeout) throws SCServiceException {
		if (this.attached == false) {
			throw new SCServiceException("Client not attached - dump not possible.");
		}
		this.manageCall(operationTimeout, Constants.CC_CMD_DUMP);
	}

	/**
	 * Kill SC.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	public void killSC() throws SCServiceException {
		if (this.attached == false) {
			throw new SCServiceException("Client not attached - killSC not possible.");
		}
		this.manageCall(Constants.DEFAULT_OPERATION_TIMEOUT_SECONDS, Constants.CC_CMD_KILL);
		try {
			// sleep to assure kill is sent
			Thread.sleep(1000);
		} catch (Exception e) {
			// ignore exception
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
	 *            the allowed time in seconds to complete the operation until it stops
	 * @param instruction
	 *            the instruction
	 * @return the string
	 * @throws SCServiceException
	 *             the sC service exception
	 */
	private String inspectCall(int operationTimeout, String instruction) throws SCServiceException {
		SCMPInspectCall inspectCall = new SCMPInspectCall(this.requester);
		SCServiceCallback callback = new SCServiceCallback(true);
		try {
			inspectCall.setRequestBody(instruction);
			inspectCall.invoke(callback, operationTimeout * Constants.SEC_TO_MILLISEC_FACTOR);
		} catch (Exception e) {
			this.requester.destroy();
			throw new SCServiceException("Inspect request failed. ", e);
		}
		if (instruction.equalsIgnoreCase(Constants.CC_CMD_KILL)) {
			// on KILL SC cannot reply a message
			return null;
		}
		SCMPMessage reply = callback.getMessageSync(operationTimeout * Constants.SEC_TO_MILLISEC_FACTOR);
		if (reply.isFault()) {
			SCServiceException ex = new SCServiceException("inspect failed");
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
	 *            the allowed time in seconds to complete the operation until it stops
	 * @param instruction
	 *            the instruction
	 * @return the string
	 * @throws SCServiceException
	 *             the SC service exception
	 */
	private String manageCall(int operationTimeout, String instruction) throws SCServiceException {
		SCMPManageCall manageCall = new SCMPManageCall(this.requester);
		SCServiceCallback callback = new SCServiceCallback(true);
		try {
			manageCall.setRequestBody(instruction);
			manageCall.invoke(callback, operationTimeout * Constants.SEC_TO_MILLISEC_FACTOR);
		} catch (Exception e) {
			this.requester.destroy();
			throw new SCServiceException(instruction + " SC failed", e);
		}
		if (instruction.equalsIgnoreCase(Constants.CC_CMD_KILL)) {
			// kill SC doesn't reply a message
			return null;
		}
		SCMPMessage reply = callback.getMessageSync(operationTimeout * Constants.SEC_TO_MILLISEC_FACTOR);
		if (reply.isFault()) {
			SCServiceException ex = new SCServiceException("Manage failed.");
			ex.setSCErrorCode(reply.getHeader(SCMPHeaderAttributeKey.SC_ERROR_CODE));
			ex.setSCErrorText(reply.getHeader(SCMPHeaderAttributeKey.SC_ERROR_TEXT));
			throw ex;
		}
		return (String) reply.getBody();
	}
}
