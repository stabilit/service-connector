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
import com.stabilit.sc.cln.client.IClientSession;

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
		this(null, null);
	}

	/**
	 * Instantiates a new SCMPSessionCallAdapter.
	 * 
	 * @param client
	 *            the client
	 */
	public SCMPSessionCallAdapter(IClient client) {
		super(client);
	}

	/**
	 * Instantiates a new SCMPSessionCallAdapter.
	 * 
	 * @param client
	 *            the client
	 * @param clientSession
	 *            the client session
	 */
	public SCMPSessionCallAdapter(IClient client, IClientSession clientSession) {
		super(client, clientSession);
	}

	/** {@inheritDoc} */
	@Override
	public abstract ISCMPCall newInstance(IClient client, IClientSession clientSession);
}