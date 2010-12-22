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
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.serviceconnector.Constants;
import org.serviceconnector.api.SCMessage;
import org.serviceconnector.call.SCMPCallFactory;
import org.serviceconnector.call.SCMPClnCreateSessionCall;
import org.serviceconnector.call.SCMPClnDeleteSessionCall;
import org.serviceconnector.call.SCMPClnExecuteCall;
import org.serviceconnector.call.SCMPEchoCall;
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.net.req.SCRequester;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.service.SCServiceException;
import org.serviceconnector.util.ITimeout;
import org.serviceconnector.util.TimeoutWrapper;
import org.serviceconnector.util.ValidatorUtility;

/**
 * The Class SessionService. SessionService is a remote interface in client API to a session service and provides communication
 * functions.
 * 
 * @author JTraber
 */
public class SCSessionService extends SCService {

	/** The Constant logger. */
	@SuppressWarnings("unused")
	private final static Logger logger = Logger.getLogger(SCSessionService.class);
	/** The sessionTimeout, timeout runs when session need to be refreshed. */
	private ScheduledFuture<TimeoutWrapper> sessionTimeout;
	/** The echo timeout in seconds. */
	private int echoTimeoutInSeconds;
	/** The echo interval in seconds. */
	private int echoIntervalInSeconds;

	/**
	 * Instantiates a new session service.
	 * 
	 * @param serviceName
	 *            the service name
	 * @param context
	 *            the context
	 */
	public SCSessionService(SCClient scClient, String serviceName, SCRequester requester) {
		super(scClient, serviceName, requester);
		this.sessionTimeout = null;
		this.echoTimeoutInSeconds = Constants.DEFAULT_OPERATION_TIMEOUT_SECONDS;
		this.echoIntervalInSeconds = Constants.DEFAULT_ECHO_INTERVAL_SECONDS;
	}

	/**
	 * Creates the session.
	 * 
	 * @param scMessage
	 *            the sc message
	 * @param callback
	 *            the callback
	 * @return the sC message
	 * @throws Exception
	 *             the exception
	 */
	public synchronized SCMessage createSession(SCMessage scMessage, SCMessageCallback callback) throws Exception {
		return this.createSession(Constants.DEFAULT_OPERATION_TIMEOUT_SECONDS, scMessage, callback);
	}

