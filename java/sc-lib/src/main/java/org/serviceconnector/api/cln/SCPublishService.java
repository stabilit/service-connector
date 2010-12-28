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
import org.serviceconnector.api.SCSubscribeMessage;
import org.serviceconnector.call.SCMPCallFactory;
import org.serviceconnector.call.SCMPClnChangeSubscriptionCall;
import org.serviceconnector.call.SCMPClnSubscribeCall;
import org.serviceconnector.call.SCMPClnUnsubscribeCall;
import org.serviceconnector.call.SCMPReceivePublicationCall;
import org.serviceconnector.net.req.SCRequester;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.service.SCServiceException;
import org.serviceconnector.util.ValidatorUtility;

/**
 * The Class PublishService. PublishService is a remote interface in client API to a publish service and provides communication
 * functions.
 */
public class SCPublishService extends SCService {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(SCPublishService.class);
	/** The no data interval seconds. */
	private int noDataIntervalSeconds;

	public SCPublishService(SCClient scClient, String serviceName, SCRequester requester) {
		super(scClient, serviceName, requester);
		this.noDataIntervalSeconds = 0;
		this.messageCallback = null;
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
	 *            the SC subscribe message
	 * @param scMessageCallback
	 *            the SC message callback
	 * @return the SC subscribe message
	 * @throws Exception
	 *             the exception
	 */
	public SCSubscribeMessage subscribe(int operationTimeoutSeconds, SCSubscribeMessage scSubscribeMessage,
			SCMessageCallback scMessageCallback) throws Exception {
		// 1. checking preconditions and initialize
		if (this.sessionActive) {
			throw new SCServiceException("already subscribed");
		}
		if (scSubscribeMessage == null) {
			throw new SCServiceException("scSubscribeMessage can not be null");
		}
		if (scMessageCallback == null) {
			throw new InvalidParameterException("Callback must be set.");
		}
		this.noDataIntervalSeconds = scSubscribeMessage.getNoDataIntervalInSeconds();
		this.messageCallback = scMessageCallback;
		this.requester.getContext().getSCMPMsgSequenceNr().reset();
		// 2. initialize call & invoke
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
			throw new SCServiceException("subscribe failed", e);
		}
		// 3. receiving reply and error handling
		SCMPMessage reply = callback.getMessageSync(operationTimeoutSeconds * Constants.SEC_TO_MILLISEC_FACTOR);
		if (reply.isFault() || reply.getHeaderFlag(SCMPHeaderAttributeKey.REJECT_SESSION)) {
			// reply is fault or rejected
			SCServiceException ex = new SCServiceException("subscribe failed");
			ex.setSCErrorCode(reply.getHeader(SCMPHeaderAttributeKey.SC_ERROR_CODE));
			ex.setSCErrorText(reply.getHeader(SCMPHeaderAttributeKey.SC_ERROR_TEXT));
			ex.setAppErrorCode(reply.getHeaderInt(SCMPHeaderAttributeKey.APP_ERROR_CODE));
			ex.setAppErrorText(reply.getHeader(SCMPHeaderAttributeKey.APP_ERROR_TEXT));
			throw ex;
		}
		// 4. post process, reply to client
		this.sessionId = reply.getSessionId();
		this.sessionActive = true;
		this.receivePublication();
		SCSubscribeMessage replyToClient = new SCSubscribeMessage();
		replyToClient.setData(reply.getBody());
		replyToClient.setCompressed(reply.getHeaderFlag(SCMPHeaderAttributeKey.COMPRESSION));
		replyToClient.setSessionId(this.sessionId);
		replyToClient.setAppErrorCode(reply.getHeaderInt(SCMPHeaderAttributeKey.APP_ERROR_CODE));
		replyToClient.setAppErrorText(reply.getHeader(SCMPHeaderAttributeKey.APP_ERROR_TEXT));
		return replyToClient;
	}

