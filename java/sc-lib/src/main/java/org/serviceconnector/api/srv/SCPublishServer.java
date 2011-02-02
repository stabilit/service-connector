/*
 * -----------------------------------------------------------------------------*
 * *
 * Copyright © 2010 STABILIT Informatik AG, Switzerland *
 * *
 * Licensed under the Apache License, Version 2.0 (the "License"); *
 * you may not use this file except in compliance with the License. *
 * You may obtain a copy of the License at *
 * *
 * http://www.apache.org/licenses/LICENSE-2.0 *
 * *
 * Unless required by applicable law or agreed to in writing, software *
 * distributed under the License is distributed on an "AS IS" BASIS, *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. *
 * See the License for the specific language governing permissions and *
 * limitations under the License. *
 * -----------------------------------------------------------------------------*
 * /*
 * /**
 */
package org.serviceconnector.api.srv;

import org.apache.log4j.Logger;
import org.serviceconnector.Constants;
import org.serviceconnector.api.SCPublishMessage;
import org.serviceconnector.call.SCMPPublishCall;
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.net.req.SCRequester;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.service.SCServiceException;

/**
 * The Class SCPublishServer. A Server that publishes messages to a SC.
 */
public class SCPublishServer extends SCSessionServer {

	/** The Constant logger. */
	@SuppressWarnings("unused")
	private final static Logger logger = Logger.getLogger(SCPublishServer.class);

	/**
	 * Instantiates a new SC publish server.
	 * 
	 * @param scServer
	 *            the SC server
	 * @param serviceName
	 *            the service name
	 * @param requester
	 *            the requester
	 */
	public SCPublishServer(SCServer scServer, String serviceName, SCRequester requester) {
		super(scServer, serviceName, requester);
	}

	/**
	 * Register server with default operation timeout.
	 * 
	 * @param maxSessions
	 *            the max sessions to serve
	 * @param maxConnections
	 *            the max connections pool uses to connect to SC
	 * @param scCallback
	 *            the SC callback
	 * @throws Exception
	 *             the exception
	 */
	public synchronized void register(int maxSessions, int maxConnections, SCPublishServerCallback scCallback) throws Exception {
		this.register(Constants.DEFAULT_OPERATION_TIMEOUT_SECONDS, maxSessions, maxConnections, scCallback);
	}

	/**
	 * Register server.
	 * 
	 * @param operationTimeoutSeconds
	 *            allowed time to complete operation
	 * @param maxSessions
	 *            the max sessions to serve
	 * @param maxConnections
	 *            the max connections pool uses to connect to SC
	 * @param scCallback
	 *            the SC callback
	 * @throws Exception
	 *             the exception
	 */
	public synchronized void register(int operationTimeoutSeconds, int maxSessions, int maxConnections,
			SCPublishServerCallback scCallback) throws Exception {
		if (scCallback == null) {
			throw new SCMPValidatorException(SCMPError.HV_ERROR, "callback is missing");
		}
		this.doRegister(operationTimeoutSeconds, maxSessions, maxConnections);
		// creating srvService & adding to registry
		SrvServiceRegistry srvServiceRegistry = AppContext.getSrvServiceRegistry();
		SrvService srvService = new SrvPublishService(this.serviceName, maxSessions, maxConnections, this.requester, scCallback);
		srvServiceRegistry.addSrvService(this.serviceName + "_" + this.scServer.getListenerPort(), srvService);
		this.registered = true;
	}

	/**
	 * Publish with default operation timeout.
	 * 
	 * @param publishMessage
	 *            the publish message
	 * @throws Exception
	 *             the exception
	 */
	public void publish(SCPublishMessage publishMessage) throws Exception {
		this.publish(Constants.DEFAULT_OPERATION_TIMEOUT_SECONDS, publishMessage);
	}

	/**
	 * Publish.
	 * 
	 * @param operationTimeoutSeconds
	 *            allowed time to complete operation
	 * @param publishMessage
	 *            the publish message
	 * @throws Exception
	 *             the exception
	 */
	public void publish(int operationTimeoutSeconds, SCPublishMessage publishMessage) throws Exception {
		if (publishMessage == null) {
			throw new SCMPValidatorException(SCMPError.HV_ERROR, "publish message is missing");
		}
		synchronized (this.scServer) {
			// get lock on scServer - only one server is allowed to communicate over the initial connection
			SCMPPublishCall publishCall = new SCMPPublishCall(this.requester, serviceName);
			publishCall.setRequestBody(publishMessage.getData());
			publishCall.setMask(publishMessage.getMask());
			publishCall.setMessageInfo(publishMessage.getMessageInfo());
			SCServerCallback callback = new SCServerCallback(true);
			try {
				publishCall.invoke(callback, operationTimeoutSeconds * Constants.SEC_TO_MILLISEC_FACTOR);
			} catch (Exception e) {
				throw new SCServiceException("publish failed ", e);
			}
			SCMPMessage message = callback.getMessageSync(operationTimeoutSeconds * Constants.SEC_TO_MILLISEC_FACTOR);
			if (message.isFault()) {
				SCServiceException ex = new SCServiceException("publish failed");
				ex.setSCErrorCode(message.getHeader(SCMPHeaderAttributeKey.SC_ERROR_CODE));
				ex.setSCErrorText(message.getHeader(SCMPHeaderAttributeKey.SC_ERROR_TEXT));
				throw ex;
			}
		}
	}
}
