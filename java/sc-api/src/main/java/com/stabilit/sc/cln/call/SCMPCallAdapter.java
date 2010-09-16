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
package com.stabilit.sc.cln.call;

import org.apache.log4j.Logger;

import com.stabilit.sc.common.net.req.IRequester;
import com.stabilit.sc.common.scmp.ISCMPCallback;
import com.stabilit.sc.common.scmp.SCMPHeaderAttributeKey;
import com.stabilit.sc.common.scmp.SCMPMessage;
import com.stabilit.sc.common.scmp.SCMPMsgType;
import com.stabilit.sc.common.scmp.internal.SCMPInternalStatus;
import com.stabilit.sc.common.scmp.internal.SCMPPart;

/**
 * The Class SCMPCallAdapter. Provides basic functionality for calls.
 * 
 * @author JTraber
 */
public abstract class SCMPCallAdapter implements ISCMPCall {

	/** The Constant logger. */
	protected static final Logger logger = Logger.getLogger(SCMPCallAdapter.class);

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
	 */
	public SCMPCallAdapter() {
		this(null, null, null);
	}

	/**
	 * Instantiates a new SCMPCallAdapter.
	 * 
	 * @param requester
	 *            the requester
	 */
	public SCMPCallAdapter(IRequester requester) {
		this.requester = requester;
		this.requestMessage = new SCMPMessage();
	}

	/**
	 * Instantiates a new scmp call adapter.
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

	/** {@inheritDoc} */
	@Override
	public ISCMPCall newInstance(IRequester requester) {
		throw new UnsupportedOperationException("not allowed");
	}

	/** {@inheritDoc} */
	@Override
	public ISCMPCall newInstance(IRequester requester, String serviceName) {
		throw new UnsupportedOperationException("not allowed");
	}

	/** {@inheritDoc} */
	@Override
	public ISCMPCall newInstance(IRequester requester, SCMPMessage receivedMessage) {
		throw new UnsupportedOperationException("not allowed");
	}

	/** {@inheritDoc} */
	@Override
	public ISCMPCall newInstance(IRequester requester, String serviceName, String sessionId) {
		throw new UnsupportedOperationException("not allowed");
	}

	/** {@inheritDoc} */
	@Override
	public ISCMPCall openGroup() {
		ISCMPCall groupCall = new SCMPGroupCall(this);
		return groupCall;
	}

	/** {@inheritDoc} */
	@Override
	public void closeGroup(ISCMPCallback callback, double timeoutMillis) {
		throw new UnsupportedOperationException("not allowed");
	}

	/** {@inheritDoc} */
	@Override
	public void invoke(ISCMPCallback callback, double timeoutInMillis) throws Exception {
		this.requestMessage.setMessageType(this.getMessageType());
		this.requestMessage.setHeader(SCMPHeaderAttributeKey.OPERATION_TIMEOUT, (int) timeoutInMillis);
		this.requester.send(this.requestMessage, timeoutInMillis, callback);
		return;
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

	/** {@inheritDoc} */
	public void setRequestBody(Object obj) {
		this.requestMessage.setBody(obj);
	}

	/**
	 * The Class SCMPGroupCall. A group call is a summary of individual single calls. Each single call can be a large or
	 * small message request and response. But all of them are handled as partial messages, large calls will be split
	 * into partial calls (PRQ). The client uses group calls if the active communication is open end. Closing the group
	 * will send the completing request (REQ). <br>
	 * Communication sample: <br>
	 * openGroup (no transport) <br>
	 * PRQ -> <-PRS <br>
	 * .... <br>
	 * PRQ-> <-PRS <br>
	 * closeGroup (terminates group) <br>
	 * REQ-> <-RES <br>
	 */
	public final class SCMPGroupCall implements ISCMPCall {

		/** The parent call. */
		private ISCMPCall parentCall;
		/** The group state. */
		private SCMPGroupState groupState;

		/**
		 * Instantiates a new SCMPGroupCall.
		 * 
		 * @param parentCall
		 *            the parent call
		 */
		private SCMPGroupCall(ISCMPCall parentCall) {
			this.parentCall = parentCall;
			this.groupState = SCMPGroupState.OPEN;
		}

		/** {@inheritDoc} */
		@Override
		public void invoke(ISCMPCallback callback, double timeoutInMillis) throws Exception {
			if (this.groupState == SCMPGroupState.CLOSE) {
				logger.warn("tried to invoke groupCall but state of group is closed");
			}
			SCMPMessage callSCMP = this.parentCall.getRequest();
			SCMPCallAdapter.this.requestMessage.setInternalStatus(SCMPInternalStatus.GROUP);

			if (callSCMP.isLargeMessage()) {
				// parent call is large no need to change anything
				this.parentCall.invoke(callback, timeoutInMillis);
				return;
			}
			if (callSCMP.isPart() == false) {
				// callSCMP is small and not part but inside a group only parts are allowed
				SCMPPart scmpPart = new SCMPPart();
				scmpPart.setHeader(callSCMP);
				scmpPart.setBody(callSCMP.getBody());
				SCMPCallAdapter.this.requestMessage = scmpPart; // SCMPCallAdapter.this points to this.parentCall
				callSCMP = null;
			}
			this.parentCall.invoke(callback, timeoutInMillis);
			return;
		}

		/** {@inheritDoc} */
		@Override
		public void setRequestBody(Object body) {
			this.parentCall.setRequestBody(body);
		}

		/** {@inheritDoc} */
		@Override
		public void closeGroup(ISCMPCallback callback, double timeoutMillis) throws Exception {
			this.groupState = SCMPGroupState.CLOSE;
			// send empty closing REQ
			SCMPMessage message = new SCMPMessage();
			message.setHeader(SCMPCallAdapter.this.requestMessage);
			message.setBody(null);
			message.setInternalStatus(SCMPInternalStatus.GROUP);
			SCMPCallAdapter.this.requestMessage = message;
			this.parentCall.invoke(callback, timeoutMillis);
			return;
		}

		/** {@inheritDoc} */
		@Override
		public ISCMPCall openGroup() {
			throw new UnsupportedOperationException("not allowed");
		}

		/** {@inheritDoc} */
		@Override
		public SCMPMsgType getMessageType() {
			return parentCall.getMessageType();
		}

		/** {@inheritDoc} */
		@Override
		public SCMPMessage getRequest() {
			return this.parentCall.getRequest();
		}

		/** {@inheritDoc} */
		@Override
		public SCMPMessage getResponse() {
			return this.parentCall.getResponse();
		}

		/** {@inheritDoc} */
		@Override
		public ISCMPCall newInstance(IRequester requester, String sessionId) {
			throw new UnsupportedOperationException("not allowed");
		}

		/** {@inheritDoc} */
		@Override
		public ISCMPCall newInstance(IRequester requester, SCMPMessage scmpMessage) {
			throw new UnsupportedOperationException("not allowed");
		}

		/** {@inheritDoc} */
		@Override
		public ISCMPCall newInstance(IRequester requester, String serviceName, String sessionId) {
			throw new UnsupportedOperationException("not allowed");
		}

		/** {@inheritDoc} */
		@Override
		public ISCMPCall newInstance(IRequester requester) {
			throw new UnsupportedOperationException("not allowed");
		}
	}

	/**
	 * The Enum SCMPGroupState. States which a group call can be.
	 */
	private static enum SCMPGroupState {

		/** The OPEN state. */
		OPEN,

		/** The CLOSE state. */
		CLOSE;
	}
}