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
import com.stabilit.sc.cln.scmp.SCMPSession;
import com.stabilit.sc.scmp.SCMP;
import com.stabilit.sc.scmp.SCMPHeaderAttributeKey;
import com.stabilit.sc.scmp.SCMPMsgType;

/**
 * @author JTraber
 * 
 */
public class SCMPClnDeleteSessionCall extends SCMPCallAdapter {

	public SCMPClnDeleteSessionCall() {
		this(null, null);
	}

	public SCMPClnDeleteSessionCall(IClient client, SCMP scmpSession) {
		super(client, scmpSession);
	}

	@Override
	public ISCMPCall newInstance(IClient client, SCMP scmpSession) {
		return new SCMPClnDeleteSessionCall(client, scmpSession);
	}

	public void setServiceName(String serviceName) {
		call.setHeader(SCMPHeaderAttributeKey.SERVICE_NAME, serviceName);
	}

	@Override
	public SCMP invoke() throws Exception {
		super.invoke(); // throws exception in case of error
		if (this.scmpSession != null && this.scmpSession instanceof SCMPSession) {
			((SCMPSession) this.scmpSession).removeSessionRegistry();
		}
		return this.result;
	}

	@Override
	public SCMPMsgType getMessageType() {
		return SCMPMsgType.CLN_DELETE_SESSION;
	}
}
