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
package org.serviceconnector.srv.ps;

import org.apache.log4j.Logger;
import org.serviceconnector.call.SCMPCallFactory;
import org.serviceconnector.call.SCMPPublishCall;
import org.serviceconnector.conf.Constants;
import org.serviceconnector.srv.ISCPublishServer;
import org.serviceconnector.srv.ISCPublishServerCallback;
import org.serviceconnector.srv.SCServer;
import org.serviceconnector.srv.SrvService;


/**
 * The Class SCPublishServer. A Server that publishes messages to an SC.
 */
public class SCPublishServer extends SCServer implements ISCPublishServer {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(SCPublishServer.class);

	/** {@inheritDoc} */
	@Override
	public void publish(String serviceName, String mask, Object data) throws Exception {
		SrvService srvService = this.srvServiceRegistry.getSrvService(serviceName);
		SCMPPublishCall publishCall = (SCMPPublishCall) SCMPCallFactory.PUBLISH_CALL.newInstance(srvService
				.getRequester(), serviceName);
		publishCall.setRequestBody(data);
		publishCall.setMask(mask);
		publishCall
				.invoke(this.callback, Constants.DEFAULT_OPERATION_TIMEOUT_SECONDS * Constants.SEC_TO_MILISEC_FACTOR);
		this.callback.getMessageSync();
	}

	/** {@inheritDoc} */
	@Override
	public void registerService(String scHost, int scPort, String serviceName, int maxSessions, int maxConnections,
			ISCPublishServerCallback scCallback) throws Exception {
		super.registerService(scHost, scPort, serviceName, maxSessions, maxConnections, scCallback);
	}
}
