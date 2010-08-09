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
package com.stabilit.scm.srv.ps;

import com.stabilit.scm.common.call.SCMPCallFactory;
import com.stabilit.scm.common.call.SCMPPublishCall;
import com.stabilit.scm.srv.ISCPublishServer;
import com.stabilit.scm.srv.ISCPublishServerCallback;
import com.stabilit.scm.srv.ISCServerCallback;
import com.stabilit.scm.srv.SCServer;

/**
 * The Class SCPublishServer. A Server that publishes messages to an SC.
 */
public class SCPublishServer extends SCServer implements ISCPublishServer {

	/**
	 * Instantiates a new SCPublishServer.
	 * 
	 * @param host
	 *            the host
	 * @param port
	 *            the port
	 */
	public SCPublishServer(String host, int port) {
		super(host, port);
	}

	/** {@inheritDoc} */
	@Override
	public void publish(String serviceName, String mask, Object data) throws Exception {
		SCMPPublishCall publishCall = (SCMPPublishCall) SCMPCallFactory.PUBLISH_CALL.newInstance(this.requester,
				serviceName);
		publishCall.setRequestBody(data);
		publishCall.setMask(mask);
		publishCall.invoke(this.callback);
		this.callback.getMessageSync();
	}

	/** {@inheritDoc} */
	@Override
	public void registerService(String serviceName, ISCServerCallback scCallback) throws Exception {
		throw new UnsupportedOperationException();
	}

	/** {@inheritDoc} */
	@Override
	public void registerService(String serviceName, ISCPublishServerCallback scCallback) throws Exception {
		super.registerService(serviceName, scCallback);
	}
}
