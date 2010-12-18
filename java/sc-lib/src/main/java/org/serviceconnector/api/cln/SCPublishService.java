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
package org.serviceconnector.api.cln;

import java.security.InvalidParameterException;

import org.apache.log4j.Logger;
import org.serviceconnector.Constants;
import org.serviceconnector.api.SCMessageCallback;
import org.serviceconnector.api.SCPublishMessage;
import org.serviceconnector.api.SCService;
import org.serviceconnector.api.SCSubscribeMessage;
import org.serviceconnector.call.SCMPCallFactory;
import org.serviceconnector.call.SCMPClnChangeSubscriptionCall;
import org.serviceconnector.call.SCMPClnSubscribeCall;
import org.serviceconnector.call.SCMPClnUnsubscribeCall;
import org.serviceconnector.call.SCMPReceivePublicationCall;
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.net.req.SCRequester;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.scmp.SCMPMessageFault;
import org.serviceconnector.service.SCServiceException;
import org.serviceconnector.util.ValidatorUtility;

/**
 * The Class PublishService. PublishService is a remote interface in client API to a publish service and provides communication
 * functions.
 */
public class SCPublishService extends SCService {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(SCPublishService.class);
	private int noDataInterval;
	private SCMessageCallback scMessageCallback;

	public SCPublishService(SCClient scClient, String serviceName, SCRequester requester) {
		super(scClient, serviceName, requester);
		this.noDataInterval = 0;
		this.scMessageCallback = null;
	}

	/**
	 * Change subscription.
	 * 
	 * @param scSubscribeMessage
	 *            the sc subscribe message
	 * @return the sC subscribe message
	 * @throws Exception
	 *             the exception
	 */
	public synchronized SCSubscribeMessage changeSubscription(SCSubscribeMessage scSubscribeMessage) throws Exception {
		return this.changeSubscription(Constants.DEFAULT_OPERATION_TIMEOUT_SECONDS, scSubscribeMessage);
	}

	/**
	 * Change subscription.
	 * 
	 * @param operationTimeoutSeconds
	 *            the timeout in seconds
	 * @param scSubscribeMessage
	 *            the sc subscribe message
	 * @return the sC subscribe message
	 * @throws Exception
	 *             the exception
	 */
	public synchronized SCSubscribeMessage changeSubscription(int operationTimeoutSeconds, SCSubscribeMessage scSubscribeMessage)
			throws Exception {
		if (this.sessionActive == false) {
			throw new SCServiceException("changeSubscription not possible - not subscribed");
		}
		if (scSubscribeMessage == null) {
			throw new SCMPValidatorException(SCMPError.HV_ERROR, "scSubscribeMessage can not be null");
		}
		ValidatorUtility.validateInt(1, operationTimeoutSeconds, 3600, SCMPError.HV_WRONG_OPERATION_TIMEOUT);
		this.requester.getContext().getSCMPMsgSequenceNr().incrementMsgSequenceNr();
		SCMPClnChangeSubscriptionCall changeSubscriptionCall = (SCMPClnChangeSubscriptionCall) SCMPCallFactory.CLN_CHANGE_SUBSCRIPTION
				.newInstance(this.requester, this.serviceName, this.sessionId);
		changeSubscriptionCall.setMask(scSubscribeMessage.getMask());
		changeSubscriptionCall.setCompressed(scSubscribeMessage.isCompressed());
		changeSubscriptionCall.setRequestBody(scSubscribeMessage.getData());
		changeSubscriptionCall.setSessionInfo(scSubscribeMessage.getSessionInfo());
		SCServiceCallback callback = new SCServiceCallback(true);

		try {
			changeSubscriptionCall.invoke(callback, operationTimeoutSeconds * Constants.SEC_TO_MILLISEC_FACTOR);
		} catch (Exception e) {
			this.sessionActive = false;
			throw new SCServiceException("change subscription failed", e);
		}
		SCMPMessage reply = callback.getMessageSync(operationTimeoutSeconds * Constants.SEC_TO_MILLISEC_FACTOR);
		if (reply.isFault()) {
			this.sessionActive = false;
			SCServiceException ex = new SCServiceException("change subscription failed");
			ex.setSCMPError(reply.getHeader(SCMPHeaderAttributeKey.SC_ERROR_CODE));
			throw ex;
		}
		if (reply.getHeaderFlag(SCMPHeaderAttributeKey.REJECT_SESSION)) {
			SCServiceException ex = new SCServiceException("change subscription failed, subscription rejected");
			if (reply.getHeaderInt(SCMPHeaderAttributeKey.APP_ERROR_CODE) != null) {
				ex.setAppErrorCode(reply.getHeaderInt(SCMPHeaderAttributeKey.APP_ERROR_CODE));
				ex.setAppErrorText(reply.getHeader(SCMPHeaderAttributeKey.APP_ERROR_TEXT));
			}
			throw ex;
		}
		SCSubscribeMessage replyToClient = new SCSubscribeMessage();
		replyToClient.setData(reply.getBody());
		replyToClient.setCompressed(reply.getHeaderFlag(SCMPHeaderAttributeKey.COMPRESSION));
		replyToClient.setSessionId(this.sessionId);
		if (reply.getHeaderInt(SCMPHeaderAttributeKey.APP_ERROR_CODE) != null) {
			replyToClient.setAppErrorCode(reply.getHeaderInt(SCMPHeaderAttributeKey.APP_ERROR_CODE));
			replyToClient.setAppErrorText(reply.getHeader(SCMPHeaderAttributeKey.APP_ERROR_TEXT));
		}
		return replyToClient;
	}

