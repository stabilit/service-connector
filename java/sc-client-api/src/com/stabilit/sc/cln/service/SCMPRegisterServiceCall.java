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
import com.stabilit.sc.common.io.SCMPHeaderAttributeKey;
import com.stabilit.sc.common.io.SCMPMsgType;

/**
 * @author JTraber
 * 
 */
public class SCMPRegisterServiceCall extends SCMPCallAdapter {
	
	public SCMPRegisterServiceCall() {
		this(null);
	}

	public SCMPRegisterServiceCall(IClient client) {
		this.client = client;
	}

	@Override
	public ISCMPCall newInstance(IClient client) {
		return new SCMPRegisterServiceCall(client);
	}
	
	public void setServiceName(String serviceName) {
		call.setHeader(SCMPHeaderAttributeKey.SERVICE_NAME, serviceName);
	}
	
	public void setMaxSessions(int maxSessions) {
		call.setHeader(SCMPHeaderAttributeKey.MAX_SESSIONS, maxSessions);
	}
	
	public void setMultithreaded(boolean multiThreaded) {
		call.setHeader(SCMPHeaderAttributeKey.MULTI_THREADED, multiThreaded);
	}
	
	public void setPortNumber(int portNumber) {
		call.setHeader(SCMPHeaderAttributeKey.PORT_NR, portNumber);
	}	
	
	@Override
	public SCMPMsgType getMessageType() {
		return SCMPMsgType.REGISTER_SERVICE;
	}
}
