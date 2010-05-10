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

import com.stabilit.sc.cln.call.ISCMPCall;
import com.stabilit.sc.cln.call.SCMPCallAdapter;
import com.stabilit.sc.cln.call.SCMPCallException;
import com.stabilit.sc.cln.client.IClient;
import com.stabilit.sc.scmp.SCMP;
import com.stabilit.sc.scmp.SCMPFault;
import com.stabilit.sc.scmp.SCMPHeaderAttributeKey;
import com.stabilit.sc.scmp.SCMPMsgType;

/**
 * The Class SCMPSrvEchoCall. Call sends echo to backend server.
 * 
 * @author JTraber
 */
public class SCMPSrvEchoCall extends SCMPCallAdapter {

	/**
	 * Instantiates a new SCMPSrvEchoCall.
	 */
	public SCMPSrvEchoCall() {
		this(null, null);
	}

	/**
	 * Instantiates a new SCMPSrvEchoCall.
	 * 
	 * @param client
	 *            the client
	 * @param scmpSession
	 *            the scmp session
	 */
	public SCMPSrvEchoCall(IClient client, SCMP scmpSession) {
		super(client, scmpSession);
	}

	/**
	 * Invoke.
	 * 
	 * @return the scmp
	 * @throws Exception
	 *             the exception
	 */
	@Override
	public SCMP invoke() throws Exception {
		this.call.setMessageType(getMessageType().getRequestName());
		this.call.setHeader(SCMPHeaderAttributeKey.SCCLIENT_ID, client.hashCode());
		this.result = client.sendAndReceive(this.call);
		if (this.result.isFault()) {
			throw new SCMPCallException((SCMPFault) result);
		}
		return this.result;
	}

	/**
	 * New instance.
	 * 
	 * @param client
	 *            the client
	 * @param scmpSession
	 *            the scmp session
	 * @return the iSCMP call
	 */
	@Override
	public ISCMPCall newInstance(IClient client, SCMP scmpSession) {
		return new SCMPSrvEchoCall(client, scmpSession);
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
	 * Gets the message type.
	 * 
	 * @return the message type
	 */
	@Override
	public SCMPMsgType getMessageType() {
		return SCMPMsgType.SRV_ECHO;
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

	/**
	 * Sets the header.
	 * 
	 * @param attr
	 *            the attribute
	 * @param value
	 *            the value
	 */
	public void setHeader(SCMPHeaderAttributeKey attr, String value) {
		this.call.setHeader(attr, value);
	}

	/**
	 * Sets the header.
	 * 
	 * @param attr
	 *            the attribute
	 * @param value
	 *            the value
	 */
	public void setHeader(SCMPHeaderAttributeKey attr, int value) {
		this.call.setHeader(attr, value);
	}

}