	/**
	 * Subscribe.
	 * 
	 * @param scSubscribeMessage
	 *            the SC subscribe message
	 * @param callback
	 *            the callback
	 * @return the SC subscribe message
	 * @throws Exception
	 *             the exception
	 */
	public synchronized SCSubscribeMessage subscribe(SCSubscribeMessage scSubscribeMessage, SCMessageCallback callback)
			throws Exception {
		return this.subscribe(Constants.DEFAULT_OPERATION_TIMEOUT_SECONDS, scSubscribeMessage, callback);
	}

	/**
	 * Subscribe.
	 * 
	 * @param operationTimeoutSeconds
	 *            the timeout in seconds
	 * @param scSubscribeMessage
	 *            the sc subscribe message
	 * @param scMessageCallback
	 *            the sc message callback
	 * @return the sC subscribe message
	 * @throws Exception
	 *             the exception
	 */
	public SCSubscribeMessage subscribe(int operationTimeoutSeconds, SCSubscribeMessage scSubscribeMessage,
			SCMessageCallback scMessageCallback) throws Exception {
		if (this.sessionActive) {
			throw new SCServiceException("already subscribed");
		}
		if (scSubscribeMessage == null) {
			throw new SCMPValidatorException(SCMPError.HV_ERROR, "scSubscribeMessage can not be null");
		}
		if (scMessageCallback == null) {
			throw new InvalidParameterException("Callback must be set.");
		}
		ValidatorUtility.validateInt(1, operationTimeoutSeconds, 3600, SCMPError.HV_WRONG_OPERATION_TIMEOUT);
		this.noDataInterval = scSubscribeMessage.getNoDataIntervalInSeconds();
		if (this.noDataInterval == 0) {
			throw new InvalidParameterException("notDataInterval must be set.");
		}
		this.requester.getContext().getSCMPMsgSequenceNr().reset();
		this.scMessageCallback = scMessageCallback;
		SCServiceCallback callback = new SCServiceCallback(true);
		SCMPClnSubscribeCall subscribeCall = (SCMPClnSubscribeCall) SCMPCallFactory.CLN_SUBSCRIBE_CALL.newInstance(this.requester,
				this.serviceName);
		subscribeCall.setMask(scSubscribeMessage.getMask());
		subscribeCall.setSessionInfo(scSubscribeMessage.getSessionInfo());
		subscribeCall.setNoDataIntervalSeconds(scSubscribeMessage.getNoDataIntervalInSeconds());
		subscribeCall.setCompressed(scSubscribeMessage.isCompressed());
		subscribeCall.setRequestBody(scSubscribeMessage.getData());
		try {
			subscribeCall.invoke(callback, operationTimeoutSeconds * Constants.SEC_TO_MILLISEC_FACTOR);
		} catch (Exception e) {
			this.sessionActive = false;
			throw new SCServiceException("subscribe failed", e);
		}
		SCMPMessage reply = callback.getMessageSync(operationTimeoutSeconds * Constants.SEC_TO_MILLISEC_FACTOR);
		if (reply.isFault()) {
			this.sessionActive = false;
			SCServiceException ex = new SCServiceException("subscribe failed");
			ex.setSCMPError(reply.getHeader(SCMPHeaderAttributeKey.SC_ERROR_CODE));
			throw ex;
		}
		if (reply.getHeaderFlag(SCMPHeaderAttributeKey.REJECT_SESSION)) {
			SCServiceException ex = new SCServiceException("change subscription failed, subscription rejected");
			if (reply.getHeaderInt(SCMPHeaderAttributeKey.APP_ERROR_CODE) != null) {
				ex.setAppErrorCode(reply.getHeaderInt(SCMPHeaderAttributeKey.APP_ERROR_CODE));
				ex.setAppErrorText(reply.getHeader(SCMPHeaderAttributeKey.APP_ERROR_TEXT));
			}
			throw ex;
		}
		this.sessionId = reply.getSessionId();
		this.sessionActive = true;
		this.receivePublication();
		SCSubscribeMessage replyToClient = new SCSubscribeMessage();
		replyToClient.setData(reply.getBody());
		replyToClient.setCompressed(reply.getHeaderFlag(SCMPHeaderAttributeKey.COMPRESSION));
		replyToClient.setSessionId(this.sessionId);
		if (reply.getHeaderInt(SCMPHeaderAttributeKey.APP_ERROR_CODE) != null) {
			replyToClient.setAppErrorCode(reply.getHeaderInt(SCMPHeaderAttributeKey.APP_ERROR_CODE));
			replyToClient.setAppErrorText(reply.getHeader(SCMPHeaderAttributeKey.APP_ERROR_TEXT));
		}
		return replyToClient;
	}

