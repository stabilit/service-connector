/*
 *-----------------------------------------------------------------------------*
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
 *-----------------------------------------------------------------------------*
/*
/**
 * 
 */
package com.stabilit.scm.cln.service;

import com.stabilit.scm.cln.call.ISCMPCall;
import com.stabilit.scm.cln.call.SCMPCallFactory;
import com.stabilit.scm.cln.call.SCMPClnDataCall;
import com.stabilit.scm.cln.scmp.SCMPServiceSession;
import com.stabilit.scm.common.net.req.IRequester;
import com.stabilit.scm.common.scmp.SCMPMessage;

/**
 * @author JTraber
 */
public class SCDataSession extends SCSessionAdapter {

	private String messageInfo;
	private SCMPClnDataCall clnDataCall;
	private ISCMPCall scmpGroupCall;

	public SCDataSession(IRequester req, SCMPServiceSession session) {
		super(req);
		this.scmpGroupCall = null;
		this.messageInfo = null;
		this.clnDataCall = (SCMPClnDataCall) SCMPCallFactory.CLN_DATA_CALL.newInstance(req, session);
	}

	@Override
	public Object invoke() throws Exception {
		SCMPMessage scmpReply = null;

		this.clnDataCall.setMessagInfo(this.messageInfo);
		this.clnDataCall.setRequestBody(this.data);

		if (this.scmpGroupCall != null) {
			scmpReply = this.scmpGroupCall.invoke();
		} else {
			scmpReply = this.clnDataCall.invoke();
		}
		return scmpReply.getBody();
	}

	@Override
	public void setMessagInfo(String messageInfo) {
		this.messageInfo = messageInfo;
	}

	@Override
	public void setData(Object data) {
		this.data = data;
	}

	@Override
	public void closeGroup() throws Exception {
		this.scmpGroupCall.closeGroup(); // send REQ (no body content)
		this.scmpGroupCall = null;
	}

	@Override
	public void openGroup() throws Exception {
		this.scmpGroupCall = this.clnDataCall.openGroup();
	}

	@Override
	public void deleteSession() throws Exception {
		this.session.deleteSession();	
	}
}