	/**
	 * Change subscription.
	 * 
	 * @param scSubscribeMessage
	 *            the SC subscribe message
	 * @return the SC subscribe message
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
	 *            the SC subscribe message
	 * @return the SC subscribe message
	 * @throws Exception
	 *             the exception
	 */
	public synchronized SCSubscribeMessage changeSubscription(int operationTimeoutSeconds, SCSubscribeMessage scSubscribeMessage)
			throws Exception {
		// 1. checking preconditions and initialize
		if (this.sessionActive == false) {
			throw new SCServiceException("changeSubscription not possible - not subscribed");
		}
		if (scSubscribeMessage == null) {
			throw new SCServiceException("scSubscribeMessage can not be null");
		}
		this.requester.getContext().getSCMPMsgSequenceNr().incrementMsgSequenceNr();
		// 2. initialize call & invoke
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
			throw new SCServiceException("change subscription failed", e);
		}
		// 3. receiving reply and error handling
		SCMPMessage reply = callback.getMessageSync(operationTimeoutSeconds * Constants.SEC_TO_MILLISEC_FACTOR);
		if (reply.isFault() || reply.getHeaderFlag(SCMPHeaderAttributeKey.REJECT_SESSION)) {
			// reply is fault or rejected
			SCServiceException ex = new SCServiceException("change subscription failed");
			ex.setSCErrorCode(reply.getHeader(SCMPHeaderAttributeKey.SC_ERROR_CODE));
			ex.setSCErrorText(reply.getHeader(SCMPHeaderAttributeKey.SC_ERROR_TEXT));
			ex.setAppErrorCode(reply.getHeaderInt(SCMPHeaderAttributeKey.APP_ERROR_CODE));
			ex.setAppErrorText(reply.getHeader(SCMPHeaderAttributeKey.APP_ERROR_TEXT));
			throw ex;
		}
		// 4. post process, reply to client
		SCSubscribeMessage replyToClient = new SCSubscribeMessage();
		replyToClient.setData(reply.getBody());
		replyToClient.setCompressed(reply.getHeaderFlag(SCMPHeaderAttributeKey.COMPRESSION));
		replyToClient.setSessionId(this.sessionId);
		replyToClient.setAppErrorCode(reply.getHeaderInt(SCMPHeaderAttributeKey.APP_ERROR_CODE));
		replyToClient.setAppErrorText(reply.getHeader(SCMPHeaderAttributeKey.APP_ERROR_TEXT));
		return replyToClient;
	}

	/**
	 * Sends a receive publication to the SC.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	synchronized void receivePublication() {
		// 1. checking preconditions and initialize
		if (this.sessionActive == false) {
			return;
		}
		this.requester.getContext().getSCMPMsgSequenceNr().incrementMsgSequenceNr();
		// 2. initialize call & invoke
		SCPublishServiceCallback callback = new SCPublishServiceCallback(this, this.messageCallback);
		SCMPReceivePublicationCall receivePublicationCall = (SCMPReceivePublicationCall) SCMPCallFactory.RECEIVE_PUBLICATION
				.newInstance(this.requester, this.serviceName, this.sessionId);
		try {
			receivePublicationCall.invoke(callback, Constants.SEC_TO_MILLISEC_FACTOR
					* (Constants.DEFAULT_OPERATION_TIMEOUT_SECONDS + this.noDataIntervalSeconds));
		} catch (Exception e) {
			// inactivate the session
			this.sessionActive = false;
			SCServiceException ex = new SCServiceException("receive publication failed");
			ex.setSCErrorCode(SCMPError.BROKEN_SESSION.getErrorCode());
			ex.setSCErrorText("receive publication failed");
			this.messageCallback.receive(ex);
			return;
		}
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
	 *            the SC subscribe message
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
		// 1. checking preconditions and initialize
		if (this.sessionActive == false) {
			// unsubscribe not possible - not subscribed on this service just ignore
			return;
		}
		this.sessionActive = false;
		if (scSubscribeMessage != null) {
			// scSubscribeMessage might be null for unsubscribe operation
			ValidatorUtility.validateStringLengthIgnoreNull(1, scSubscribeMessage.getSessionInfo(), 256,
					SCMPError.HV_WRONG_SESSION_INFO);
		}
		this.requester.getContext().getSCMPMsgSequenceNr().incrementMsgSequenceNr();
		// 2. initialize call & invoke
		try {
			SCServiceCallback callback = new SCServiceCallback(true);
			SCMPClnUnsubscribeCall unsubscribeCall = (SCMPClnUnsubscribeCall) SCMPCallFactory.CLN_UNSUBSCRIBE_CALL.newInstance(
					this.requester, this.serviceName, this.sessionId);
			if (scSubscribeMessage != null) {
				unsubscribeCall.setSessionInfo(scSubscribeMessage.getSessionInfo());
			}
			try {
				unsubscribeCall.invoke(callback, operationTimeoutSeconds * Constants.SEC_TO_MILLISEC_FACTOR);
			} catch (Exception e) {
				throw new SCServiceException("unsubscribe failed", e);
			}
			// 3. receiving reply and error handling
			SCMPMessage reply = callback.getMessageSync(operationTimeoutSeconds * Constants.SEC_TO_MILLISEC_FACTOR);
			if (reply.isFault()) {
				SCServiceException ex = new SCServiceException("unsubscribe failed");
				ex.setSCErrorCode(reply.getHeader(SCMPHeaderAttributeKey.SC_ERROR_CODE));
				ex.setSCErrorText(reply.getHeader(SCMPHeaderAttributeKey.SC_ERROR_TEXT));
				throw ex;
			}
		} finally {
			// 4. post process, reply to client
			this.sessionId = null;
		}
	}

	/**
	 * Checks if is subscribed.
	 * 
	 * @return true, if is subscribed
	 */
	public boolean isSubscribed() {
		return this.sessionActive;
	}
}