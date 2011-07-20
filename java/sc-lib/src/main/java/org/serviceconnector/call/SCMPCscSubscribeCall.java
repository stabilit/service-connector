/*
 * -----------------------------------------------------------------------------*
 * *
 * Copyright © 2010 STABILIT Informatik AG, Switzerland *
 * *
 * Licensed under the Apache License, Version 2.0 (the "License"); *
 * you may not use this file except in compliance with the License. *
 * You may obtain a copy of the License at *
 * *
 * http://www.apache.org/licenses/LICENSE-2.0 *
 * *
 * Unless required by applicable law or agreed to in writing, software *
 * distributed under the License is distributed on an "AS IS" BASIS, *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. *
 * See the License for the specific language governing permissions and *
 * limitations under the License. *
 * -----------------------------------------------------------------------------*
 * /*
 * /**
 */
package org.serviceconnector.call;

import java.net.InetAddress;

import org.serviceconnector.Constants;
import org.serviceconnector.net.req.Requester;
import org.serviceconnector.scmp.ISCMPMessageCallback;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.scmp.SCMPMsgType;

/**
 * The Class SCMPCscSubscribeCall.
 */
public class SCMPCscSubscribeCall extends SCMPCallAdapter {

	/**
	 * Instantiates a new SCMP cascaded subscribe call.
	 * 
	 * @param requester
	 *            the requester
	 * @param msgToSend
	 *            the message to send
	 */
	public SCMPCscSubscribeCall(Requester requester, SCMPMessage msgToSend) {
		super(requester, msgToSend);
	}

	@Override
	public void invoke(ISCMPMessageCallback scmpCallback, int timeoutMillis) throws Exception {
		InetAddress localHost = InetAddress.getLocalHost();
		String ipList = this.requestMessage.getHeader(SCMPHeaderAttributeKey.IP_ADDRESS_LIST);
		ipList += Constants.SLASH + localHost.getHostAddress();
		this.requestMessage.setHeader(SCMPHeaderAttributeKey.IP_ADDRESS_LIST, ipList);
		this.setVersion(SCMPMessage.SC_VERSION.toString());
		super.invoke(scmpCallback, timeoutMillis);
	}

	@Override
	public SCMPMsgType getMessageType() {
		return SCMPMsgType.CSC_SUBSCRIBE;
	}
	
	/**
	 * Sets the version.
	 * 
	 * @param version
	 *            the new version
	 */
	private void setVersion(String version) {
		this.requestMessage.setHeader(SCMPHeaderAttributeKey.SC_VERSION, version);
	}
}