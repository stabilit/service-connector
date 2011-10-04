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

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.serviceconnector.Constants;
import org.serviceconnector.api.SCMessage;
import org.serviceconnector.api.SCServiceException;
import org.serviceconnector.call.SCMPClnCreateSessionCall;
import org.serviceconnector.call.SCMPClnDeleteSessionCall;
import org.serviceconnector.call.SCMPClnExecuteCall;
import org.serviceconnector.call.SCMPEchoCall;
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.log.PerformanceLogger;
import org.serviceconnector.net.req.SCRequester;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.util.ITimeout;
import org.serviceconnector.util.TimeoutWrapper;
import org.serviceconnector.util.ValidatorUtility;

/**
 * The Class SessionService. SessionService is a remote interface in client API to a session service and provides communication
 * functions. Creating a session service instance for API users should be done by calling SCClient newSessionService().<br />
 * <br />
 * State Diagram<br />
 * 
 * <pre>
 *        ||                                                            |-------execute-------|
 *        \/                                                            |                     |
 *    |---------|              |----------|                      |--------------|             |
 *    | initial |----attach--->| attached |----create session--->| has session  |--echo--|    |
 *    |         |<---detach----|          |<---delete session----| with service |<-------|    |
 *    |---------|              |----------|                      |--------------|<------------|
 *        ||
 *        \/
 * </pre>
 * 
 * After creating a session a delete session must be done in every case at the end of communication. A delete session may be called
 * multiple times. Nothing happens if the session turns inactive in meantime.
 * 
 * @author JTraber
 */
public class SCSessionService extends SCService {

	/** The Constant LOGGER. */
	@SuppressWarnings("unused")
	private static final Logger LOGGER = Logger.getLogger(SCSessionService.class);
	/** The sessionTimeout, timeout runs when session need to be refreshed. */
	private ScheduledFuture<TimeoutWrapper> sessionTimeout;
	/** The echo timeout in seconds. Time to wait for the reply of SC in case of an echo until the session is marked as dead. */
	private int echoTimeoutSeconds;
	/**
	 * The echo interval in seconds. Interval in seconds between two subsequent ECHO messages sent by the client to SC. The message
	 * is sent only when no message is pending.
	 */
	private int echoIntervalSeconds;

	/**
	 * Instantiates a new session service. Should only be used by service connector internal classes. Instantiating
	 * SCSessionService should be done by the SCClient method newSessionService().
	 * 
	 * @param scClient
	 *            the SC client
	 * @param serviceName
	 *            the service name
	 * @param requester
	 *            the requester
	 */
	SCSessionService(SCClient scClient, String serviceName, SCRequester requester) {
		super(scClient, serviceName, requester);
		this.sessionTimeout = null;
		this.echoTimeoutSeconds = Constants.DEFAULT_OPERATION_TIMEOUT_SECONDS;
		this.echoIntervalSeconds = Constants.DEFAULT_ECHO_INTERVAL_SECONDS;
	}

	/**
	 * Creates the session on SC. Uses default operation timeout to complete operation. <br />
	 * Once a create session has been called a delete session must be done at the end of communication.
	 * 
	 * @param scMessage
	 *            the SC message
	 * @param callback
	 *            the message callback which is used to inform the client in case of asynchronous operations
	 * @return the SC message
	 * @throws SCServiceException
	 *             session already created<br />
	 *             create session on SC failed<br />
	 *             error message received from SC<br />
	 *             create session has been rejected by the server<br />
	 * @throws SCMPValidatorException
	 *             create session message is null<br />
	 *             message callback is null<br />
	 */
	public synchronized SCMessage createSession(SCMessage scMessage, SCMessageCallback callback) throws SCServiceException,
			SCMPValidatorException {
		return this.createSession(Constants.DEFAULT_OPERATION_TIMEOUT_SECONDS, scMessage, callback);
	}

