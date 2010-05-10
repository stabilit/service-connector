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

import java.util.Map;

import com.stabilit.sc.cln.client.IClient;
import com.stabilit.sc.scmp.SCMP;
import com.stabilit.sc.scmp.SCMPFault;
import com.stabilit.sc.scmp.SCMPHeaderAttributeKey;
import com.stabilit.sc.scmp.SCMPMsgType;

/**
 * The Class SCMPEchoSCCall. Call sends echo to SC.
 * 
 * @author JTraber
 */
public class SCMPEchoSCCall extends SCMPCallAdapter {

	/**
	 * Instantiates a new SCMPEchoSCCall.
	 */
	public SCMPEchoSCCall() {
		this(null);
	}

	/**
	 * Instantiates a new SCMPEchoSCCall.
	 * 
	 * @param client
	 *            the client to use when invoking call
	 */
	public SCMPEchoSCCall(IClient client) {
		this.client = client;
	}

	/*
	 * (non-Javadoc)
	 * @see com.stabilit.sc.cln.service.SCMPCallAdapter#invoke()
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
	 * @see com.stabilit.sc.cln.service.SCMPCallAdapter#newInstance(com.stabilit.sc.cln.client.IClient)
	 */
	@Override
	public ISCMPCall newInstance(IClient client) {
		return new SCMPEchoSCCall(client);
	}

	/*
	 * (non-Javadoc)
	 * @see com.stabilit.sc.cln.service.ISCMPCall#getMessageType()
	 */
	@Override
	public SCMPMsgType getMessageType() {
		return SCMPMsgType.ECHO_SC;
	}

	/**
	 * Sets the service name.
	 * 
	 * @param serviceName
	 *            the new service name
	 */
	public void setServiceName(String serviceName) {
		call.setHeader(SCMPHeaderAttributeKey.SERVICE_NAME, serviceName);
	}

	/**
	 * Sets the header.
	 * 
	 * @param header
	 *            the header
	 */
	public void setHeader(Map<String, String> header) {
		this.call.setHeader(header);
	}
}