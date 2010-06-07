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

import com.stabilit.scm.cln.call.SCMPCallFactory;
import com.stabilit.scm.cln.call.SCMPClnDataCall;
import com.stabilit.scm.scmp.SCMPMessage;

/**
 * @author JTraber
 *
 */
public class SCDataService extends SCServiceAdapter {

	private String messageInfo;
	
	@Override
	public Object invoke() throws Exception {
		// data call - session is stored inside client!!
		SCMPClnDataCall clnDataCall = (SCMPClnDataCall) SCMPCallFactory.CLN_DATA_CALL.newInstance(client, session);
		clnDataCall.setMessagInfo(this.messageInfo);
		clnDataCall.setRequestBody(this.data);
		SCMPMessage scmpReply = clnDataCall.invoke();
		return scmpReply.getBody();
	}

	@Override
	public void setMessagInfo(String messageInfo) {
	     this.messageInfo = messageInfo;	
	}

}
