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
package org.serviceconnector.web;

import org.serviceconnector.Constants;
import org.serviceconnector.api.SCMessage;
import org.serviceconnector.api.SCPublishMessage;
import org.serviceconnector.api.SCServiceException;
import org.serviceconnector.api.SCSubscribeMessage;
import org.serviceconnector.api.srv.ISCPublishServerCallback;
import org.serviceconnector.call.SCMPPublishCall;
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;

/**
 * The Class SCBasePublishServlet. Base servlet for publish service implementations.<br />
 * <br />
 * User needs to extend this servlet to implement a publish service. This base servlet implements ISCPublishServerCallback which
 * gets called if client actions are processed. Publishing messages to an SC can be done by publish methods.
 * More information available in super class.<br />
 * <br />
 * The urlPath passed in constructor is used by the SC to call the servlet(URL). So it must match the mapping in web.xml.
 */
public abstract class SCBasePublishServlet extends SCBaseServlet implements ISCPublishServerCallback {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 8439823120932344164L;

	/**
	 * Instantiates a new sC base publish servlet.
	 * 
	 * @param urlPath
	 *            the URL path
	 */
	protected SCBasePublishServlet(String urlPath) {
		super(urlPath);
	}

	/* (non-Javadoc)
	 * @see org.serviceconnector.api.srv.ISCPublishServerCallback#subscribe(org.serviceconnector.api.SCSubscribeMessage, int)
	 */
	@Override
	public SCMessage subscribe(SCSubscribeMessage message, int operationTimeoutMillis) {
		return message;
	}

	/* (non-Javadoc)
	 * @see org.serviceconnector.api.srv.ISCPublishServerCallback#changeSubscription(org.serviceconnector.api.SCSubscribeMessage, int)
	 */
	@Override
	public SCMessage changeSubscription(SCSubscribeMessage message, int operationTimeoutMillis) {
		return message;
	}

	/* (non-Javadoc)
	 * @see org.serviceconnector.api.srv.ISCPublishServerCallback#unsubscribe(org.serviceconnector.api.SCSubscribeMessage, int)
	 */
	@Override
	public void unsubscribe(SCSubscribeMessage message, int operationTimeoutMillis) {
	}

	/* (non-Javadoc)
	 * @see org.serviceconnector.api.srv.ISCPublishServerCallback#abortSubscription(org.serviceconnector.api.SCSubscribeMessage, int)
	 */
	@Override
	public void abortSubscription(SCSubscribeMessage scMessage, int operationTimeoutMillis) {
	}
	
	/* (non-Javadoc)
	 * @see org.serviceconnector.api.srv.ISCPublishServerCallback#exceptionCaught(org.serviceconnector.api.SCServiceException)
	 */
	@Override
	public void exceptionCaught(SCServiceException ex) {
	}

	/**
	 * Base subscribe.
	 * 
	 * @param reqMessage
	 *            the requester message
	 * @param operationTimeoutMillis
	 *            the operation timeout milliseconds
	 * @return the sCMP message
	 */
	protected final SCMPMessage baseSubscribe(SCMPMessage reqMessage, int operationTimeoutMillis) {
		// create scMessage
		SCSubscribeMessage scMessage = new SCSubscribeMessage();
		scMessage.setData(reqMessage.getBody());
		scMessage.setDataLength(reqMessage.getBodyLength());
		scMessage.setCompressed(reqMessage.getHeaderFlag(SCMPHeaderAttributeKey.COMPRESSION));
		scMessage.setMessageInfo(reqMessage.getHeader(SCMPHeaderAttributeKey.MSG_INFO));
		scMessage.setSessionInfo(reqMessage.getHeader(SCMPHeaderAttributeKey.SESSION_INFO));
		scMessage.setSessionId(reqMessage.getSessionId());
		scMessage.setMask(reqMessage.getHeader(SCMPHeaderAttributeKey.MASK));
		scMessage.setServiceName(reqMessage.getServiceName());

		// call servlet service implementation
		SCMessage scReply = ((ISCPublishServerCallback) this).subscribe(scMessage, operationTimeoutMillis);

		// set up reply - SCMP Version request
		SCMPMessage reply = new SCMPMessage(reqMessage.getSCMPVersion());
		long msgSequenceNr = this.requester.getSCMPMsgSequenceNr().incrementAndGetMsgSequenceNr();
		reply.setHeader(SCMPHeaderAttributeKey.MESSAGE_SEQUENCE_NR, msgSequenceNr);
		reply.setServiceName(serviceName);
		reply.setMessageType(reqMessage.getMessageType());
		reply.setSessionId(reqMessage.getSessionId());

		if (scReply != null) {
			reply.setBody(scReply.getData());
			if (scReply.isCompressed()) {
				reply.setHeaderFlag(SCMPHeaderAttributeKey.COMPRESSION);
			}
			if (scReply.getAppErrorCode() != Constants.EMPTY_APP_ERROR_CODE) {
				reply.setHeader(SCMPHeaderAttributeKey.APP_ERROR_CODE, scReply.getAppErrorCode());
			}
			if (scReply.getAppErrorText() != null) {
				reply.setHeader(SCMPHeaderAttributeKey.APP_ERROR_TEXT, scReply.getAppErrorText());
			}
			if (scReply.isReject()) {
				// subscription rejected
				reply.setHeaderFlag(SCMPHeaderAttributeKey.REJECT_SESSION);
			} else {
				// add session to composite registry
				SCBaseServlet.compositeRegistry.addSession(reqMessage.getSessionId());
			}
			if (scReply.getSessionInfo() != null) {
				reply.setHeader(SCMPHeaderAttributeKey.SESSION_INFO, scReply.getSessionInfo());
			}
		}
		return reply;
	}

