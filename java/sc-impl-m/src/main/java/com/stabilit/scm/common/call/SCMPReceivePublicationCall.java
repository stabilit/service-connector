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
import com.stabilit.scm.common.scmp.SCMPHeaderAttributeKey;
import com.stabilit.scm.common.scmp.SCMPMsgType;

/**
 * @author JTraber
 */
public class SCMPReceivePublicationCall extends SCMPSessionCallAdapter {

	public SCMPReceivePublicationCall() {
		this(null, null, null);
	}

	public SCMPReceivePublicationCall(IRequester requester, String serviceName, String sessionId) {
		super(requester, serviceName, sessionId);
	}

	/** {@inheritDoc} */
	@Override
	public SCMPMsgType getMessageType() {
		return SCMPMsgType.RECEIVE_PUBLICATION;
	}

	@Override
	public ISCMPCall newInstance(IRequester req, String serviceName, String sessionId) {
		return new SCMPReceivePublicationCall(req, serviceName, sessionId);
	}

	public void setNoDataInterval(int noDataInterval) {
		this.requestMessage.setHeader(SCMPHeaderAttributeKey.NO_DATA_INTERVAL, noDataInterval);
	}

	public void setMask(String mask) {
		this.requestMessage.setHeader(SCMPHeaderAttributeKey.MASK, mask);
	}
}