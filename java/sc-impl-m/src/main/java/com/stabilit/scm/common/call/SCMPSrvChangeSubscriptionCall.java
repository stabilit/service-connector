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
package com.stabilit.scm.common.call;

import com.stabilit.scm.cln.call.ISCMPCall;
import com.stabilit.scm.common.net.req.IRequester;
import com.stabilit.scm.common.scmp.SCMPMessage;
import com.stabilit.scm.common.scmp.SCMPMsgType;

/**
 * The Class SCMPSrvChangeSubscriptionCall. Call changes subscription for a client.
 * 
 * @author JTraber
 */
public class SCMPSrvChangeSubscriptionCall extends SCMPServerCallAdapter {

	/**
	 * Instantiates a new SCMPSrvChangeSubscriptionCall.
	 */
	public SCMPSrvChangeSubscriptionCall() {
		this(null, null);
	}

	/**
	 * Instantiates a new SCMPSrvChangeSubscriptionCall.
	 * 
	 * @param req
	 *            the requester
	 * @param receivedMessage
	 *            the received message
	 */
	public SCMPSrvChangeSubscriptionCall(IRequester req, SCMPMessage receivedMessage) {
		super(req, receivedMessage);
	}

	/** {@inheritDoc} */
	@Override
	public ISCMPCall newInstance(IRequester req, SCMPMessage receivedMessage) {
		return new SCMPSrvChangeSubscriptionCall(req, receivedMessage);
	}

	/** {@inheritDoc} */
	@Override
	public SCMPMsgType getMessageType() {
		return SCMPMsgType.SRV_CHANGE_SUBSCRIPTION;
	}
}