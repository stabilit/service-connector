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

import org.apache.log4j.Logger;
import org.serviceconnector.Constants;
import org.serviceconnector.api.SCServiceException;
import org.serviceconnector.api.SCSubscribeMessage;
import org.serviceconnector.call.SCMPClnChangeSubscriptionCall;
import org.serviceconnector.call.SCMPClnSubscribeCall;
import org.serviceconnector.call.SCMPClnUnsubscribeCall;
import org.serviceconnector.call.SCMPReceivePublicationCall;
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.log.PerformanceLogger;
import org.serviceconnector.net.req.SCRequester;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.util.ValidatorUtility;

/**
 * The Class PublishService. PublishService is a remote interface in client API to a publish service and provides communication
 * functions. Creating a publish service instance for API users should be done by calling SCClient newPublishService().<br />
 * <br />
 * State Diagram<br />
 * 
 * <pre>
 *        ||                                                        |-----receive publication-------|
 *        \/                                                        |                               |
 *    |---------|              |----------|                   |---------------|                     |
 *    | initial |----attach--->| attached |-----subscribe---->| subscribed to |------change-----|   |
 *    |         |<---detach----|          |<---unsubscribe----|    service    |<--subscription--|   |
 *    |---------|              |----------|                   |---------------|<--------------------|
 *        ||
 *        \/
 * </pre>
 * 
 * After subscribing the service an unsubscribe must follow in every case at the end of communication. Unsubscribe may be called
 * multiple times. Nothing happens if the subscription turns inactive in meantime.
 * 
 * @author JTraber
 */
public class SCPublishService extends SCService {

	/** The Constant LOGGER. */
	@SuppressWarnings("unused")
	private static final Logger LOGGER = Logger.getLogger(SCPublishService.class);
	/**
	 * The no data interval seconds.Interval in seconds the SC will wait to deliver RECEIVE_PUBLICATION response with noData flag
	 * set. Default = 0.
	 */
	private int noDataIntervalSeconds;

	/**
	 * Instantiates a new SC publish service. Should only be used by service connector internal classes. Instantiating
	 * SCPublishService should be done by the SCClient method newPublishService().
	 * 
	 * @param scClient
	 *            the SC client
	 * @param serviceName
	 *            the service name
	 * @param requester
	 *            the requester
	 */
	SCPublishService(SCClient scClient, String serviceName, SCRequester requester) {
		super(scClient, serviceName, requester);
		this.noDataIntervalSeconds = 0;
		this.messageCallback = null;
	}

	/**
	 * Subscribe to SC with default operation timeout. <br />
	 * Once a subscribe is called a unsubscribe need to be done at the end of a communication.
	 * 
	 * @param scSubscribeMessage
	 *            the SC subscribe message
	 * @param scMessageCallback
	 *            the SC message callback
	 * @return the SC subscribe message
	 * @throws SCMPValidatorException
	 *             subscribe message is null<br />
	 *             callback is null<br />
	 *             mask is invalid<br />
	 * @throws SCServiceException
	 *             instance already subscribed before<br />
	 *             subscribe to host failed<br />
	 *             error message received from SC <br />
	 */
	public synchronized SCSubscribeMessage subscribe(SCSubscribeMessage scSubscribeMessage, SCMessageCallback scMessageCallback)
			throws SCServiceException, SCMPValidatorException {
		return this.subscribe(Constants.DEFAULT_OPERATION_TIMEOUT_SECONDS, scSubscribeMessage, scMessageCallback);
	}

