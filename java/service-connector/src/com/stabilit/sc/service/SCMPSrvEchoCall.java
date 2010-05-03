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
package com.stabilit.sc.service;

import java.util.Map;

import com.stabilit.sc.cln.client.IClient;
import com.stabilit.sc.cln.service.ISCMPCall;
import com.stabilit.sc.cln.service.SCMPCallAdapter;
import com.stabilit.sc.cln.service.SCMPServiceException;
import com.stabilit.sc.common.scmp.SCMP;
import com.stabilit.sc.common.scmp.SCMPFault;
import com.stabilit.sc.common.scmp.SCMPHeaderAttributeKey;
import com.stabilit.sc.common.scmp.SCMPMsgType;

/**
 * @author JTraber
 * 
 */
public class SCMPSrvEchoCall extends SCMPCallAdapter {

	public SCMPSrvEchoCall() {
		this(null, null);
	}

	public SCMPSrvEchoCall(IClient client, SCMP scmpSession) {
		super(client, scmpSession);
	}

	@Override
	public SCMP invoke() throws Exception {
		this.call.setMessageType(getMessageType().getRequestName());
		this.call.setHeader(SCMPHeaderAttributeKey.SCCLIENT_ID, client.hashCode());
		this.result = client.sendAndReceive(this.call);
		if (this.result.isFault()) {
			throw new SCMPServiceException((SCMPFault) result);
		}
		return this.result;
	}

	@Override
	public ISCMPCall newInstance(IClient client, SCMP scmpSession) {
		return new SCMPSrvEchoCall(client, scmpSession);
	}

	public void setServiceName(String serviceName) {
		call.setHeader(SCMPHeaderAttributeKey.SERVICE_NAME, serviceName);
	}

	@Override
	public SCMPMsgType getMessageType() {
		return SCMPMsgType.SRV_ECHO;
	}
	
	public void setHeader(Map<String, String> header) {
		this.call.setHeader(header);		
	}
	
	public void setHeader(SCMPHeaderAttributeKey attr, String value) {
		this.call.setHeader(attr, value);		
	}

	public void setHeader(SCMPHeaderAttributeKey attr, int value) {
		this.call.setHeader(attr, value);		
	}

}