	/**
	 * Creates the session.
	 * 
	 * @param operationTimeoutSeconds
	 *            the operation timeout seconds
	 * @param scMessage
	 *            the sc message
	 * @param messageCallback
	 *            the message callback which is used to inform the client in case of asynchronous operations
	 * @return the sc message
	 * @throws Exception
	 *             the exception
	 */
	public synchronized SCMessage createSession(int operationTimeoutSeconds, SCMessage scMessage, SCMessageCallback messageCallback)
			throws Exception {
		// 1. checking preconditions and initialize
		if (this.sessionActive) {
			throw new SCServiceException("session already created - delete session first.");
		}
		if (messageCallback == null) {
			throw new SCServiceException("message callback must be set.");
		}
		if (scMessage == null) {
			throw new SCServiceException("scMessage must be set.");
		}
		if (scMessage.getDataLength() > Constants.MAX_MESSAGE_SIZE) {
			throw new SCServiceException("message > 60kB not allowed");
		}
		ValidatorUtility.validateInt(1, operationTimeoutSeconds, 3600, SCMPError.HV_WRONG_OPERATION_TIMEOUT);
		ValidatorUtility.validateStringLengthIgnoreNull(1, scMessage.getSessionInfo(), 256, SCMPError.HV_WRONG_SESSION_INFO);
		this.messageCallback = messageCallback;
		this.requester.getContext().getSCMPMsgSequenceNr().reset();
		// 2. initialize call & invoke
		SCMPClnCreateSessionCall createSessionCall = (SCMPClnCreateSessionCall) SCMPCallFactory.CLN_CREATE_SESSION_CALL.newInstance(
				this.requester, this.serviceName);
		createSessionCall.setRequestBody(scMessage.getData());
		createSessionCall.setCompressed(scMessage.isCompressed());
		createSessionCall.setSessionInfo(scMessage.getSessionInfo());
		createSessionCall.setEchoIntervalSeconds(this.echoIntervalInSeconds);
		SCServiceCallback callback = new SCServiceCallback(true);
		try {
			createSessionCall.invoke(callback, operationTimeoutSeconds * Constants.SEC_TO_MILLISEC_FACTOR);
		} catch (Exception e) {
			throw new SCServiceException("create session failed ", e);
		}
		// 3. receiving reply and error handling
		SCMPMessage reply = callback.getMessageSync(operationTimeoutSeconds * Constants.SEC_TO_MILLISEC_FACTOR);
		if (reply.isFault() || reply.getHeaderFlag(SCMPHeaderAttributeKey.REJECT_SESSION)) {
			SCServiceException ex = new SCServiceException("create session failed");
			ex.setSCMPError(reply.getHeader(SCMPHeaderAttributeKey.SC_ERROR_CODE));
			ex.setSCMPDetailErrorText(reply.getHeader(SCMPHeaderAttributeKey.SC_ERROR_TEXT));
			ex.setAppErrorCode(reply.getHeaderInt(SCMPHeaderAttributeKey.APP_ERROR_CODE));
			ex.setAppErrorText(reply.getHeader(SCMPHeaderAttributeKey.APP_ERROR_TEXT));
			throw ex;
		}
		// 4. post process, reply to client
		this.triggerSessionTimeout();
		this.sessionId = reply.getSessionId();
		this.sessionActive = true;
		SCMessage replyToClient = new SCMessage();
		replyToClient.setData(reply.getBody());
		replyToClient.setCompressed(reply.getHeaderFlag(SCMPHeaderAttributeKey.COMPRESSION));
		replyToClient.setSessionId(this.sessionId);
		replyToClient.setAppErrorCode(reply.getHeaderInt(SCMPHeaderAttributeKey.APP_ERROR_CODE));
		replyToClient.setAppErrorText(reply.getHeader(SCMPHeaderAttributeKey.APP_ERROR_TEXT));
		return replyToClient;
	}

	public synchronized SCMessage execute(SCMessage requestMsg) throws Exception {
		return this.execute(Constants.DEFAULT_OPERATION_TIMEOUT_SECONDS, requestMsg);
	}

