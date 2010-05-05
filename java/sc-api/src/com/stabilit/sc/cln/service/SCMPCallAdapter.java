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
package com.stabilit.sc.cln.service;

import com.stabilit.sc.cln.client.IClient;
import com.stabilit.sc.scmp.SCMP;
import com.stabilit.sc.scmp.SCMPFault;
import com.stabilit.sc.scmp.SCMPHeaderAttributeKey;
import com.stabilit.sc.scmp.SCMPInternalStatus;
import com.stabilit.sc.scmp.SCMPMsgType;
import com.stabilit.sc.scmp.SCMPPart;

/**
 * @author JTraber
 * 
 */
public abstract class SCMPCallAdapter implements ISCMPCall {

	protected IClient client;
	protected SCMP scmpSession;
	protected SCMP call;
	protected SCMP result;

	public SCMPCallAdapter() {
		this(null, null);
	}

	public SCMPCallAdapter(IClient client, SCMP scmpSession) {
		this.client = client;

		this.scmpSession = scmpSession;

		if (this.scmpSession != null) {
			if (this.scmpSession.isPart()) {
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

	@Override
	public ISCMPCall newInstance(IClient client) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ISCMPCall newInstance(IClient client, SCMP scmpSession) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ISCMPCall openGroup() {
		ISCMPCall groupCall = new SCMPGroupCall(this);
		return groupCall;
	}

	@Override
	public SCMP closeGroup() {
		throw new UnsupportedOperationException("not allowed");
	}

	@Override
	public SCMP invoke() throws Exception {
		this.call.setMessageType(getMessageType().getRequestName());
		this.result = client.sendAndReceive(this.call);

		if (this.result.isFault()) {
			throw new SCMPServiceException((SCMPFault) result);
		}
		return this.result;
	}

	@Override
	public SCMP getCall() {
		return call;
	}

	@Override
	public SCMP getResult() {
		return result;
	}

	public void setBody(Object obj) {
		call.setBody(obj);
	}

	public void setCompression(boolean compression) {
		call.setHeader(SCMPHeaderAttributeKey.COMPRESSION, compression);
	}

	// inner class
	public class SCMPGroupCall implements ISCMPCall {
		private ISCMPCall parentCall;
		private SCMPGroupState groupState;

		private SCMPGroupCall(ISCMPCall parentCall) {
			this.parentCall = parentCall;
			this.groupState = SCMPGroupState.OPEN;
		}

		@Override
		public SCMP invoke() throws Exception {
			if (this.groupState == SCMPGroupState.CLOSE) {
				throw new SCMPCallException("group is closed");
			}
			SCMP callSCMP = this.parentCall.getCall();
			// check if parent call is a large call
			if (callSCMP.isLargeMessage()) {
				SCMPCallAdapter.this.call.setInternalStatus(SCMPInternalStatus.GROUP);
				SCMP scmp = this.parentCall.invoke();
				return scmp;
			}
			// set
			if (callSCMP.isPart() == false) {
				SCMPPart scmpPart = new SCMPPart();
				scmpPart.setHeader(callSCMP);
				scmpPart.setBody(callSCMP.getBody());
				SCMPCallAdapter.this.call = scmpPart; // SCMPCallAdapter.this points to this.parentCall
				callSCMP = null;
			}
			SCMP scmp = this.parentCall.invoke();
			return scmp;
		}

		@Override
		public void setBody(Object body) {
			this.parentCall.setBody(body);
		}

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

		@Override
		public ISCMPCall openGroup() {
			throw new UnsupportedOperationException("not allowed");
		}

		@Override
		public SCMPMsgType getMessageType() {
			return parentCall.getMessageType();
		}

		@Override
		public SCMP getCall() {
			return this.parentCall.getCall();
		}

		@Override
		public SCMP getResult() {
			return this.parentCall.getResult();
		}

		@Override
		public ISCMPCall newInstance(IClient client) {
			throw new UnsupportedOperationException("not allowed");
		}

		@Override
		public ISCMPCall newInstance(IClient client, SCMP scmp) {
			throw new UnsupportedOperationException("not allowed");
		}

	}

	private static enum SCMPGroupState {
		OPEN, CLOSE;
	}

}