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
package com.stabilit.scm.common.call;

import com.stabilit.scm.cln.call.ISCMPCall;
import com.stabilit.scm.cln.call.SCMPCallAdapter;
import com.stabilit.scm.common.net.req.IRequester;

/**
 * The Class SCMPCallAdapter. Provides basic functionality for calls which needs a session.
 * 
 * @author JTraber
 */
public abstract class SCMPSessionCallAdapter extends SCMPCallAdapter {

	/**
	 * Instantiates a new SCMPSessionCallAdapter.
	 */
	public SCMPSessionCallAdapter() {
		this(null, null, null);
	}
	
	public SCMPSessionCallAdapter(String s) {
		
	}

	/**
	 * Instantiates a new SCMPSessionCallAdapter.
	 * 
	 * @param client
	 *            the client
	 * @param sessionId
	 *            the session id
	 */
	public SCMPSessionCallAdapter(IRequester req, String serviceName, String sessionId) {
		super(req, serviceName, sessionId);
	}

	/** {@inheritDoc} */
	@Override
	public abstract ISCMPCall newInstance(IRequester req, String serviceName, String sessionId);
}