	public synchronized SCMessage execute(int operationTimeoutSeconds, SCMessage scMessage) throws Exception {
		// 1. checking preconditions and initialize
		if (this.sessionActive == false) {
			throw new SCServiceException("execute not possible, no active session.");
		}
		if (this.pendingRequest) {
			// pending Request - reply still outstanding
			throw new SCServiceException("execute not possible, there is a pending request - two pending request are not allowed.");
		}
		if (scMessage == null) {
			throw new SCServiceException("scMessage must be set.");
		}
		ValidatorUtility.validateInt(1, operationTimeoutSeconds, 3600, SCMPError.HV_WRONG_OPERATION_TIMEOUT);
		ValidatorUtility.validateStringLengthIgnoreNull(1, scMessage.getMessageInfo(), 256, SCMPError.HV_WRONG_MESSAGE_INFO);
		ValidatorUtility.validateStringLengthIgnoreNull(1, scMessage.getCacheId(), 256, SCMPError.HV_WRONG_SESSION_INFO);
		// cancel session timeout even if its running already
		this.cancelSessionTimeout(true);
		this.requester.getContext().getSCMPMsgSequenceNr().incrementMsgSequenceNr();
		// 2. initialize call & invoke
		SCMPClnExecuteCall clnExecuteCall = (SCMPClnExecuteCall) SCMPCallFactory.CLN_EXECUTE_CALL.newInstance(this.requester,
				this.serviceName, this.sessionId);
		clnExecuteCall.setMessagInfo(scMessage.getMessageInfo());
		clnExecuteCall.setCacheId(scMessage.getCacheId());
		clnExecuteCall.setCompressed(scMessage.isCompressed());
		clnExecuteCall.setRequestBody(scMessage.getData());
		SCServiceCallback callback = new SCServiceCallback(true);
		try {
			clnExecuteCall.invoke(callback, operationTimeoutSeconds * Constants.SEC_TO_MILLISEC_FACTOR);
		} catch (Exception e) {
			this.triggerSessionTimeout();
			throw new SCServiceException("execute reuest failed ", e);
		}
		// 3. receiving reply and error handling
		SCMPMessage reply = callback.getMessageSync(operationTimeoutSeconds * Constants.SEC_TO_MILLISEC_FACTOR);
		this.triggerSessionTimeout();
		if (reply.isFault()) {
			SCServiceException scEx = new SCServiceException("execute failed");
			scEx.setSCMPError(reply.getHeader(SCMPHeaderAttributeKey.SC_ERROR_CODE));
			scEx.setSCMPDetailErrorText(reply.getHeader(SCMPHeaderAttributeKey.SC_ERROR_TEXT));
			throw scEx;
		}
		// 4. post process, reply to client
		SCMessage replyToClient = new SCMessage();
		replyToClient.setData(reply.getBody());
		replyToClient.setCompressed(reply.getHeaderFlag(SCMPHeaderAttributeKey.COMPRESSION));
		replyToClient.setSessionId(this.sessionId);
		replyToClient.setCacheId(reply.getCacheId());
		replyToClient.setMessageInfo(reply.getHeader(SCMPHeaderAttributeKey.MSG_INFO));
		replyToClient.setAppErrorCode(reply.getHeaderInt(SCMPHeaderAttributeKey.APP_ERROR_CODE));
		replyToClient.setAppErrorText(reply.getHeader(SCMPHeaderAttributeKey.APP_ERROR_TEXT));
		return replyToClient;
	}

	public synchronized void send(SCMessage requestMsg) throws Exception {
		this.send(Constants.DEFAULT_OPERATION_TIMEOUT_SECONDS, requestMsg);
	}

	public synchronized void send(int operationtTimeoutSeconds, SCMessage scMessage) throws Exception {
		// 1. checking preconditions and initialize
		if (this.sessionActive == false) {
			throw new SCServiceException("send not possible, no active session.");
		}
		if (this.pendingRequest) {
			// already executed before - reply still outstanding
			throw new SCServiceException("send not possible, there is a pending request - two pending request are not allowed.");
		}
		if (scMessage == null) {
			throw new InvalidParameterException("Message must be set.");
		}
		ValidatorUtility.validateInt(1, operationtTimeoutSeconds, 3600, SCMPError.HV_WRONG_OPERATION_TIMEOUT);
		ValidatorUtility.validateStringLengthIgnoreNull(1, scMessage.getMessageInfo(), 256, SCMPError.HV_WRONG_MESSAGE_INFO);
		ValidatorUtility.validateStringLengthIgnoreNull(1, scMessage.getCacheId(), 256, SCMPError.HV_WRONG_SESSION_INFO);
		// cancel session timeout even if its running already
		this.cancelSessionTimeout(true);
		this.requester.getContext().getSCMPMsgSequenceNr().incrementMsgSequenceNr();
		// important to set pendingRequest true in case of asynchronous communication
		this.pendingRequest = true;
		// 2. initialize call & invoke
		SCMPClnExecuteCall clnExecuteCall = (SCMPClnExecuteCall) SCMPCallFactory.CLN_EXECUTE_CALL.newInstance(this.requester,
				this.serviceName, this.sessionId);
		clnExecuteCall.setMessagInfo(scMessage.getMessageInfo());
		clnExecuteCall.setCacheId(scMessage.getCacheId());
		clnExecuteCall.setCompressed(scMessage.isCompressed());
		clnExecuteCall.setRequestBody(scMessage.getData());
		SCServiceCallback scmpCallback = new SCServiceCallback(this, this.messageCallback);
		try {
			clnExecuteCall.invoke(scmpCallback, operationtTimeoutSeconds * Constants.SEC_TO_MILLISEC_FACTOR);
		} catch (Exception e) {
			this.pendingRequest = false;
			this.triggerSessionTimeout();
			throw new SCServiceException("send request failed ", e);
		}
	}

