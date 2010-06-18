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
package com.stabilit.scm.common.service;

import java.io.InputStream;
import java.io.OutputStream;

import com.stabilit.scm.cln.call.SCMPAttachCall;
import com.stabilit.scm.cln.call.SCMPCallFactory;
import com.stabilit.scm.cln.call.SCMPDetachCall;
import com.stabilit.scm.cln.service.IClientServiceConnector;
import com.stabilit.scm.cln.service.ISCSession;
import com.stabilit.scm.cln.service.ISCSubscription;
import com.stabilit.scm.cln.service.SCPublishMessageHandler;
import com.stabilit.scm.common.conf.IConstants;

/**
 * The Class ClientServiceConnector.
 * 
 * @author JTraber
 */
class ClientServiceConnector extends ServiceConnector implements IClientServiceConnector {

	/**
	 * Instantiates a new client service connector.
	 * 
	 * @param host
	 *            the host
	 * @param port
	 *            the port
	 */
	public ClientServiceConnector(String host, int port) {
		super(host, port, IConstants.DEFAULT_CLIENT_CON, IConstants.DEFAULT_NR_OF_THREADS);
	}

	@Override
	public void connect() throws Exception {
		super.connect();
		// sets up the attach call
		SCMPAttachCall attachCall = (SCMPAttachCall) SCMPCallFactory.ATTACH_CALL.newInstance(this.requester);

		attachCall.setKeepAliveTimeout(30);
		attachCall.setKeepAliveInterval(360);
		// attaches client
		attachCall.invoke();
	}

	@Override
	public void disconnect() throws Exception {
		// detach
		SCMPDetachCall detachCall = (SCMPDetachCall) SCMPCallFactory.DETACH_CALL.newInstance(this.requester);
		detachCall.invoke();
		super.disconnect();
	}

	/** {@inheritDoc} */
	@Override
	public ISCSession newDataSession(String serviceName) throws Exception {
		SCDataSession scDataSession = new SCDataSession(serviceName, this.requester);
		return scDataSession;
	}

	/** {@inheritDoc} */
	@Override
	public void downloadFile(String string, String sourceFileName, OutputStream outStream) {
		throw new UnsupportedOperationException();
	}

	/** {@inheritDoc} */
	@Override
	public void uploadFile(String string, String targetFileName, InputStream inStream) {
		throw new UnsupportedOperationException();
	}

	/** {@inheritDoc} */
	@Override
	public ISCSubscription newSubscription(String string, SCPublishMessageHandler messageHandler, String mask) {
		throw new UnsupportedOperationException();
	}
}
