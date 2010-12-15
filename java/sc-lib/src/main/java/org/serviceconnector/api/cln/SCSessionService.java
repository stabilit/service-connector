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
import org.serviceconnector.api.SCMessageCallback;
import org.serviceconnector.api.SCService;
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

	public synchronized SCMessage createSession(SCMessage scMessage) throws Exception {
		return this.createSession(Constants.DEFAULT_OPERATION_TIMEOUT_SECONDS, scMessage);
	}

	/**
	 * Creates the session.
	 * 
	 * @param operationTimeoutSeconds
	 *            the operation timeout seconds
	 * @param scMessage
	 *            the sc message
	 * @return the sC message
	 * @throws Exception
	 *             the exception
	 */
	public synchronized SCMessage createSession(int operationTimeoutSeconds, SCMessage scMessage) throws Exception {
		if (this.subscriptionActive) {
			throw new SCServiceException("session already created - delete session first.");
		}
		ValidatorUtility.validateInt(1, operationTimeoutSeconds, 3600, SCMPError.HV_WRONG_OPERATION_TIMEOUT);

		this.requester.getContext().getSCMPMsgSequenceNr().reset();
		SCServiceCallback callback = new SCServiceCallback(true);
		SCMPClnCreateSessionCall createSessionCall = (SCMPClnCreateSessionCall) SCMPCallFactory.CLN_CREATE_SESSION_CALL.newInstance(
				this.requester, this.serviceName);
		if (scMessage != null) {
			if (scMessage.getDataLength() > Constants.MAX_MESSAGE_SIZE) {
				throw new SCServiceException("message > 60kB not allowed");
			}
			createSessionCall.setRequestBody(scMessage.getData());
			createSessionCall.setCompressed(scMessage.isCompressed());
			createSessionCall.setSessionInfo(scMessage.getSessionInfo());
		}
		createSessionCall.setEchoIntervalSeconds(this.echoIntervalInSeconds);
		try {
			createSessionCall.invoke(callback, operationTimeoutSeconds * Constants.SEC_TO_MILLISEC_FACTOR);
		} catch (Exception e) {
			throw new SCServiceException("create session failed ", e);
		}
		SCMPMessage reply = callback.getMessageSync(operationTimeoutSeconds * Constants.SEC_TO_MILLISEC_FACTOR);
		if (reply.isFault()) {
			SCServiceException ex = new SCServiceException("create session failed");
			ex.setSCMPError(reply.getHeader(SCMPHeaderAttributeKey.SC_ERROR_CODE));
			throw ex;
		}
		if (reply.getHeaderFlag(SCMPHeaderAttributeKey.REJECT_SESSION)) {
			SCServiceException ex = new SCServiceException("create session failed, session rejected");
			if (reply.getHeaderInt(SCMPHeaderAttributeKey.APP_ERROR_CODE) != null) {
				ex.setAppErrorCode(reply.getHeaderInt(SCMPHeaderAttributeKey.APP_ERROR_CODE));
				ex.setAppErrorText(reply.getHeader(SCMPHeaderAttributeKey.APP_ERROR_TEXT));
			}
			throw ex;
		}
		this.sessionId = reply.getSessionId();
		this.subscriptionActive = true;
		this.triggerSessionTimeout();
		SCMessage replyToClient = new SCMessage();
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
		if (this.subscriptionActive == false) {
			// delete session not possible - no session on this service just ignore
			return;
		}
		if (this.pendingRequest) {
			// pending request - reply still outstanding
			throw new SCServiceException("execute not possible, there is a pending request - two pending request are not allowed.");
		}
		ValidatorUtility.validateInt(1, operationTimeoutSeconds, 3600, SCMPError.HV_WRONG_OPERATION_TIMEOUT);
		this.pendingRequest = true;
		// cancel session timeout
		this.sessionTimeout.cancel(false);
		SCServiceCallback callback = new SCServiceCallback(true);
		try {
			this.requester.getContext().getSCMPMsgSequenceNr().incrementMsgSequenceNr();
			SCMPClnDeleteSessionCall deleteSessionCall = (SCMPClnDeleteSessionCall) SCMPCallFactory.CLN_DELETE_SESSION_CALL
					.newInstance(this.requester, this.serviceName, this.sessionId);
			if (scMessage != null) {
				deleteSessionCall.setSessionInfo(scMessage.getSessionInfo());
			}
			try {
				deleteSessionCall.invoke(callback, operationTimeoutSeconds * Constants.SEC_TO_MILLISEC_FACTOR);
			} catch (Exception e) {
				if (this.subscriptionActive == false) {
					// ignore errors in state of dead session
					return;
				}
				throw new SCServiceException("delete session failed ", e);
			}
			SCMPMessage reply = callback.getMessageSync(operationTimeoutSeconds * Constants.SEC_TO_MILLISEC_FACTOR);
			if (reply.isFault()) {
				if (this.subscriptionActive == false) {
					// ignore errors in state of dead session
					return;
				}
				SCServiceException ex = new SCServiceException("delete session failed");
				ex.setSCMPError(reply.getHeader(SCMPHeaderAttributeKey.SC_ERROR_CODE));
				throw ex;
			}
		} finally {
			this.pendingRequest = false;
			this.sessionId = null;
			this.subscriptionActive = false;
		}
	}

	public synchronized SCMessage execute(SCMessage requestMsg) throws Exception {
		return this.execute(Constants.DEFAULT_OPERATION_TIMEOUT_SECONDS, requestMsg);
	}

	@SuppressWarnings("unchecked")
	public synchronized SCMessage execute(int operationTimeoutSeconds, SCMessage requestMsg) throws Exception {
		if (this.subscriptionActive == false) {
			throw new SCServiceException("execute not possible, no active session.");
		}
		if (requestMsg == null) {
			throw new InvalidParameterException("Message must be set.");
		}
		if (this.pendingRequest) {
			// pending Request - reply still outstanding
			throw new SCServiceException("execute not possible, there is a pending request - two pending request are not allowed.");
		}
		ValidatorUtility.validateInt(1, operationTimeoutSeconds, 3600, SCMPError.HV_WRONG_OPERATION_TIMEOUT);
		this.pendingRequest = true;
		// cancel session timeout
		this.sessionTimeout.cancel(false);
		this.requester.getContext().getSCMPMsgSequenceNr().incrementMsgSequenceNr();
		SCMPClnExecuteCall clnExecuteCall = (SCMPClnExecuteCall) SCMPCallFactory.CLN_EXECUTE_CALL.newInstance(this.requester,
				this.serviceName, this.sessionId);
		String msgInfo = requestMsg.getMessageInfo();
		if (msgInfo != null) {
			// message info optional
			clnExecuteCall.setMessagInfo(msgInfo);
		}
		String cacheId = requestMsg.getCacheId();
		if (cacheId != null) {
			clnExecuteCall.setCacheId(cacheId);
		}
		clnExecuteCall.setCompressed(requestMsg.isCompressed());
		clnExecuteCall.setRequestBody(requestMsg.getData());
		// invoke asynchronous
		SCServiceCallback callback = new SCServiceCallback(true);
		try {
			clnExecuteCall.invoke(callback, operationTimeoutSeconds * Constants.SEC_TO_MILLISEC_FACTOR);
		} catch (Exception e) {
			this.pendingRequest = false;
			throw new SCServiceException("execute reuest failed ", e);
		}
		// wait for message in callback
		SCMPMessage reply = callback.getMessageSync(operationTimeoutSeconds * Constants.SEC_TO_MILLISEC_FACTOR);
		this.pendingRequest = false;
		if (reply.isFault()) {
			SCServiceException scEx = new SCServiceException("execute failed");
			scEx.setSCMPError(reply.getHeader(SCMPHeaderAttributeKey.SC_ERROR_CODE));
			throw scEx;
		}
		this.triggerSessionTimeout();
		SCMessage replyToClient = new SCMessage();
		replyToClient.setData(reply.getBody());
		replyToClient.setCompressed(reply.getHeaderFlag(SCMPHeaderAttributeKey.COMPRESSION));
		replyToClient.setSessionId(this.sessionId);
		replyToClient.setCacheId(reply.getCacheId());
		replyToClient.setMessageInfo(reply.getHeader(SCMPHeaderAttributeKey.MSG_INFO));
		if (reply.getHeaderInt(SCMPHeaderAttributeKey.APP_ERROR_CODE) != null) {
			replyToClient.setAppErrorCode(reply.getHeaderInt(SCMPHeaderAttributeKey.APP_ERROR_CODE));
			replyToClient.setAppErrorText(reply.getHeader(SCMPHeaderAttributeKey.APP_ERROR_TEXT));
		}
		return replyToClient;
	}

	public synchronized void send(SCMessage requestMsg, SCMessageCallback callback) throws Exception {
		this.send(Constants.DEFAULT_OPERATION_TIMEOUT_SECONDS, requestMsg, callback);
	}

	public synchronized void send(int operationtTimeoutSeconds, SCMessage requestMsg, SCMessageCallback callback) throws Exception {
		if (this.subscriptionActive == false) {
			throw new SCServiceException("execute not possible, no active session.");
		}
		if (callback == null) {
			throw new InvalidParameterException("Callback must be set.");
		}
		if (requestMsg == null) {
			throw new InvalidParameterException("Message must be set.");
		}
		if (this.pendingRequest) {
			// already executed before - reply still outstanding
			throw new SCServiceException("execute not possible, there is a pending request - two pending request are not allowed.");
		}
		ValidatorUtility.validateInt(1, operationtTimeoutSeconds, 3600, SCMPError.HV_WRONG_OPERATION_TIMEOUT);
		this.pendingRequest = true;
		// cancel session timeout
		this.sessionTimeout.cancel(false);
		this.requester.getContext().getSCMPMsgSequenceNr().incrementMsgSequenceNr();
		SCMPClnExecuteCall clnExecuteCall = (SCMPClnExecuteCall) SCMPCallFactory.CLN_EXECUTE_CALL.newInstance(this.requester,
				this.serviceName, this.sessionId);
		String msgInfo = requestMsg.getMessageInfo();
		if (msgInfo != null) {
			// message info optional
			clnExecuteCall.setMessagInfo(msgInfo);
		}
		String cacheId = requestMsg.getCacheId();
		if (cacheId != null) {
			clnExecuteCall.setCacheId(cacheId);
		}
		clnExecuteCall.setCompressed(requestMsg.isCompressed());
		clnExecuteCall.setRequestBody(requestMsg.getData());
		SCServiceCallback scmpCallback = new SCServiceCallback(this, callback);
		try {
			clnExecuteCall.invoke(scmpCallback, operationtTimeoutSeconds * Constants.SEC_TO_MILLISEC_FACTOR);
		} catch (Exception e) {
			this.pendingRequest = false;
			throw new SCServiceException("execute request failed ", e);
		}
	}

	public void setRequestComplete() {
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
	 * Echo.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	private synchronized void echo() throws Exception {
		if (this.pendingRequest) {
			// an operation is running no echo necessary
			return;
		}
		this.pendingRequest = true;
		this.requester.getContext().getSCMPMsgSequenceNr().incrementMsgSequenceNr();
		SCMPEchoCall clnEchoCall = (SCMPEchoCall) SCMPCallFactory.ECHO_CALL.newInstance(this.requester, this.serviceName,
				this.sessionId);
		SCServiceCallback callback = new SCServiceCallback(true);
		try {
			clnEchoCall.invoke(callback, this.echoTimeoutInSeconds * Constants.SEC_TO_MILLISEC_FACTOR);
		} catch (Exception e) {
			this.pendingRequest = false;
			throw new SCServiceException("echo request failed", e);
		}
		// wait for message in callback
		SCMPMessage reply = callback.getMessageSync(this.echoTimeoutInSeconds * Constants.SEC_TO_MILLISEC_FACTOR);
		this.pendingRequest = false;
		if (reply.isFault()) {
			SCServiceException ex = new SCServiceException("echo failed");
			ex.setSCMPError(reply.getHeader(SCMPHeaderAttributeKey.SC_ERROR_CODE));
			throw ex;
		}
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
			if (SCSessionService.this.pendingRequest) {
				// no echo will be sent in state of pending request
				return;
			}
			try {
				// send echo to SC
				SCSessionService.this.echo();
				// trigger session timeout
				SCSessionService.this.triggerSessionTimeout();
			} catch (Exception e) {
				// echo failed - mark session as dead
				SCSessionService.this.subscriptionActive = false;
				SCSessionService.this.sessionTimeout.cancel(false);
			}
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