	/**
	 * Echo. Refreshes the session on SC. Avoids session timeout.
	 */
	private synchronized void echo() {
		// 1. checking preconditions and initialize
		if (this.sessionActive == false) {
			return;
		}
		if (this.pendingRequest) {
			// an operation is running no echo allowed
			return;
		}
		// cancel session timeout even if its running already
		this.cancelSessionTimeout(true);
		this.requester.getContext().getSCMPMsgSequenceNr().incrementMsgSequenceNr();
		// 2. initialize call & invoke
		SCMPEchoCall clnEchoCall = (SCMPEchoCall) SCMPCallFactory.ECHO_CALL.newInstance(this.requester, this.serviceName,
				this.sessionId);
		SCServiceCallback callback = new SCServiceCallback(true);
		try {
			clnEchoCall.invoke(callback, this.echoTimeoutInSeconds * Constants.SEC_TO_MILLISEC_FACTOR);
		} catch (Exception e) {
			// inactivate the session
			this.sessionActive = false;
			SCServiceException ex = new SCServiceException("refreshing session by echo failed");
			ex.setSCMPError(SCMPError.BROKEN_SESSION);
			ex.setSCMPDetailErrorText("refreshing session by echo failed");
			this.messageCallback.receive(ex);
			return;
		}
		// 3. receiving reply and error handling
		SCMPMessage reply = callback.getMessageSync(this.echoTimeoutInSeconds * Constants.SEC_TO_MILLISEC_FACTOR);
		if (reply.isFault()) {
			// inactivate the session
			this.sessionActive = false;
			SCServiceException ex = new SCServiceException("refreshing session by echo failed");
			ex.setSCMPError(SCMPError.BROKEN_SESSION);
			ex.setSCMPDetailErrorText("refreshing session by echo failed");
			this.messageCallback.receive(ex);
			return;
		}
		// 4. post process, reply to client
		this.triggerSessionTimeout();
	}

	/**
	 * Delete session.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	public synchronized void deleteSession() throws Exception {
		this.deleteSession(Constants.DEFAULT_OPERATION_TIMEOUT_SECONDS, null);
	}

	/**
	 * Delete session.
	 * 
	 * @param operationTimeoutSeconds
	 *            the operation timeout seconds
	 * @throws Exception
	 *             the exception
	 */
	public synchronized void deleteSession(int operationTimeoutSeconds) throws Exception {
		this.deleteSession(operationTimeoutSeconds, null);
	}

	/**
	 * Delete session.
	 * 
	 * @param scMessage
	 *            the sc message
	 * @throws Exception
	 *             the exception
	 */
	public synchronized void deleteSession(SCMessage scMessage) throws Exception {
		this.deleteSession(Constants.DEFAULT_OPERATION_TIMEOUT_SECONDS, scMessage);
	}

