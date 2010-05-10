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

import com.stabilit.sc.cln.client.IClient;
import com.stabilit.sc.scmp.SCMP;
import com.stabilit.sc.scmp.SCMPFault;
import com.stabilit.sc.scmp.SCMPHeaderAttributeKey;
import com.stabilit.sc.scmp.SCMPInternalStatus;
import com.stabilit.sc.scmp.SCMPMsgType;
import com.stabilit.sc.scmp.internal.SCMPPart;

/**
 * The Class SCMPCallAdapter. Provides basic functionality for calls.
 * 
 * @author JTraber
 */
public abstract class SCMPCallAdapter implements ISCMPCall {

	/** The client to use for the call. */
	protected IClient client;
	/** The scmp session to use for the call. */
	protected SCMP scmpSession;
	/** The call. */
	protected SCMP call;
	/** The result. */
	protected SCMP result;

	/**
	 * Instantiates a new scmp call adapter.
	 */
	public SCMPCallAdapter() {
		this(null, null);
	}

	/**
	 * Instantiates a new scmp call adapter.
	 * 
	 * @param client
	 *            the client
	 * @param scmpSession
	 *            the scmp session
	 */
	public SCMPCallAdapter(IClient client, SCMP scmpSession) {
		this.client = client;
		this.scmpSession = scmpSession;

		if (this.scmpSession != null) {
			if (this.scmpSession.isPart()) {
				// on SC scmpSession might be a part - call to server must be a part too
				this.call = new SCMPPart();
				this.call.setHeader(this.scmpSession.getHeader());
			} else {
				this.call = new SCMP();
			}
			this.call.setSessionId(scmpSession.getSessionId());
			this.call.setHeader(SCMPHeaderAttributeKey.SERVICE_NAME, scmpSession
					.getHeader(SCMPHeaderAttributeKey.SERVICE_NAME));
		}

		if (this.call == null) {
			this.call = new SCMP();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.stabilit.sc.cln.service.ISCMPCall#newInstance(com.stabilit.sc.cln.client.IClient)
	 */
	@Override
	public ISCMPCall newInstance(IClient client) {
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * @see com.stabilit.sc.cln.service.ISCMPCall#newInstance(com.stabilit.sc.cln.client.IClient,
	 * com.stabilit.sc.scmp.SCMP)
	 */
	@Override
	public ISCMPCall newInstance(IClient client, SCMP scmpSession) {
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * @see com.stabilit.sc.cln.service.ISCMPCall#openGroup()
	 */
	@Override
	public ISCMPCall openGroup() {
		ISCMPCall groupCall = new SCMPGroupCall(this);
		return groupCall;
	}

	/*
	 * (non-Javadoc)
	 * @see com.stabilit.sc.cln.service.ISCMPCall#closeGroup()
	 */
	@Override
	public SCMP closeGroup() {
		throw new UnsupportedOperationException("not allowed");
	}

	/*
	 * (non-Javadoc)
	 * @see com.stabilit.sc.cln.service.ISCMPCall#invoke()
	 */
	@Override
	public SCMP invoke() throws Exception {
		this.call.setMessageType(getMessageType().getRequestName());
		this.result = client.sendAndReceive(this.call);

		if (this.result.isFault()) {
			throw new SCMPCallException((SCMPFault) result);
		}
		return this.result;
	}

	/*
	 * (non-Javadoc)
	 * @see com.stabilit.sc.cln.service.ISCMPCall#getCall()
	 */
	@Override
	public SCMP getCall() {
		return call;
	}

	/*
	 * (non-Javadoc)
	 * @see com.stabilit.sc.cln.service.ISCMPCall#getResult()
	 */
	@Override
	public SCMP getResult() {
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see com.stabilit.sc.cln.service.ISCMPCall#setBody(java.lang.Object)
	 */
	public void setBody(Object obj) {
		call.setBody(obj);
	}

	/**
	 * Sets the compression.
	 * 
	 * @param compression
	 *            the new compression
	 */
	public void setCompression(boolean compression) {
		call.setHeader(SCMPHeaderAttributeKey.COMPRESSION, compression);
	}

	/**
	 * The Class SCMPGroupCall. Inner class - allows to open a group for messages. Sends parts as long as group is
	 * not closed. Very useful if you don't know how big your complete message is going to be.
	 */
	public class SCMPGroupCall implements ISCMPCall {

		/** The parent call. */
		private ISCMPCall parentCall;
		/** The group state. */
		private SCMPGroupState groupState;

		/**
		 * Instantiates a new sCMP group call.
		 * 
		 * @param parentCall
		 *            the parent call
		 */
		private SCMPGroupCall(ISCMPCall parentCall) {
			this.parentCall = parentCall;
			this.groupState = SCMPGroupState.OPEN;
		}

		/*
		 * (non-Javadoc)
		 * @see com.stabilit.sc.cln.service.ISCMPCall#invoke()
		 */
		@Override
		public SCMP invoke() throws Exception {
			if (this.groupState == SCMPGroupState.CLOSE) {
				throw new SCMPCallException("group is closed");
			}
			SCMP callSCMP = this.parentCall.getCall();
			SCMPCallAdapter.this.call.setInternalStatus(SCMPInternalStatus.GROUP);

			if (callSCMP.isLargeMessage()) {
				// parent call is large no need to change anything
				SCMP scmp = this.parentCall.invoke();
				return scmp;
			}
			if (callSCMP.isPart() == false) {
				// callSCMP is small and not part but inside a group only parts are allowed
				SCMPPart scmpPart = new SCMPPart();
				scmpPart.setHeader(callSCMP);
				scmpPart.setBody(callSCMP.getBody());
				SCMPCallAdapter.this.call = scmpPart; // SCMPCallAdapter.this points to this.parentCall
				callSCMP = null;
			}
			SCMP scmp = this.parentCall.invoke();
			return scmp;
		}

		/*
		 * (non-Javadoc)
		 * @see com.stabilit.sc.cln.service.ISCMPCall#setBody(java.lang.Object)
		 */
		@Override
		public void setBody(Object body) {
			this.parentCall.setBody(body);
		}

		/*
		 * (non-Javadoc)
		 * @see com.stabilit.sc.cln.service.ISCMPCall#closeGroup()
		 */
		@Override
		public SCMP closeGroup() throws Exception {
			this.groupState = SCMPGroupState.CLOSE;
			// send empty closing REQ
			SCMP scmp = new SCMP();
			scmp.setHeader(SCMPCallAdapter.this.call);
			scmp.setBody(null);
			scmp.setInternalStatus(SCMPInternalStatus.GROUP);
			SCMPCallAdapter.this.call = scmp;
			SCMP result = this.parentCall.invoke();
			return result;
		}

		/*
		 * (non-Javadoc)
		 * @see com.stabilit.sc.cln.service.ISCMPCall#openGroup()
		 */
		@Override
		public ISCMPCall openGroup() {
			throw new UnsupportedOperationException("not allowed");
		}

		/*
		 * (non-Javadoc)
		 * @see com.stabilit.sc.cln.service.ISCMPCall#getMessageType()
		 */
		@Override
		public SCMPMsgType getMessageType() {
			return parentCall.getMessageType();
		}

		/*
		 * (non-Javadoc)
		 * @see com.stabilit.sc.cln.service.ISCMPCall#getCall()
		 */
		@Override
		public SCMP getCall() {
			return this.parentCall.getCall();
		}

		/*
		 * (non-Javadoc)
		 * @see com.stabilit.sc.cln.service.ISCMPCall#getResult()
		 */
		@Override
		public SCMP getResult() {
			return this.parentCall.getResult();
		}

		/*
		 * (non-Javadoc)
		 * @see com.stabilit.sc.cln.service.ISCMPCall#newInstance(com.stabilit.sc.cln.client.IClient)
		 */
		@Override
		public ISCMPCall newInstance(IClient client) {
			throw new UnsupportedOperationException("not allowed");
		}

		/*
		 * (non-Javadoc)
		 * @see com.stabilit.sc.cln.service.ISCMPCall#newInstance(com.stabilit.sc.cln.client.IClient,
		 * com.stabilit.sc.scmp.SCMP)
		 */
		@Override
		public ISCMPCall newInstance(IClient client, SCMP scmp) {
			throw new UnsupportedOperationException("not allowed");
		}
	}

	/**
	 * The Enum SCMPGroupState.
	 */
	private static enum SCMPGroupState {
		/** The OPEN of a group. */
		OPEN,
		/** The CLOSE of a group. */
		CLOSE;
	}
}