	/**
	 * Subscribe to SC. <br />
	 * Once a subscribe is called a unsubscribe need to be done at the end of a communication.
	 * 
	 * @param operationTimeoutSeconds
	 *            the allowed time in seconds to complete the operation
	 * @param scSubscribeMessage
	 *            the SC subscribe message
	 * @param scMessageCallback
	 *            the SC message callback
	 * @return the SC subscribe message
	 * @throws SCMPValidatorException
	 *             subscribe message is null<br />
	 *             callback is null<br />
	 *             mask is invalid<br />
	 * @throws SCServiceException
	 *             instance already subscribed before<br />
	 *             subscribe to host failed<br />
	 *             error message received from SC <br />
	 */
	public SCSubscribeMessage subscribe(int operationTimeoutSeconds, SCSubscribeMessage scSubscribeMessage,
			SCMessageCallback scMessageCallback) throws SCServiceException, SCMPValidatorException {
		// 1. checking preconditions and initialize
		if (this.sessionActive) {
			throw new SCServiceException(this.serviceName + " already subscribed.");
		}
		if (scSubscribeMessage == null) {
			throw new SCMPValidatorException("Subscribe message (scSubscribeMessage) must not be null.");
		}
		if (scMessageCallback == null) {
			throw new SCMPValidatorException("Callback must be set.");
		}
		this.noDataIntervalSeconds = scSubscribeMessage.getNoDataIntervalSeconds();
		String mask = scSubscribeMessage.getMask();
		ValidatorUtility.validateMask(mask, SCMPError.HV_WRONG_MASK);
		this.messageCallback = scMessageCallback;
		this.requester.getSCMPMsgSequenceNr().reset();
		// 2. initialize call & invoke
		SCServiceCallback callback = new SCServiceCallback(true);
		SCMPClnSubscribeCall subscribeCall = new SCMPClnSubscribeCall(this.requester, this.serviceName);
		subscribeCall.setMask(scSubscribeMessage.getMask());
		subscribeCall.setSessionInfo(scSubscribeMessage.getSessionInfo());
		subscribeCall.setNoDataIntervalSeconds(scSubscribeMessage.getNoDataIntervalSeconds());
		subscribeCall.setCompressed(scSubscribeMessage.isCompressed());
		subscribeCall.setRequestBody(scSubscribeMessage.getData());
		try {
			subscribeCall.invoke(callback, operationTimeoutSeconds * Constants.SEC_TO_MILLISEC_FACTOR);
		} catch (Exception e) {
			throw new SCServiceException("Subscribe failed.", e);
		}
		// 3. receiving reply and error handling
		SCMPMessage reply = callback.getMessageSync(operationTimeoutSeconds * Constants.SEC_TO_MILLISEC_FACTOR);
		if (reply.isFault() || reply.getHeaderFlag(SCMPHeaderAttributeKey.REJECT_SESSION)) {
			// reply is fault or rejected
			SCServiceException ex = new SCServiceException("Subscribe failed.");
			ex.setSCErrorCode(reply.getHeaderInt(SCMPHeaderAttributeKey.SC_ERROR_CODE));
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
		replyToClient.setDataLength(reply.getBodyLength());
		replyToClient.setCompressed(reply.getHeaderFlag(SCMPHeaderAttributeKey.COMPRESSION));
		replyToClient.setSessionId(this.sessionId);
		replyToClient.setSessionInfo(reply.getHeader(SCMPHeaderAttributeKey.SESSION_INFO));
		replyToClient.setAppErrorCode(reply.getHeaderInt(SCMPHeaderAttributeKey.APP_ERROR_CODE));
		replyToClient.setAppErrorText(reply.getHeader(SCMPHeaderAttributeKey.APP_ERROR_TEXT));
		return replyToClient;
	}

	/**
	 * Change subscription on SC with default operation timeout.
	 * 
	 * @param scSubscribeMessage
	 *            the SC subscribe message
	 * @return the SC subscribe message
	 * @throws SCMPValidatorException
	 *             subscribe message is null<br />
	 *             mask not valid<br />
	 * @throws SCServiceException
	 *             instance not subscribed<br />
	 *             change subscription to host failed<br />
	 *             error message received from SC <br />
	 */
	public synchronized SCSubscribeMessage changeSubscription(SCSubscribeMessage scSubscribeMessage) throws SCServiceException,
			SCMPValidatorException {
		return this.changeSubscription(Constants.DEFAULT_OPERATION_TIMEOUT_SECONDS, scSubscribeMessage);
	}

	/**
	 * Change subscription on SC.
	 * 
	 * @param operationTimeoutSeconds
	 *            the allowed time in seconds to complete the operation
	 * @param scSubscribeMessage
	 *            the SC subscribe message
	 * @return the SC subscribe message
	 * @throws SCMPValidatorException
	 *             subscribe message is null<br />
	 *             mask not valid<br />
	 * @throws SCServiceException
	 *             instance not subscribed<br />
	 *             change subscription to host failed<br />
	 *             error message received from SC <br />
	 */
	public synchronized SCSubscribeMessage changeSubscription(int operationTimeoutSeconds, SCSubscribeMessage scSubscribeMessage)
			throws SCServiceException, SCMPValidatorException {
		// 1. checking preconditions and initialize
		if (this.sessionActive == false) {
			throw new SCServiceException("ChangeSubscription not possible - not subscribed.");
		}
		if (scSubscribeMessage == null) {
			throw new SCMPValidatorException("Subscribe message (scSubscribeMessage) must not be null.");
		}
		String mask = scSubscribeMessage.getMask();
		ValidatorUtility.validateMask(mask, SCMPError.HV_WRONG_MASK);
		this.requester.getSCMPMsgSequenceNr().incrementAndGetMsgSequenceNr();
		// 2. initialize call & invoke
		SCMPClnChangeSubscriptionCall changeSubscriptionCall = new SCMPClnChangeSubscriptionCall(this.requester, this.serviceName,
				this.sessionId);
		changeSubscriptionCall.setMask(scSubscribeMessage.getMask());
		changeSubscriptionCall.setCompressed(scSubscribeMessage.isCompressed());
		changeSubscriptionCall.setRequestBody(scSubscribeMessage.getData());
		changeSubscriptionCall.setSessionInfo(scSubscribeMessage.getSessionInfo());
		SCServiceCallback callback = new SCServiceCallback(true);
		try {
			changeSubscriptionCall.invoke(callback, operationTimeoutSeconds * Constants.SEC_TO_MILLISEC_FACTOR);
		} catch (Exception e) {
			throw new SCServiceException("Change subscription failed.", e);
		}
		// 3. receiving reply and error handling
		SCMPMessage reply = callback.getMessageSync(operationTimeoutSeconds * Constants.SEC_TO_MILLISEC_FACTOR);
		if (reply.isFault() || reply.getHeaderFlag(SCMPHeaderAttributeKey.REJECT_SESSION)) {
			// reply is fault or rejected
			SCServiceException ex = new SCServiceException("Change subscription failed.");
			ex.setSCErrorCode(reply.getHeaderInt(SCMPHeaderAttributeKey.SC_ERROR_CODE));
			ex.setSCErrorText(reply.getHeader(SCMPHeaderAttributeKey.SC_ERROR_TEXT));
			ex.setAppErrorCode(reply.getHeaderInt(SCMPHeaderAttributeKey.APP_ERROR_CODE));
			ex.setAppErrorText(reply.getHeader(SCMPHeaderAttributeKey.APP_ERROR_TEXT));
			throw ex;
		}
		// 4. post process, reply to client
		SCSubscribeMessage replyToClient = new SCSubscribeMessage();
		replyToClient.setData(reply.getBody());
		replyToClient.setDataLength(reply.getBodyLength());
		replyToClient.setCompressed(reply.getHeaderFlag(SCMPHeaderAttributeKey.COMPRESSION));
		replyToClient.setSessionId(this.sessionId);
		replyToClient.setSessionInfo(reply.getHeader(SCMPHeaderAttributeKey.SESSION_INFO));
		replyToClient.setAppErrorCode(reply.getHeaderInt(SCMPHeaderAttributeKey.APP_ERROR_CODE));
		replyToClient.setAppErrorText(reply.getHeader(SCMPHeaderAttributeKey.APP_ERROR_TEXT));
		return replyToClient;
	}

	/**
	 * Sends a receive publication (CRP) to the SC. Is only used internally (method visibility).
	 * The registered message callback from the client gets informed in case of an error.
	 */
	synchronized void receivePublication() {
		// 1. checking preconditions and initialize
		if (this.sessionActive == false) {
			return;
		}
		this.requester.getSCMPMsgSequenceNr().incrementAndGetMsgSequenceNr();
		// 2. initialize call & invoke
		SCPublishServiceCallback callback = new SCPublishServiceCallback(this, this.messageCallback);
		SCMPReceivePublicationCall receivePublicationCall = new SCMPReceivePublicationCall(this.requester, this.serviceName,
				this.sessionId);
		try {
			PerformanceLogger.begin();
			receivePublicationCall.invoke(callback, Constants.SEC_TO_MILLISEC_FACTOR
					* (Constants.DEFAULT_OPERATION_TIMEOUT_SECONDS + this.noDataIntervalSeconds));
		} catch (Exception e) {
			PerformanceLogger.end();
			// inactivate the session
			this.sessionActive = false;
			SCServiceException ex = new SCServiceException("Receive publication failed.");
			ex.setSCErrorCode(SCMPError.BROKEN_SUBSCRIPTION.getErrorCode());
			ex.setSCErrorText(SCMPError.BROKEN_SUBSCRIPTION.getErrorText("Receive publication for service=" + this.serviceName
					+ " failed."));
			this.messageCallback.receive(ex);
			return;
		}
		PerformanceLogger.end();
	}

	/**
	 * Unsubscribe from SC with default operation timeout.
	 * Unsubscribe may be called multiple times. Nothing happens if the subscription turns inactive in meantime.
	 * 
	 * @throws SCServiceException
	 *             unsubscribe from SC failed<br />
	 *             error message received from SC <br />
	 */
	public synchronized void unsubscribe() throws SCServiceException {
		this.unsubscribe(Constants.DEFAULT_OPERATION_TIMEOUT_SECONDS, null);
	}

	/**
	 * Unsubscribe from SC.
	 * Unsubscribe may be called multiple times. Nothing happens if the subscription turns inactive in meantime.
	 * 
	 * @param scSubscribeMessage
	 *            the SC subscribe message
	 * @throws SCServiceException
	 *             unsubscribe from SC failed<br />
	 *             error message received from SC <br />
	 */
	public synchronized void unsubscribe(SCSubscribeMessage scSubscribeMessage) throws SCServiceException {
		this.unsubscribe(Constants.DEFAULT_OPERATION_TIMEOUT_SECONDS, scSubscribeMessage);
	}

	/**
	 * Unsubscribe from SC.
	 * Unsubscribe may be called multiple times. Nothing happens if the subscription turns inactive in meantime.
	 * 
	 * @param operationTimeoutSeconds
	 *            the allowed time in seconds to complete the operation
	 * @throws SCServiceException
	 *             unsubscribe from SC failed<br />
	 *             error message received from SC <br />
	 */
	public synchronized void unsubscribe(int operationTimeoutSeconds) throws SCServiceException {
		this.unsubscribe(operationTimeoutSeconds, null);
	}

	/**
	 * Unsubscribe from SC.
	 * Unsubscribe may be called multiple times. Nothing happens if the subscription turns inactive in meantime.
	 * 
	 * @param operationTimeoutSeconds
	 *            the allowed time in seconds to complete the operation
	 * @param scSubscribeMessage
	 *            the SC subscribe message
	 * @throws SCServiceException
	 *             unsubscribe from SC failed<br />
	 *             error message received from SC <br />
	 */
	public synchronized void unsubscribe(int operationTimeoutSeconds, SCSubscribeMessage scSubscribeMessage)
			throws SCServiceException {
		// 1. checking preconditions and initialize
		if (this.sessionActive == false) {
			// unsubscribe not possible - not subscribed on this service just ignore
			return;
		}
		this.sessionActive = false;
		this.requester.getSCMPMsgSequenceNr().incrementAndGetMsgSequenceNr();
		// 2. initialize call & invoke
		try {
			SCServiceCallback callback = new SCServiceCallback(true);
			SCMPClnUnsubscribeCall unsubscribeCall = new SCMPClnUnsubscribeCall(this.requester, this.serviceName, this.sessionId);
			if (scSubscribeMessage != null) {
				unsubscribeCall.setSessionInfo(scSubscribeMessage.getSessionInfo());
			}
			try {
				unsubscribeCall.invoke(callback, operationTimeoutSeconds * Constants.SEC_TO_MILLISEC_FACTOR);
			} catch (Exception e) {
				throw new SCServiceException("Unsubscribe failed.", e);
			}
			// 3. receiving reply and error handling
			SCMPMessage reply = callback.getMessageSync(operationTimeoutSeconds * Constants.SEC_TO_MILLISEC_FACTOR);
			if (reply.isFault()) {
				SCServiceException ex = new SCServiceException("Unsubscribe failed.");
				ex.setSCErrorCode(reply.getHeaderInt(SCMPHeaderAttributeKey.SC_ERROR_CODE));
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