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
import com.stabilit.sc.common.scmp.SCMP;
import com.stabilit.sc.common.scmp.SCMPFault;
import com.stabilit.sc.common.scmp.SCMPHeaderAttributeKey;
import com.stabilit.sc.common.scmp.SCMPPart;

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
}