	/**
	 * Base unsubscribe.
	 * 
	 * @param reqMessage
	 *            the requester message
	 * @param operationTimeoutMillis
	 *            the operation timeout milliseconds
	 * @return the sCMP message
	 */
	protected final SCMPMessage baseUnsubscribe(SCMPMessage reqMessage, int operationTimeoutMillis) {
		// remove session from composite registry
		SCBaseServlet.compositeRegistry.removeSession(reqMessage.getSessionId());
		// create scMessage
		SCSubscribeMessage scMessage = new SCSubscribeMessage();
		scMessage.setData(reqMessage.getBody());
		scMessage.setDataLength(reqMessage.getBodyLength());
		scMessage.setSessionId(reqMessage.getSessionId());
		scMessage.setServiceName(reqMessage.getServiceName());
		scMessage.setSessionInfo(reqMessage.getHeader(SCMPHeaderAttributeKey.SESSION_INFO));
		// call servlet service implementation
		((ISCPublishServerCallback) this).unsubscribe(scMessage, operationTimeoutMillis);

		// set up reply - SCMP Version request
		SCMPMessage reply = new SCMPMessage(reqMessage.getSCMPVersion());
		long msgSequenceNr = this.requester.getSCMPMsgSequenceNr().incrementAndGetMsgSequenceNr();
		reply.setHeader(SCMPHeaderAttributeKey.MESSAGE_SEQUENCE_NR, msgSequenceNr);
		reply.setServiceName(serviceName);
		reply.setMessageType(reqMessage.getMessageType());
		reply.setSessionId(reqMessage.getSessionId());
		return reply;
	}

	/**
	 * Base abort subscription.
	 * 
	 * @param reqMessage
	 *            the requester message
	 * @param operationTimeoutMillis
	 *            the operation timeout milliseconds
	 * @return the sCMP message
	 */
	protected final SCMPMessage baseAbortSubscription(SCMPMessage reqMessage, int operationTimeoutMillis) {
		// create scMessage
		SCSubscribeMessage scMessage = new SCSubscribeMessage();
		scMessage.setData(reqMessage.getBody());
		scMessage.setDataLength(reqMessage.getBodyLength());
		scMessage.setCompressed(reqMessage.getHeaderFlag(SCMPHeaderAttributeKey.COMPRESSION));
		scMessage.setMessageInfo(reqMessage.getHeader(SCMPHeaderAttributeKey.MSG_INFO));
		scMessage.setSessionId(reqMessage.getSessionId());
		scMessage.setServiceName(reqMessage.getServiceName());
		scMessage.setMask(reqMessage.getHeader(SCMPHeaderAttributeKey.MASK));

		// call servlet service implementation
		((ISCPublishServerCallback) this).abortSubscription(scMessage, operationTimeoutMillis);

		// set up reply - SCMP Version request
		SCMPMessage reply = new SCMPMessage(reqMessage.getSCMPVersion());
		reply.setServiceName(serviceName);
		reply.setSessionId(reqMessage.getSessionId());
		reply.setMessageType(reqMessage.getMessageType());
		return reply;
	}