	/**
	 * Creates the session on SC. <br />
	 * Once a create session has been called a delete session must be done at the end of communication.
	 * 
	 * @param operationTimeoutSeconds
	 *            the allowed time in seconds to complete the operation
	 * @param scMessage
	 *            the SC message
	 * @param messageCallback
	 *            the message callback which is used to inform the client in case of asynchronous operations
	 * @return the SC message
	 * @throws SCServiceException
	 *             session already created<br />
	 *             create session on SC failed<br />
	 *             error message received from SC<br />
	 *             create session has been rejected by the server<br />
	 * @throws SCMPValidatorException
	 *             create session message is null<br />
	 *             message callback is null<br />
	 */
	public synchronized SCMessage createSession(int operationTimeoutSeconds, SCMessage scMessage, SCMessageCallback messageCallback)
			throws SCServiceException, SCMPValidatorException {
		// 1. checking preconditions and initialize
		if (this.sessionActive) {
			throw new SCServiceException("Session already created - delete session first.");
		}
		if (messageCallback == null) {
			throw new SCMPValidatorException("Message callback must be set.");
		}
		if (scMessage == null) {
			throw new SCMPValidatorException("Message (scMessage) must be set.");
		}
		// reset pendingRequest - necessary if service instances reused
		this.pendingRequest = false;
		this.messageCallback = messageCallback;
		this.requester.getSCMPMsgSequenceNr().reset();
		// 2. initialize call & invoke
		SCMPClnCreateSessionCall createSessionCall = new SCMPClnCreateSessionCall(this.requester, this.serviceName);
		createSessionCall.setRequestBody(scMessage.getData());
		createSessionCall.setCompressed(scMessage.isCompressed());
		createSessionCall.setSessionInfo(scMessage.getSessionInfo());
		createSessionCall.setEchoIntervalSeconds(this.echoIntervalSeconds);
		SCServiceCallback callback = new SCServiceCallback(true);
		try {
			createSessionCall.invoke(callback, operationTimeoutSeconds * Constants.SEC_TO_MILLISEC_FACTOR);
		} catch (Exception e) {
			throw new SCServiceException("Create session failed. ", e);
		}
		// 3. receiving reply and error handling
		SCMPMessage reply = callback.getMessageSync(operationTimeoutSeconds * Constants.SEC_TO_MILLISEC_FACTOR);
		if (reply.isFault() || reply.getHeaderFlag(SCMPHeaderAttributeKey.REJECT_SESSION)) {
			SCServiceException ex = new SCServiceException("Create session failed.");
			ex.setSCErrorCode(reply.getHeaderInt(SCMPHeaderAttributeKey.SC_ERROR_CODE));
			ex.setSCErrorText(reply.getHeader(SCMPHeaderAttributeKey.SC_ERROR_TEXT));
			ex.setAppErrorCode(reply.getHeaderInt(SCMPHeaderAttributeKey.APP_ERROR_CODE));
			ex.setAppErrorText(reply.getHeader(SCMPHeaderAttributeKey.APP_ERROR_TEXT));
			throw ex;
		}
		this.sessionId = reply.getSessionId();
		this.sessionActive = true;
		// 4. post process, reply to client
		this.triggerSessionTimeout();
		SCMessage replyToClient = new SCMessage();
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
	 * Execute with default operation timeout. Execute is a synchronous operation with the SC. Any reply will be received as return
	 * of the method.
	 * 
	 * @param requestMsg
	 *            the request message
	 * @return the SC message
	 * @throws SCServiceException
	 *             session not active (not created yet or dead)<br />
	 *             pending request, no second request allowed<br />
	 *             execute on SC failed<br />
	 *             error message received from SC<br />
	 * @throws SCMPValidatorException
	 *             execute message is null<br />
	 */
	public synchronized SCMessage execute(SCMessage requestMsg) throws SCServiceException, SCMPValidatorException {
		return this.execute(Constants.DEFAULT_OPERATION_TIMEOUT_SECONDS, requestMsg);
	}

	/**
	 * Execute. Execute is a synchronous operation with the SC. Any reply will be received as return of the method.
	 * 
	 * @param operationTimeoutSeconds
	 *            the allowed time in seconds to complete the operation
	 * @param scMessage
	 *            the SC message to execute
	 * @return the reply
	 * @throws SCServiceException
	 *             session not active (not created yet or dead)<br />
	 *             pending request, no second request allowed<br />
	 *             execute on SC failed<br />
	 *             error message received from SC<br />
	 * @throws SCMPValidatorException
	 *             execute message is null<br />
	 */
	public synchronized SCMessage execute(int operationTimeoutSeconds, SCMessage scMessage) throws SCServiceException,
			SCMPValidatorException {
		// 1. checking preconditions and initialize
		if (this.sessionActive == false) {
			throw new SCServiceException("Execute not possible, no active session.");
		}
		if (this.pendingRequest) {
			// pending Request - reply still outstanding
			throw new SCServiceException("Execute not possible, there is a pending request - two pending request are not allowed.");
		}
		if (scMessage == null) {
			throw new SCMPValidatorException("Message (scMessage) must be set.");
		}
		this.requester.getSCMPMsgSequenceNr().incrementAndGetMsgSequenceNr();
		// 2. initialize call & invoke
		SCMPClnExecuteCall clnExecuteCall = new SCMPClnExecuteCall(this.requester, this.serviceName, this.sessionId);
		clnExecuteCall.setMessageInfo(scMessage.getMessageInfo());
		clnExecuteCall.setCacheId(scMessage.getCacheId());
		clnExecuteCall.setCompressed(scMessage.isCompressed());
		clnExecuteCall.setPartSize(scMessage.getPartSize());
		clnExecuteCall.setRequestBody(scMessage.getData());
		SCServiceCallback callback = new SCServiceCallback(true);
		try {
			PerformanceLogger.beginThreadBound();
			clnExecuteCall.invoke(callback, operationTimeoutSeconds * Constants.SEC_TO_MILLISEC_FACTOR);
		} catch (Exception e) {
			PerformanceLogger.endThreadBound(this.sessionId);
			throw new SCServiceException("Execute request failed. ", e);
		}
		// 3. receiving reply and error handling
		SCMPMessage reply = callback.getMessageSync(operationTimeoutSeconds * Constants.SEC_TO_MILLISEC_FACTOR);
		PerformanceLogger.endThreadBound(this.sessionId);
		if (reply.isFault()) {
			SCServiceException scEx = new SCServiceException("Execute failed.");
			scEx.setSCErrorCode(reply.getHeaderInt(SCMPHeaderAttributeKey.SC_ERROR_CODE));
			scEx.setSCErrorText(reply.getHeader(SCMPHeaderAttributeKey.SC_ERROR_TEXT));
			throw scEx;
		}
		// 4. post process, reply to client
		SCMessage replyToClient = new SCMessage();
		replyToClient.setData(reply.getBody());
		replyToClient.setDataLength(reply.getBodyLength());
		replyToClient.setCompressed(reply.getHeaderFlag(SCMPHeaderAttributeKey.COMPRESSION));
		replyToClient.setSessionId(this.sessionId);
		replyToClient.setCacheId(reply.getCacheId());
		replyToClient.setCachePartNr(reply.getHeader(SCMPHeaderAttributeKey.CACHE_PARTN_NUMBER));
		replyToClient.setMessageInfo(reply.getHeader(SCMPHeaderAttributeKey.MSG_INFO));
		replyToClient.setAppErrorCode(reply.getHeaderInt(SCMPHeaderAttributeKey.APP_ERROR_CODE));
		replyToClient.setAppErrorText(reply.getHeader(SCMPHeaderAttributeKey.APP_ERROR_TEXT));
		return replyToClient;
	}

	/**
	 * Send with default operation timeout. Send is an asynchronous operation with the SC. Any reply will be informed over the
	 * callback.
	 * 
	 * @param requestMsg
	 *            the request message
	 * @throws SCServiceException
	 *             session not active (not created yet or dead)<br />
	 *             pending request, no second request allowed<br />
	 *             send on SC failed<br />
	 * @throws SCMPValidatorException
	 *             send message is null<br />
	 */
	public synchronized void send(SCMessage requestMsg) throws SCServiceException, SCMPValidatorException {
		this.send(Constants.DEFAULT_OPERATION_TIMEOUT_SECONDS, requestMsg);
	}

	/**
	 * Send. Asynchronous operation. Send is an asynchronous operation with the SC. Any reply will be informed over the callback.
	 * 
	 * @param operationtTimeoutSeconds
	 *            the allowed time in seconds to complete the operation
	 * @param scMessage
	 *            the SC message
	 * @throws SCServiceException
	 *             session not active (not created yet or dead)<br />
	 *             pending request, no second request allowed<br />
	 *             send on SC failed<br />
	 * @throws SCMPValidatorException
	 *             send message is null<br />
	 */
	public synchronized void send(int operationtTimeoutSeconds, SCMessage scMessage) throws SCServiceException,
			SCMPValidatorException {
		// 1. checking preconditions and initialize
		if (this.sessionActive == false) {
			throw new SCServiceException("Send not possible, no active session.");
		}
		if (this.pendingRequest) {
			// already executed before - reply still outstanding
			throw new SCServiceException("Send not possible, there is a pending request - two pending request are not allowed.");
		}
		if (scMessage == null) {
			throw new SCMPValidatorException("Message (scMessage) must be set.");
		}
		this.requester.getSCMPMsgSequenceNr().incrementAndGetMsgSequenceNr();
		// important to set pendingRequest true in case of asynchronous communication
		this.pendingRequest = true;
		// 2. initialize call & invoke
		SCMPClnExecuteCall clnExecuteCall = new SCMPClnExecuteCall(this.requester, this.serviceName, this.sessionId);
		clnExecuteCall.setMessageInfo(scMessage.getMessageInfo());
		clnExecuteCall.setCacheId(scMessage.getCacheId());
		clnExecuteCall.setCompressed(scMessage.isCompressed());
		clnExecuteCall.setPartSize(scMessage.getPartSize());
		clnExecuteCall.setRequestBody(scMessage.getData());
		SCServiceCallback scmpCallback = new SCServiceCallback(this, this.messageCallback);
		try {
			clnExecuteCall.invoke(scmpCallback, operationtTimeoutSeconds * Constants.SEC_TO_MILLISEC_FACTOR);
		} catch (Exception e) {
			this.pendingRequest = false;
			throw new SCServiceException("Send request failed. ", e);
		}
	}

	/**
	 * Echo. Refreshes the session on SC. Avoids session timeout.
	 */
	private void echo() {
		// 1. checking preconditions and initialize
		if (this.sessionActive == false) {
			return;
		}
		this.requester.getSCMPMsgSequenceNr().incrementAndGetMsgSequenceNr();
		// 2. initialize call & invoke
		SCMPEchoCall clnEchoCall = new SCMPEchoCall(this.requester, this.serviceName, this.sessionId);
		SCServiceCallback callback = new SCServiceCallback(true);
		try {
			PerformanceLogger.beginThreadBound();
			clnEchoCall.invoke(callback, this.echoTimeoutSeconds * Constants.SEC_TO_MILLISEC_FACTOR);
		} catch (Exception e) {
			PerformanceLogger.endThreadBound(this.sessionId);
			// inactivate the session
			this.sessionActive = false;
			SCServiceException ex = new SCServiceException("Refreshing session by echo failed, service=" + this.serviceName
					+ " sid=" + this.sessionId + ".");
			ex.setSCErrorCode(SCMPError.BROKEN_SESSION.getErrorCode());
			ex.setSCErrorText(SCMPError.BROKEN_SESSION.getErrorText("Can not send echo message for service=" + this.serviceName
					+ " sid=" + this.sessionId + "."));
			this.messageCallback.receive(ex);
			return;
		}
		// 3. receiving reply and error handling
		SCMPMessage reply = callback.getMessageSync(this.echoTimeoutSeconds * Constants.SEC_TO_MILLISEC_FACTOR);
		PerformanceLogger.endThreadBound(this.sessionId);
		if (reply.isFault()) {
			// inactivate the session
			this.sessionActive = false;
			SCServiceException ex = new SCServiceException("Refreshing session by echo failed, service=" + this.serviceName
					+ " sid=" + this.sessionId + ".");
			ex.setSCErrorCode(SCMPError.BROKEN_SESSION.getErrorCode());
			ex.setSCErrorText(SCMPError.BROKEN_SESSION.getErrorText("Can not send echo message for service=" + this.serviceName
					+ " sid=" + this.sessionId + "."));
			this.messageCallback.receive(ex);
			return;
		}
		// 4. post process, reply to client
		this.triggerSessionTimeout();
	}

	/**
	 * Delete session on SC with default operation timeout.
	 * A delete session may be called multiple times. Nothing happens if the session turns inactive in meantime.
	 * 
	 * @throws SCServiceException
	 *             pending request, no second request allowed<br />
	 *             delete session on SC failed<br />
	 *             error message received from SC<br />
	 */
	public synchronized void deleteSession() throws SCServiceException {
		this.deleteSession(Constants.DEFAULT_OPERATION_TIMEOUT_SECONDS, null);
	}

	/**
	 * Delete session on SC.
	 * A delete session may be called multiple times. Nothing happens if the session turns inactive in meantime.
	 * 
	 * @param operationTimeoutSeconds
	 *            allowed time to complete operation
	 * @throws SCServiceException
	 *             pending request, no second request allowed<br />
	 *             delete session on SC failed<br />
	 *             error message received from SC<br />
	 */
	public synchronized void deleteSession(int operationTimeoutSeconds) throws SCServiceException {
		this.deleteSession(operationTimeoutSeconds, null);
	}

	/**
	 * Delete session on SC with default operation timeout.
	 * A delete session may be called multiple times. Nothing happens if the session turns inactive in meantime.
	 * 
	 * @param scMessage
	 *            the SC message
	 * @throws SCServiceException
	 *             pending request, no second request allowed<br />
	 *             delete session on SC failed<br />
	 *             error message received from SC<br />
	 */
	public synchronized void deleteSession(SCMessage scMessage) throws SCServiceException {
		this.deleteSession(Constants.DEFAULT_OPERATION_TIMEOUT_SECONDS, scMessage);
	}

	/**
	 * Delete session on SC.
	 * A delete session may be called multiple times. Nothing happens if the session turns inactive in meantime.
	 * 
	 * @param operationTimeoutSeconds
	 *            the allowed time in seconds to complete the operation
	 * @param scMessage
	 *            the SC message
	 * @throws SCServiceException
	 *             pending request, no second request allowed<br />
	 *             delete session on SC failed<br />
	 *             error message received from SC<br />
	 */
	public synchronized void deleteSession(int operationTimeoutSeconds, SCMessage scMessage) throws SCServiceException {
		// 1. checking preconditions and initialize
		if (this.sessionActive == false) {
			// delete session not possible - no session on this service just ignore
			return;
		}
		if (this.pendingRequest) {
			// pending request - reply still outstanding
			throw new SCServiceException(
					"Delete session not possible, there is a pending request - two pending request are not allowed.");
		}
		// cancel session timeout even if its running already
		this.cancelSessionTimeout(true);
		this.requester.getSCMPMsgSequenceNr().incrementAndGetMsgSequenceNr();
		try {
			// 2. initialize call & invoke
			SCServiceCallback callback = new SCServiceCallback(true);
			SCMPClnDeleteSessionCall deleteSessionCall = new SCMPClnDeleteSessionCall(this.requester, this.serviceName,
					this.sessionId);
			if (scMessage != null) {
				// message might be null for deleteSession operation
				deleteSessionCall.setSessionInfo(scMessage.getSessionInfo());
			}
			try {
				deleteSessionCall.invoke(callback, operationTimeoutSeconds * Constants.SEC_TO_MILLISEC_FACTOR);
			} catch (Exception e) {
				throw new SCServiceException("Delete session failed ", e);
			}
			// 3. receiving reply and error handling
			SCMPMessage reply = callback.getMessageSync(operationTimeoutSeconds * Constants.SEC_TO_MILLISEC_FACTOR);
			if (reply.isFault()) {
				SCServiceException ex = new SCServiceException("Delete session failed.");
				ex.setSCErrorCode(reply.getHeaderInt(SCMPHeaderAttributeKey.SC_ERROR_CODE));
				ex.setSCErrorText(reply.getHeader(SCMPHeaderAttributeKey.SC_ERROR_TEXT));
				throw ex;
			}
		} finally {
			// 4. post process, reply to client
			this.sessionId = null;
			this.sessionActive = false;
		}
	}

	/**
	 * Trigger session timeout.
	 */
	@SuppressWarnings("unchecked")
	private void triggerSessionTimeout() {
		SCSessionTimeout sessTimeout = new SCSessionTimeout();
		TimeoutWrapper timeoutWrapper = new TimeoutWrapper(sessTimeout);
		this.sessionTimeout = (ScheduledFuture<TimeoutWrapper>) AppContext.eci_cri_Scheduler.schedule(timeoutWrapper,
				(echoIntervalSeconds * Constants.SEC_TO_MILLISEC_FACTOR), TimeUnit.MILLISECONDS);
	}

	/**
	 * Cancel session timeout.
	 * 
	 * @param mayInterruptIfRunning
	 *            the may interrupt if running
	 */
	private void cancelSessionTimeout(boolean mayInterruptIfRunning) {
		SCSessionService.this.sessionTimeout.cancel(mayInterruptIfRunning);
		// removes canceled timeouts
		AppContext.eci_cri_Scheduler.purge();
	}

	/**
	 * Sets the echo timeout in seconds. Time in seconds the an echo request waits to be confirmed. If no confirmation is received
	 * session is marked as dead.
	 * 
	 * @param echoTimeoutSeconds
	 *            time to wait for completion of an echo request
	 *            Example: 10
	 * @throws SCMPValidatorException
	 *             echoTimeoutSeconds > 1 and < 3600<br />
	 */
	public void setEchoTimeoutSeconds(int echoTimeoutSeconds) throws SCMPValidatorException {
		// validate in this case its a local needed information
		ValidatorUtility.validateInt(1, echoTimeoutSeconds, Constants.MAX_ECHO_TIMEOUT_VALUE, SCMPError.HV_WRONG_ECHO_TIMEOUT);
		this.echoTimeoutSeconds = echoTimeoutSeconds;
	}

	/**
	 * Gets the echo timeout in seconds. Time in seconds the an echo request waits to be confirmed.
	 * 
	 * @return the echo timeout in seconds
	 */
	public int getEchoTimeoutSeconds() {
		return this.echoTimeoutSeconds;
	}

	/**
	 * Sets the echo interval in seconds. Interval in seconds between two subsequent ECHO messages sent by the client to SC. The
	 * message is sent only when no message is pending.
	 * 
	 * @param echoIntervalSeconds
	 *            Validation: echoIntervalSeconds > 1 and < 3600<br />
	 *            Example: 360
	 */
	public void setEchoIntervalSeconds(int echoIntervalSeconds) {
		this.echoIntervalSeconds = echoIntervalSeconds;
	}

	/**
	 * Gets the echo interval in seconds.
	 * 
	 * @return the echo interval in seconds
	 */
	public int getEchoIntervalSeconds() {
		return this.echoIntervalSeconds;
	}

	/**
	 * Checks if has a session.
	 * 
	 * @return true, if is subscribed
	 */
	public boolean hasSession() {
		return this.sessionActive;
	}

	/**
	 * The Class SCSessionTimeout. Get control at the time a session refresh is needed. Takes care of sending an echo to SC which
	 * gets the session refreshed.
	 */
	private class SCSessionTimeout implements ITimeout {

		/**
		 * Time run out, need to send an echo to SC otherwise session gets deleted for session timeout reason.
		 */
		@Override
		public void timeout() {
			// send echo to SC
			SCSessionService.this.echo();
		}

		/** {@inheritDoc} */
		@Override
		public int getTimeoutMillis() {
			return SCSessionService.this.echoIntervalSeconds * Constants.SEC_TO_MILLISEC_FACTOR;
		}
	}
}