	/**
	 * Checks if is subscribed.
	 * 
	 * @return true, if is subscribed
	 */
	public boolean isSubscribed() {
		return this.sessionActive;
	}

	/**
	 * Sends a receive publication to the SC.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	private synchronized void receivePublication() throws Exception {
		if (this.sessionActive == false || this.sessionId == null) {
			return;
		}
		SCMPReceivePublicationCall receivePublicationCall = (SCMPReceivePublicationCall) SCMPCallFactory.RECEIVE_PUBLICATION
				.newInstance(this.requester, this.serviceName, this.sessionId);
		this.requester.getContext().getSCMPMsgSequenceNr().incrementMsgSequenceNr();
		PublishServiceCallback callback = new PublishServiceCallback(this.scMessageCallback);
		receivePublicationCall.invoke(callback, Constants.SEC_TO_MILLISEC_FACTOR
				* (Constants.DEFAULT_OPERATION_TIMEOUT_SECONDS + this.noDataInterval));
	}

	/**
	 * Unsubscribe.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	public synchronized void unsubscribe() throws Exception {
		this.unsubscribe(Constants.DEFAULT_OPERATION_TIMEOUT_SECONDS, null);
	}

	/**
	 * Unsubscribe.
	 * 
	 * @param scSubscribeMessage
	 *            the sc subscribe message
	 * @throws Exception
	 *             the exception
	 */
	public synchronized void unsubscribe(SCSubscribeMessage scSubscribeMessage) throws Exception {
		this.unsubscribe(Constants.DEFAULT_OPERATION_TIMEOUT_SECONDS, scSubscribeMessage);
	}

	/**
	 * Unsubscribe.
	 * 
	 * @param operationTimeoutSeconds
	 *            the operation timeout seconds
	 * @throws Exception
	 *             the exception
	 */
	public synchronized void unsubscribe(int operationTimeoutSeconds) throws Exception {
		this.unsubscribe(operationTimeoutSeconds, null);
	}

