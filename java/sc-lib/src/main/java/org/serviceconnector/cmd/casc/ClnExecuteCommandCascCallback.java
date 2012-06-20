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
package org.serviceconnector.cmd.casc;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.serviceconnector.cache.SCCache;
import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.net.req.IRequest;
import org.serviceconnector.net.req.netty.IdleTimeoutException;
import org.serviceconnector.net.res.IResponderCallback;
import org.serviceconnector.net.res.IResponse;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.scmp.SCMPMessageFault;

/**
 * The Class ClnExecuteCommandCascCallback.
 */
public class ClnExecuteCommandCascCallback extends CommandCascCallback {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = Logger.getLogger(ClnExecuteCommandCascCallback.class);
	/** The request message. */
	private SCMPMessage requestMessage;

	/**
	 * Instantiates a new ClnExecuteCommandCascCallback.
	 * 
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @param callback
	 *            the callback
	 */
	public ClnExecuteCommandCascCallback(IRequest request, IResponse response, IResponderCallback callback) {
		super(request, response, callback);
		this.requestMessage = this.request.getMessage();
	}

	/** {@inheritDoc} */
	@Override
	public void receive(SCMPMessage reply) {
		String serviceName = this.requestMessage.getServiceName();
		// caching
		SCCache cache = AppContext.getSCCache();
		if (cache.isCacheEnabled() == true) {
			cache.cacheMessage(this.requestMessage, reply);
		}
		// forward server reply to client
		reply.setIsReply(true);
		reply.setMessageType(this.msgType);
		reply.setServiceName(serviceName);
		this.response.setSCMP(reply);
		this.responderCallback.responseCallback(request, response);
	}

	@Override
	public void receive(Exception ex) {
		String sid = this.requestMessage.getSessionId();
		LOGGER.warn("receive exception sid=" + sid + " " + ex.toString());
		SCMPMessage fault = null;
		if (ex instanceof IdleTimeoutException) {
			// operation timeout handling
			fault = new SCMPMessageFault(SCMPError.OPERATION_TIMEOUT, "Operation timeout expired on SC sid=" + sid);
		} else if (ex instanceof IOException) {
			fault = new SCMPMessageFault(SCMPError.CONNECTION_EXCEPTION, "broken connection to server sid=" + sid);
		} else {
			fault = new SCMPMessageFault(SCMPError.SC_ERROR, "error executing " + this.msgType + " sid=" + sid);
		}
		// caching
		SCCache cache = AppContext.getSCCache();
		if (cache.isCacheEnabled() == true) {
			cache.cacheMessage(this.requestMessage, fault);
		}
		String serviceName = this.requestMessage.getServiceName();
		// forward server reply to client
		fault.setSessionId(sid);
		fault.setIsReply(true);
		fault.setMessageType(this.msgType);
		fault.setServiceName(serviceName);
		this.response.setSCMP(fault);
		// check for cache id
		this.responderCallback.responseCallback(request, response);
	}
}
