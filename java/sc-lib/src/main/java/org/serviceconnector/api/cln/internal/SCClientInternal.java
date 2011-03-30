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
package org.serviceconnector.api.cln.internal;

import org.apache.log4j.Logger;
import org.serviceconnector.Constants;
import org.serviceconnector.api.SCServiceException;
import org.serviceconnector.api.cln.SCClient;
import org.serviceconnector.api.cln.SCServiceCallback;
import org.serviceconnector.call.SCMPAttachCall;
import org.serviceconnector.call.SCMPDetachCall;
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.conf.RemoteNodeConfiguration;
import org.serviceconnector.net.ConnectionType;
import org.serviceconnector.net.req.SCRequester;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.util.ValidatorUtility;

/**
 * Internal client only usable by SC itself.
 * 
 * @author ds
 */
public class SCClientInternal extends SCClient {

	/** The Constant LOGGER. */
	@SuppressWarnings("unused")
	private static final Logger LOGGER = Logger.getLogger(SCClientInternal.class);

	/**
	 * Instantiates a new SC internal client.
	 * 
	 * @param host
	 *            the host
	 * @param port
	 *            the port
	 */
	public SCClientInternal(String host, int port) {
		super(host, port);
	}

	/**
	 * Instantiates a new SC internal client.
	 * 
	 * @param host
	 *            the host
	 * @param port
	 *            the port
	 * @param connectionType
	 *            the connection type
	 */
	public SCClientInternal(String host, int port, ConnectionType connectionType) {
		super(host, port, connectionType);
	}

	/**
	 * Attach client to SC without touching the application context
	 * 
	 * @param operationTimeoutSeconds
	 *            the allowed time in seconds to complete the operation
	 * @throws SCMPValidatorException
	 *             port is not within limits 0 to 0xFFFF<br>
	 *             host is missing<br>
	 * @throws SCServiceException
	 *             instance already attached before<br>
	 *             attach to host failed<br>
	 *             error message received from SC <br>
	 */
	public synchronized void attach(int operationTimeoutSeconds) throws SCServiceException, SCMPValidatorException {
		// 1. checking preconditions and validate
		if (this.attached) {
			throw new SCServiceException("SCClient already attached.");
		}
		if (this.getHost() == null) {
			throw new SCMPValidatorException("Host is missing.");
		}
		ValidatorUtility.validateInt(Constants.MIN_PORT_VALUE, this.getPort(), Constants.MAX_PORT_VALUE, SCMPError.HV_WRONG_PORTNR);
		this.requester = new SCRequester(new RemoteNodeConfiguration(this.getPort() + "client", this.getHost(), this.getPort(),
				this.getConnectionType().getValue(), this.getKeepAliveIntervalSeconds(), this.getMaxConnections()));
		SCServiceCallback callback = new SCServiceCallback(true);
		SCMPAttachCall attachCall = new SCMPAttachCall(this.requester);
		try {
			attachCall.invoke(callback, operationTimeoutSeconds * Constants.SEC_TO_MILLISEC_FACTOR);
		} catch (Exception e) {
			this.requester.destroy();
			throw new SCServiceException("Attach to " + this.getHost() + ":" + this.getPort() + " failed. ", e);
		}
		// 3. receiving reply and error handling
		SCMPMessage reply = callback.getMessageSync(operationTimeoutSeconds * Constants.SEC_TO_MILLISEC_FACTOR);
		if (reply.isFault()) {
			this.requester.destroy();
			SCServiceException ex = new SCServiceException("Attach to " + this.getHost() + ":" + this.getPort() + " failed.");
			ex.setSCErrorCode(reply.getHeaderInt(SCMPHeaderAttributeKey.SC_ERROR_CODE));
			ex.setSCErrorText(reply.getHeader(SCMPHeaderAttributeKey.SC_ERROR_TEXT));
			throw ex;
		}
		// 4. post process, reply to client
		this.attached = true;
	}
	
	/**
	 * Detach client from SC without touching Application Context
	 * 
	 * @param operationTimeoutSeconds
	 *            the allowed time in seconds to complete the operation
	 * @throws SCServiceException
	 *             detach to host failed<br>
	 *             error message received from SC<br>
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
		}
	}	
}