	/**
	 * Unsubscribe.
	 * 
	 * @param operationTimeoutSeconds
	 *            the timeout in seconds
	 * @throws Exception
	 *             the exception
	 */
	public synchronized void unsubscribe(int operationTimeoutSeconds, SCSubscribeMessage scSubscribeMessage) throws Exception {
		if (this.sessionActive == false) {
			// unsubscribe not possible - not subscribed on this service just ignore
			return;
		}
		this.sessionActive = false;
		ValidatorUtility.validateInt(1, operationTimeoutSeconds, 3600, SCMPError.HV_WRONG_OPERATION_TIMEOUT);
		try {
			this.requester.getContext().getSCMPMsgSequenceNr().incrementMsgSequenceNr();
			SCMPClnUnsubscribeCall unsubscribeCall = (SCMPClnUnsubscribeCall) SCMPCallFactory.CLN_UNSUBSCRIBE_CALL.newInstance(
					this.requester, this.serviceName, this.sessionId);
			SCServiceCallback callback = new SCServiceCallback(true);
			if (scSubscribeMessage != null) {
				unsubscribeCall.setSessionInfo(scSubscribeMessage.getSessionInfo());
			}
			try {
				unsubscribeCall.invoke(callback, operationTimeoutSeconds * Constants.SEC_TO_MILLISEC_FACTOR);
			} catch (Exception e) {
				throw new SCServiceException("unsubscribe failed", e);
			}
			SCMPMessage reply = callback.getMessageSync(operationTimeoutSeconds * Constants.SEC_TO_MILLISEC_FACTOR);
			if (reply.isFault()) {
				SCServiceException ex = new SCServiceException("unsubscribe failed");
				ex.setSCMPError(reply.getHeader(SCMPHeaderAttributeKey.SC_ERROR_CODE));
				throw ex;
			}
		} finally {
			this.sessionId = null;
		}
	}

	/**
	 * The Class PublishServiceCallback. Responsible for handling the right communication sequence for publish subscribe protocol.
	 */
	private class PublishServiceCallback extends SCServiceCallback {

		/**
		 * Instantiates a new publish service callback.
		 * 
		 * @param messageCallback
		 *            the message callback
		 */
		public PublishServiceCallback(SCMessageCallback messageCallback) {
			super(SCPublishService.this, messageCallback);
		}

		/** {@inheritDoc} */
		@Override
		public void receive(SCMPMessage reply) {
			if (SCPublishService.this.sessionActive == false) {
				// client is not subscribed anymore - stop continuing
				return;
			}
			if (reply.isFault()) {
				// operation failed
				SCMPMessageFault fault = (SCMPMessageFault) reply;
				if (fault.getCause() != null) {
					super.receive(fault.getCause());
				} else { // EXC received
					SCServiceException ex = new SCServiceException("SCPublishService operation failed");
					ex.setSCMPError(fault.getHeader(SCMPHeaderAttributeKey.SC_ERROR_CODE));
					super.receive(ex);
				}
				return;
			}
			// check if reply is real answer
			boolean noData = reply.getHeaderFlag(SCMPHeaderAttributeKey.NO_DATA);
			if (noData == false) {
				// data reply received - give to application
				SCPublishMessage messageReply = new SCPublishMessage();
				messageReply.setData(reply.getBody());
				messageReply.setCompressed(reply.getHeaderFlag(SCMPHeaderAttributeKey.COMPRESSION));
				messageReply.setSessionId(reply.getSessionId());
				try {
					messageReply.setMask(reply.getHeader(SCMPHeaderAttributeKey.MASK));
					messageReply.setMessageInfo(reply.getHeader(SCMPHeaderAttributeKey.MSG_INFO));
					if (reply.getHeaderInt(SCMPHeaderAttributeKey.APP_ERROR_CODE) != null) {
						messageReply.setAppErrorCode(reply.getHeaderInt(SCMPHeaderAttributeKey.APP_ERROR_CODE));
						messageReply.setAppErrorText(reply.getHeader(SCMPHeaderAttributeKey.APP_ERROR_TEXT));
					}
				} catch (SCMPValidatorException ex) {
					logger.warn("attributes invalid when setting in scmessage");
				}
				this.messageCallback.receive(messageReply);
				// inform service request is completed
				this.service.setRequestComplete();
			}
			if (SCPublishService.this.sessionActive) {
				// client is still subscribed - CRP again
				try {
					SCPublishService.this.receivePublication();
				} catch (Exception e) {
					logger.info("subscribed " + e.toString());
					SCMPMessageFault fault = new SCMPMessageFault(e);
					super.receive(fault);
					return;
				}
			}
		}
	}
}