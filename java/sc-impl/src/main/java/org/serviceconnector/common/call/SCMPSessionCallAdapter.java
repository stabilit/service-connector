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
package org.serviceconnector.common.call;

import org.apache.log4j.Logger;
import org.serviceconnector.common.net.req.IRequester;
import org.serviceconnector.sc.cln.call.ISCMPCall;
import org.serviceconnector.sc.cln.call.SCMPCallAdapter;


/**
 * The Class SCMPCallAdapter. Provides basic functionality for calls which needs a session.
 * 
 * @author JTraber
 */
public abstract class SCMPSessionCallAdapter extends SCMPCallAdapter {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(SCMPSessionCallAdapter.class);
	
	/**
	 * Instantiates a new SCMPSessionCallAdapter.
	 */
	public SCMPSessionCallAdapter() {
		this(null, null, null);
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