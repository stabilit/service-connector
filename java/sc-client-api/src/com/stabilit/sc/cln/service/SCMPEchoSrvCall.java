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

import java.util.Map;

import com.stabilit.sc.cln.client.IClient;
import com.stabilit.sc.common.io.SCMP;
import com.stabilit.sc.common.io.SCMPFault;
import com.stabilit.sc.common.io.SCMPHeaderAttributeKey;
import com.stabilit.sc.common.io.SCMPMsgType;
import com.stabilit.sc.common.io.SCMPPart;
import com.stabilit.sc.common.io.SCMPPartID;

/**
 * @author JTraber
 * 
 */
public class SCMPEchoSrvCall extends SCMPCallAdapter {

	public SCMPEchoSrvCall() {
		this(null, null);
	}

	public SCMPEchoSrvCall(IClient client, SCMP scmpSession) {
		super(client, scmpSession);
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
	public ISCMPCall newInstance(IClient client, SCMP scmpSession) {
		return new SCMPEchoSrvCall(client, scmpSession);
	}

	public void setServiceName(String serviceName) {
		call.setHeader(SCMPHeaderAttributeKey.SERVICE_NAME, serviceName);
	}

	@Override
	public SCMPMsgType getMessageType() {
		return SCMPMsgType.ECHO_SRV;
	}
	
	public void setHeader(Map<String, String> header) {
		this.call.setHeader(header);		
	}
	
	public void setMaxNodes(int maxNodes) {
		this.call.setHeader(SCMPHeaderAttributeKey.MAX_NODES, String.valueOf(maxNodes));
	}
	
	public void setPartMessage(boolean partMessage) {
		if (partMessage == true) {
			if (this.call.isPart()) {
				return;
			}
			SCMPPart scmpPart = new SCMPPart();
			scmpPart.setHeader(this.call.getHeader());
			scmpPart.setBody(this.call.getBody());
			scmpPart.setHeader(SCMPHeaderAttributeKey.PART_ID, SCMPPartID.getNextAsString());
			this.call = scmpPart;
			return;
		}
		if (this.call.isPart() == false) {
			return;			
		}
		SCMP scmp = new SCMP();
		scmp.setHeader(this.call.getHeader());
		scmp.setBody(this.call.getBody());
		this.call = scmp;
		return;
	}
}