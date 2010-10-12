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
import org.serviceconnector.scmp.ISCMPCallback;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;


/**
 * The Class SCMPCallAdapter. Provides basic functionality for direct calls to a backend server.
 * 
 * @author JTraber
 */
public abstract class SCMPServerCallAdapter extends SCMPCallAdapter {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(SCMPServerCallAdapter.class);
	
	/**
	 * Instantiates a new SCMPServerCallAdapter.
	 */
	public SCMPServerCallAdapter() {
		this(null, null);
	}

	/**
	 * @param requester
	 */
	public SCMPServerCallAdapter(IRequester requester) {
		super(requester);
	}

	/**
	 * Instantiates a new SCMPServerCallAdapter. Constructor is necessary because in SC you need to hand over received
	 * message.
	 * 
	 * @param req
	 *            the requester
	 * @param message
	 *            the message
	 */
	public SCMPServerCallAdapter(IRequester req, SCMPMessage message) {
		this.requester = req;
		this.requestMessage = message;
	}

	/** {@inheritDoc} */
	@Override
	public abstract ISCMPCall newInstance(IRequester req, SCMPMessage message);

	/** {@inheritDoc} */
	@Override
	public void invoke(ISCMPCallback callback, int timeoutInMillis) throws Exception {
		super.invoke(callback, timeoutInMillis);
		// no OP_TIMEOUT needed to send to server
		this.requestMessage.removeHeader(SCMPHeaderAttributeKey.OPERATION_TIMEOUT);
	}
}