	/**
	 * Delete session.
	 * 
	 * @param operationTimeoutSeconds
	 *            the timeout in seconds
	 * @throws Exception
	 *             the exception
	 */
	public synchronized void deleteSession(int operationTimeoutSeconds, SCMessage scMessage) throws Exception {
		// 1. checking preconditions and initialize
		if (this.sessionActive == false) {
			// delete session not possible - no session on this service just ignore
			return;
		}
		if (this.pendingRequest) {
			// pending request - reply still outstanding
			throw new SCServiceException(
					"delete session not possible, there is a pending request - two pending request are not allowed.");
		}
		ValidatorUtility.validateInt(1, operationTimeoutSeconds, 3600, SCMPError.HV_WRONG_OPERATION_TIMEOUT);
		if (scMessage != null) {
			ValidatorUtility.validateStringLengthIgnoreNull(1, scMessage.getSessionInfo(), 256, SCMPError.HV_WRONG_SESSION_INFO);
		}
		// cancel session timeout even if its running already
		this.cancelSessionTimeout(true);
		this.requester.getContext().getSCMPMsgSequenceNr().incrementMsgSequenceNr();
		try {
			// 2. initialize call & invoke
			SCServiceCallback callback = new SCServiceCallback(true);
			SCMPClnDeleteSessionCall deleteSessionCall = (SCMPClnDeleteSessionCall) SCMPCallFactory.CLN_DELETE_SESSION_CALL
					.newInstance(this.requester, this.serviceName, this.sessionId);
			if (scMessage != null) {
				deleteSessionCall.setSessionInfo(scMessage.getSessionInfo());
			}
			try {
				deleteSessionCall.invoke(callback, operationTimeoutSeconds * Constants.SEC_TO_MILLISEC_FACTOR);
			} catch (Exception e) {
				throw new SCServiceException("delete session failed ", e);
			}
			// 3. receiving reply and error handling
			SCMPMessage reply = callback.getMessageSync(operationTimeoutSeconds * Constants.SEC_TO_MILLISEC_FACTOR);
			if (reply.isFault()) {
				SCServiceException ex = new SCServiceException("delete session failed");
				ex.setSCMPError(reply.getHeader(SCMPHeaderAttributeKey.SC_ERROR_CODE));
				ex.setSCMPDetailErrorText(reply.getHeader(SCMPHeaderAttributeKey.SC_ERROR_TEXT));
				throw ex;
			}
		} finally {
			// 4. post process, reply to client
			this.sessionId = null;
			this.sessionActive = false;
		}
	}

	synchronized void setRequestComplete() {
		super.setRequestComplete();
		// trigger session timeout
		this.triggerSessionTimeout();
	}

	/**
	 * Trigger session timeout.
	 */
	@SuppressWarnings("unchecked")
	private void triggerSessionTimeout() {
		SCSessionTimeout sessionTimeout = new SCSessionTimeout();
		TimeoutWrapper timeoutWrapper = new TimeoutWrapper(sessionTimeout);
		this.sessionTimeout = (ScheduledFuture<TimeoutWrapper>) AppContext.eciScheduler.schedule(timeoutWrapper,
				(int) (echoIntervalInSeconds * Constants.SEC_TO_MILLISEC_FACTOR), TimeUnit.MILLISECONDS);
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
		AppContext.eciScheduler.purge();
	}

	/**
	 * Sets the echo timeout in seconds. Time in seconds the an echo request waits to be confirmed. If no confirmation is received
	 * session is marked as dead.
	 * 
	 * @param echoTimeoutInSeconds
	 *            the new echo timeout in seconds
	 */
	public void setEchoTimeoutInSeconds(int echoTimeoutInSeconds) throws SCMPValidatorException {
		ValidatorUtility.validateInt(1, echoTimeoutInSeconds, 3600, SCMPError.HV_WRONG_ECHO_TIMEOUT);
		this.echoTimeoutInSeconds = echoTimeoutInSeconds;
	}

	/**
	 * Gets the echo timeout in seconds. Time in seconds the an echo request waits to be confirmed.
	 * 
	 * @return the echo timeout in seconds
	 */
	public int getEchoTimeoutInSeconds() {
		return this.echoTimeoutInSeconds;
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
			return SCSessionService.this.echoIntervalInSeconds * Constants.SEC_TO_MILLISEC_FACTOR;
		}
	}

	/**
	 * Sets the echo interval in seconds.
	 * 
	 * @param echoIntervalInSeconds
	 *            the new echo interval in seconds
	 * @throws SCMPValidatorException
	 *             the sCMP validator exception
	 */
	public void setEchoIntervalInSeconds(int echoIntervalInSeconds) throws SCMPValidatorException {
		ValidatorUtility.validateInt(1, echoIntervalInSeconds, 3600, SCMPError.HV_WRONG_ECHO_INTERVAL);
		this.echoIntervalInSeconds = echoIntervalInSeconds;
	}

	/**
	 * Gets the echo interval in seconds.
	 * 
	 * @return the echo interval in seconds
	 */
	public int getEchoIntervalInSeconds() {
		return this.echoIntervalInSeconds;
	}
}