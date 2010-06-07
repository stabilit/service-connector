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
package com.stabilit.scm.cln.call;

import com.stabilit.scm.cln.req.IRequester;
import com.stabilit.scm.scmp.SCMPHeaderAttributeKey;
import com.stabilit.scm.scmp.SCMPMsgType;

/**
 * The Class SCMPDeRegisterServiceCall. Call deregisters server.
 * 
 * @author JTraber
 */
public class SCMPDeRegisterServiceCall extends SCMPCallAdapter {

	/**
	 * Instantiates a new SCMPDeRegisterServiceCall.
	 */
	public SCMPDeRegisterServiceCall() {
		this(null);
	}

	/**
	 * Instantiates a new SCMPDeRegisterServiceCall.
	 * 
	 * @param client
	 *            the client to use when invoking call
	 */
	public SCMPDeRegisterServiceCall(IRequester client) {
		this.req = client;
	}

	/** {@inheritDoc} */
	@Override
	public ISCMPCall newInstance(IRequester client) {
		return new SCMPDeRegisterServiceCall(client);
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

	/** {@inheritDoc} */
	@Override
	public SCMPMsgType getMessageType() {
		return SCMPMsgType.DEREGISTER_SERVICE;
	}
}