	/**
	 * Base change subscription.
	 * 
	 * @param reqMessage
	 *            the requester message
	 * @param operationTimeoutMillis
	 *            the operation timeout milliseconds
	 * @return the sCMP message
	 */
	protected final SCMPMessage baseChangeSubscription(SCMPMessage reqMessage, int operationTimeoutMillis) {
		// create scMessage
		SCSubscribeMessage scMessage = new SCSubscribeMessage();
		scMessage.setData(reqMessage.getBody());
		scMessage.setDataLength(reqMessage.getBodyLength());
		scMessage.setCompressed(reqMessage.getHeaderFlag(SCMPHeaderAttributeKey.COMPRESSION));
		scMessage.setMessageInfo(reqMessage.getHeader(SCMPHeaderAttributeKey.MSG_INFO));
		scMessage.setSessionInfo(reqMessage.getHeader(SCMPHeaderAttributeKey.SESSION_INFO));
		scMessage.setSessionId(reqMessage.getSessionId());
		scMessage.setMask(reqMessage.getHeader(SCMPHeaderAttributeKey.MASK));
		scMessage.setServiceName(reqMessage.getServiceName());
		scMessage.setActualMask(reqMessage.getHeader(SCMPHeaderAttributeKey.ACTUAL_MASK));

		// call servlet service implementation
		SCMessage scReply = ((ISCPublishServerCallback) this).changeSubscription(scMessage, operationTimeoutMillis);

		// set up reply - SCMP Version request
		SCMPMessage reply = new SCMPMessage(reqMessage.getSCMPVersion());
		long msgSequenceNr = this.requester.getSCMPMsgSequenceNr().incrementAndGetMsgSequenceNr();
		reply.setHeader(SCMPHeaderAttributeKey.MESSAGE_SEQUENCE_NR, msgSequenceNr);
		reply.setServiceName(serviceName);
		reply.setMessageType(reqMessage.getMessageType());

		if (scReply != null) {
			reply.setBody(scReply.getData());
			if (scReply.isCompressed()) {
				reply.setHeaderFlag(SCMPHeaderAttributeKey.COMPRESSION);
			}
			if (scReply.getAppErrorCode() != Constants.EMPTY_APP_ERROR_CODE) {
				reply.setHeader(SCMPHeaderAttributeKey.APP_ERROR_CODE, scReply.getAppErrorCode());
			}
			if (scReply.getAppErrorText() != null) {
				reply.setHeader(SCMPHeaderAttributeKey.APP_ERROR_TEXT, scReply.getAppErrorText());
			}
			if (scReply.isReject()) {
				reply.setHeaderFlag(SCMPHeaderAttributeKey.REJECT_SESSION);
			}
			if (scReply.getSessionInfo() != null) {
				reply.setHeader(SCMPHeaderAttributeKey.SESSION_INFO, scReply.getSessionInfo());
			}
		}
		return reply;
	}

	/**
	 * Publish message to SC.
	 * 
	 * @param publishMessage
	 *            the publish message
	 * @throws SCServiceException
	 *             server not registered yet<br />
	 *             publish to SC failed<br />
	 *             error message received from SC <br />
	 * @throws SCMPValidatorException
	 *             publish message is not set<br />
	 */
	public final void publish(SCPublishMessage publishMessage) throws SCServiceException, SCMPValidatorException {
		this.publish(Constants.DEFAULT_OPERATION_TIMEOUT_SECONDS, publishMessage);
	}

	/**
	 * Publish message to SC.
	 * 
	 * @param operationTimeoutSeconds
	 *            the allowed time in seconds to complete the operation
	 * @param publishMessage
	 *            the publish message
	 * @throws SCServiceException
	 *             server not registered yet<br />
	 *             publish to SC failed<br />
	 *             error message received from SC <br />
	 * @throws SCMPValidatorException
	 *             publish message is not set<br />
	 */
	public final void publish(int operationTimeoutSeconds, SCPublishMessage publishMessage) throws SCServiceException,
			SCMPValidatorException {
		if (this.registered == false) {
			throw new SCServiceException("Server is not registered for a service.");
		}
		if (publishMessage == null) {
			throw new SCMPValidatorException("Publish message is missing.");
		}
		synchronized (this) {
			this.requester.getSCMPMsgSequenceNr().incrementAndGetMsgSequenceNr();
			// get lock on scServer - only one server is allowed to communicate over the initial connection
			SCMPPublishCall publishCall = new SCMPPublishCall(this.requester, serviceName);
			publishCall.setRequestBody(publishMessage.getData());
			publishCall.setMask(publishMessage.getMask());
			publishCall.setPartSize(publishMessage.getPartSize());
			publishCall.setMessageInfo(publishMessage.getMessageInfo());
			SCServerCallback callback = new SCServerCallback(true);
			try {
				publishCall.invoke(callback, operationTimeoutSeconds * Constants.SEC_TO_MILLISEC_FACTOR);
			} catch (Exception e) {
				throw new SCServiceException("Publish failed. ", e);
			}
			SCMPMessage message = callback.getMessageSync(operationTimeoutSeconds * Constants.SEC_TO_MILLISEC_FACTOR);
			if (message.isFault()) {
				SCServiceException ex = new SCServiceException("Publish failed.");
				ex.setSCErrorCode(message.getHeaderInt(SCMPHeaderAttributeKey.SC_ERROR_CODE));
				ex.setSCErrorText(message.getHeader(SCMPHeaderAttributeKey.SC_ERROR_TEXT));
				throw ex;
			}
		}
	}
}
