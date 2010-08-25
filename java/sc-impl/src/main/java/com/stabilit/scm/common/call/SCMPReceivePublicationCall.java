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

import org.apache.log4j.Logger;

import com.stabilit.scm.cln.call.ISCMPCall;
import com.stabilit.scm.common.net.req.IRequester;
import com.stabilit.scm.common.scmp.SCMPMsgType;

/**
 * The Class SCMPReceivePublicationCall. Call receives publications from SC.
 * 
 * @author JTraber
 */
public class SCMPReceivePublicationCall extends SCMPSessionCallAdapter {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(SCMPReceivePublicationCall.class);
	
	/**
	 * Instantiates a new sCMP receive publication call.
	 */
	public SCMPReceivePublicationCall() {
		this(null, null, null);
	}

	/**
	 * Instantiates a new sCMP receive publication call.
	 * 
	 * @param requester
	 *            the requester
	 * @param serviceName
	 *            the service name
	 * @param sessionId
	 *            the session id
	 */
	public SCMPReceivePublicationCall(IRequester requester, String serviceName, String sessionId) {
		super(requester, serviceName, sessionId);
	}

	/** {@inheritDoc} */
	@Override
	public SCMPMsgType getMessageType() {
		return SCMPMsgType.RECEIVE_PUBLICATION;
	}

	/** {@inheritDoc} */
	@Override
	public ISCMPCall newInstance(IRequester req, String serviceName, String sessionId) {
		return new SCMPReceivePublicationCall(req, serviceName, sessionId);
	}
}