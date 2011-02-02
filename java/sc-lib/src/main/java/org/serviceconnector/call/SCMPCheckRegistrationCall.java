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
package org.serviceconnector.call;

import org.apache.log4j.Logger;
import org.serviceconnector.net.req.IRequester;
import org.serviceconnector.scmp.SCMPMsgType;

/**
 * The Class SCMPCheckRegistrationCall. Checks server registration.
 * 
 * @author JTraber
 */
public class SCMPCheckRegistrationCall extends SCMPCallAdapter  {

	/** The Constant logger. */
	@SuppressWarnings("unused")
	private final static Logger logger = Logger.getLogger(SCMPCheckRegistrationCall.class);
		
	/**
	 * Instantiates a new SCMPCheckRegistrationCall.
	 * 
	 * @param requester
	 *            the requesters to use when invoking call
	 */
	public SCMPCheckRegistrationCall(IRequester requester, String serviceName) {
		super(requester, serviceName);
	}

	/** {@inheritDoc} */
	@Override
	public SCMPMsgType getMessageType() {
		return SCMPMsgType.CHECK_REGISTRATION;
	}
}
