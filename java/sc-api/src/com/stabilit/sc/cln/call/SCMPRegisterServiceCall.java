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

import com.stabilit.sc.cln.client.IClient;
import com.stabilit.sc.scmp.SCMPHeaderAttributeKey;
import com.stabilit.sc.scmp.SCMPMsgType;

/**
 * The Class SCMPRegisterServiceCall. Call registers a server.
 * 
 * @author JTraber
 */
public class SCMPRegisterServiceCall extends SCMPCallAdapter {

	/**
	 * Instantiates a new SCMPRegisterServiceCall.
	 */
	public SCMPRegisterServiceCall() {
		this(null);
	}

	/**
	 * Instantiates a new SCMPRegisterServiceCall.
	 * 
	 * @param client
	 *            the client to use when invoking call
	 */
	public SCMPRegisterServiceCall(IClient client) {
		this.client = client;
	}

	/*
	 * (non-Javadoc)
	 * @see com.stabilit.sc.cln.service.SCMPCallAdapter#newInstance(com.stabilit.sc.cln.client.IClient)
	 */
	@Override
	public ISCMPCall newInstance(IClient client) {
		return new SCMPRegisterServiceCall(client);
	}

	/**
	 * Sets the service name.
	 * 
	 * @param serviceName
	 *            the new service name
	 */
	public void setServiceName(String serviceName) {
		requestMessage.setHeader(SCMPHeaderAttributeKey.SERVICE_NAME, serviceName);
	}

	/**
	 * Sets the max sessions.
	 * 
	 * @param maxSessions
	 *            the new max sessions
	 */
	public void setMaxSessions(int maxSessions) {
		requestMessage.setHeader(SCMPHeaderAttributeKey.MAX_SESSIONS, maxSessions);
	}

	/**
	 * Sets the multithreaded.
	 * 
	 * @param multiThreaded
	 *            the new multithreaded
	 */
	public void setMultithreaded(boolean multiThreaded) {
		requestMessage.setHeader(SCMPHeaderAttributeKey.MULTI_THREADED, multiThreaded);
	}

	/**
	 * Sets the port number.
	 * 
	 * @param portNumber
	 *            the new port number
	 */
	public void setPortNumber(int portNumber) {
		requestMessage.setHeader(SCMPHeaderAttributeKey.PORT_NR, portNumber);
	}

	/*
	 * (non-Javadoc)
	 * @see com.stabilit.sc.cln.service.ISCMPCall#getMessageType()
	 */
	@Override
	public SCMPMsgType getMessageType() {
		return SCMPMsgType.REGISTER_SERVICE;
	}
}
