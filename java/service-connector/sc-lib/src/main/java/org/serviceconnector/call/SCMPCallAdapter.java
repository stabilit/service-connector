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
package org.serviceconnector.call;

import org.serviceconnector.net.req.IRequester;
import org.serviceconnector.scmp.ISCMPMessageCallback;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.scmp.SCMPVersion;

/**
 * The Class SCMPCallAdapter. Provides basic functionality for calls.
 * 
 * @author JTraber
 */
public abstract class SCMPCallAdapter implements ISCMPCall {

	/** The client to used to invoke the call. */
	protected IRequester requester;
	/** The session id to use for the call. */
	protected String sessionId;
	/** The service name. */
	protected String serviceName;
	/** The request message. */
	protected SCMPMessage requestMessage;
	/** The response message. */
	protected SCMPMessage responseMessage;

	/**
	 * Instantiates a new SCMPCallAdapter.
	 * 
	 * @param requester
	 *            the requester
	 */
	public SCMPCallAdapter(IRequester requester) {
		this.requester = requester;
		this.requestMessage = new SCMPMessage(SCMPVersion.CURRENT);
	}

	/**
	 * Instantiates a new SCMP call adapter.
	 * 
	 * @param requester
	 *            the requester
	 * @param serviceName
	 *            the service name
	 */
	public SCMPCallAdapter(IRequester requester, String serviceName) {
		this(requester);
		this.requestMessage.setHeader(SCMPHeaderAttributeKey.SERVICE_NAME, serviceName);
	}

	/**
	 * Instantiates a new SCMPCallAdapter.
	 * 
	 * @param requester
	 *            the requester
	 * @param serviceName
	 *            the service name
	 * @param sessionId
	 *            the session id
	 */
	public SCMPCallAdapter(IRequester requester, String serviceName, String sessionId) {
		this(requester, serviceName);
		this.sessionId = sessionId;
		this.requestMessage.setSessionId(sessionId);
	}

	/**
	 * Instantiates a new SCMPCallAdapter. Constructor is necessary because in SC you need to hand over received
	 * message.
	 * 
	 * @param requester
	 *            the requester
	 * @param messageToForward
	 *            the message
	 */
	public SCMPCallAdapter(IRequester requester, SCMPMessage messageToForward) {
		this.requester = requester;
		this.requestMessage = messageToForward;
	}

	/** {@inheritDoc} */
	@Override
	public void invoke(ISCMPMessageCallback callback, int timeoutMillis) throws Exception {
		this.requestMessage.setMessageType(this.getMessageType());
		this.requestMessage.setHeader(SCMPHeaderAttributeKey.OPERATION_TIMEOUT, timeoutMillis);
		this.requester.send(this.requestMessage, timeoutMillis, callback);
		return;
	}

	/** {@inheritDoc} */
	@Override
	public void setRequestBody(Object obj) {
		throw new UnsupportedOperationException();
	}

	/** {@inheritDoc} */
	@Override
	public SCMPMessage getRequest() {
		return requestMessage;
	}

	/** {@inheritDoc} */
	@Override
	public SCMPMessage getResponse() {
		return responseMessage;
	}
}