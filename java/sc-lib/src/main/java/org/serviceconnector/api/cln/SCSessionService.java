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
import java.util.TimerTask;

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
import org.serviceconnector.net.req.RequesterContext;
import org.serviceconnector.net.req.SCRequester;
import org.serviceconnector.net.req.netty.IdleTimeoutException;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.scmp.SCMPFault;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPLargeResponse;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.service.SCServiceException;
import org.serviceconnector.util.ITimerRun;
import org.serviceconnector.util.TimerTaskWrapper;
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
	/** The timer run, runs when session need to be refreshed on SC. */
	private ITimerRun timerRun;
	/** The timer task. */
	private TimerTask timerTask;
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
	public SCSessionService(String serviceName, SCContext context) {
		super(serviceName, context);
		this.requester = new SCRequester(new RequesterContext(context.getConnectionPool(), this.msgSequenceNr));
		this.scServiceContext = new SCServiceContext(this);
		this.timerRun = null;
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
		if (this.sessionActive) {
			throw new SCServiceException("session already created - delete session first.");
		}
		ValidatorUtility.validateInt(1, operationTimeoutSeconds, 3600, SCMPError.HV_WRONG_OPERATION_TIMEOUT);

		this.msgSequenceNr.reset();
		SCServiceCallback callback = new SCServiceCallback(true);
		SCMPClnCreateSessionCall createSessionCall = (SCMPClnCreateSessionCall) SCMPCallFactory.CLN_CREATE_SESSION_CALL.newInstance(
				this.requester, this.serviceName);
		if (scMessage != null) {
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
		if (reply.isFault() || reply.getHeaderFlag(SCMPHeaderAttributeKey.REJECT_SESSION)) {
			SCServiceException ex = new SCServiceException("create session failed"
					+ reply.getHeader(SCMPHeaderAttributeKey.SC_ERROR_TEXT));
			ex.setAppErrorCode(reply.getHeader(SCMPHeaderAttributeKey.APP_ERROR_CODE));
			ex.setAppErrorText(reply.getHeader(SCMPHeaderAttributeKey.APP_ERROR_TEXT));
			throw ex;
		}
		this.sessionId = reply.getSessionId();
		this.sessionActive = true;
		// trigger session timeout
		this.timerRun = new SessionTimeouter((int) echoIntervalInSeconds);
		this.timerTask = new TimerTaskWrapper(this.timerRun);
		AppContext.eciTimer.schedule(timerTask, (int) (echoIntervalInSeconds * Constants.SEC_TO_MILLISEC_FACTOR));
		SCMessage replyToClient = new SCMessage();
		replyToClient.setData(reply.getBody());
		replyToClient.setCompressed(reply.getHeaderFlag(SCMPHeaderAttributeKey.COMPRESSION));
		replyToClient.setSessionId(this.sessionId);
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
		if (this.sessionActive == false) {
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
		this.timerTask.cancel();
		SCServiceCallback callback = new SCServiceCallback(true);
		try {
			this.msgSequenceNr.incrementMsgSequenceNr();
			SCMPClnDeleteSessionCall deleteSessionCall = (SCMPClnDeleteSessionCall) SCMPCallFactory.CLN_DELETE_SESSION_CALL
					.newInstance(this.requester, this.serviceName, this.sessionId);
			if (scMessage != null) {
				deleteSessionCall.setSessionInfo(scMessage.getSessionInfo());
			}
			try {
				deleteSessionCall.invoke(callback, operationTimeoutSeconds * Constants.SEC_TO_MILLISEC_FACTOR);
			} catch (Exception e) {
				if (this.sessionActive == false) {
					// ignore errors in state of dead session
					return;
				}
				throw new SCServiceException("delete session failed ", e);
			}
			SCMPMessage reply = callback.getMessageSync(operationTimeoutSeconds * Constants.SEC_TO_MILLISEC_FACTOR);
			if (reply.isFault()) {
				if (this.sessionActive == false) {
					// ignore errors in state of dead session
					return;
				}
				throw new SCServiceException("delete session failed " + reply.getHeader(SCMPHeaderAttributeKey.SC_ERROR_TEXT));
			}
		} finally {
			this.pendingRequest = false;
			this.sessionId = null;
			this.sessionActive = false;
		}
	}

	public synchronized SCMessage execute(SCMessage requestMsg) throws Exception {
		return this.execute(Constants.DEFAULT_OPERATION_TIMEOUT_SECONDS, requestMsg);
	}

	public synchronized SCMessage execute(int timeoutInSeconds, SCMessage requestMsg) throws Exception {
		if (this.sessionActive == false) {
			throw new SCServiceException("execute not possible, no active session.");
		}
		if (requestMsg == null) {
			throw new InvalidParameterException("Message must be set.");
		}
		if (this.pendingRequest) {
			// pending Request - reply still outstanding
			throw new SCServiceException("execute not possible, there is a pending request - two pending request are not allowed.");
		}
		ValidatorUtility.validateInt(1, timeoutInSeconds, 3600, SCMPError.HV_WRONG_OPERATION_TIMEOUT);
		this.pendingRequest = true;
		// cancel session timeout
		this.timerTask.cancel();
		this.msgSequenceNr.incrementMsgSequenceNr();
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
			clnExecuteCall.invoke(callback, timeoutInSeconds * Constants.SEC_TO_MILLISEC_FACTOR);
		} catch (Exception e) {
			this.pendingRequest = false;
			throw new SCServiceException("execute reuest failed ", e);
		}
		// wait for message in callback
		SCMPMessage reply = callback.getMessageSync(timeoutInSeconds * Constants.SEC_TO_MILLISEC_FACTOR);
		this.pendingRequest = false;
		if (reply.isFault()) {
			SCMPFault fault = null;
			if (reply instanceof SCMPFault) {
				fault = (SCMPFault) reply;
			} else {
				if (reply instanceof SCMPLargeResponse) {
					fault = ((SCMPLargeResponse) reply).getFault();
				}
			}
			String errorCode = fault.getHeader(SCMPHeaderAttributeKey.SC_ERROR_CODE);
			if (errorCode != null && errorCode.equals(SCMPError.PROXY_TIMEOUT.getErrorCode())) {
				// OTI run out on SC - mark session as dead!
				this.sessionActive = false;
			}
			Exception ex = fault.getCause();
			if (ex != null && ex instanceof IdleTimeoutException) {
				// OTI run out on client - mark session as dead!
				this.sessionActive = false;
			}
			throw new SCServiceException("execute failed " + reply.getHeader(SCMPHeaderAttributeKey.SC_ERROR_TEXT));
		}
		// trigger session timeout
		this.timerTask = new TimerTaskWrapper(this.timerRun);
		AppContext.eciTimer.schedule(this.timerTask, (long) this.timerRun.getTimeoutMillis());
		SCMessage replyToClient = new SCMessage();
		replyToClient.setData(reply.getBody());
		replyToClient.setCompressed(reply.getHeaderFlag(SCMPHeaderAttributeKey.COMPRESSION));
		replyToClient.setSessionId(this.sessionId);
		replyToClient.setMessageInfo(reply.getHeader(SCMPHeaderAttributeKey.MSG_INFO));
		return replyToClient;
	}

	public synchronized void send(SCMessage requestMsg, SCMessageCallback callback) throws Exception {
		this.send(Constants.DEFAULT_OPERATION_TIMEOUT_SECONDS, requestMsg, callback);
	}

	public synchronized void send(int timeoutInSeconds, SCMessage requestMsg, SCMessageCallback callback) throws Exception {
		if (this.sessionActive == false) {
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
		ValidatorUtility.validateInt(1, timeoutInSeconds, 3600, SCMPError.HV_WRONG_OPERATION_TIMEOUT);
		this.pendingRequest = true;
		// cancel session timeout
		this.timerTask.cancel();
		this.msgSequenceNr.incrementMsgSequenceNr();
		SCMPClnExecuteCall clnExecuteCall = (SCMPClnExecuteCall) SCMPCallFactory.CLN_EXECUTE_CALL.newInstance(this.requester,
				this.serviceName, this.sessionId);
		String msgInfo = requestMsg.getMessageInfo();
		if (msgInfo != null) {
			// message info optional
			clnExecuteCall.setMessagInfo(msgInfo);
		}
		clnExecuteCall.setCompressed(requestMsg.isCompressed());
		clnExecuteCall.setRequestBody(requestMsg.getData());
		SCServiceCallback scmpCallback = new SCServiceCallback(this, callback);
		try {
			clnExecuteCall.invoke(scmpCallback, timeoutInSeconds * Constants.SEC_TO_MILLISEC_FACTOR);
		} catch (Exception e) {
			this.pendingRequest = false;
			throw new SCServiceException("execute request failed ", e);
		}
	}

	public void setRequestComplete() {
		super.setRequestComplete();
		// trigger session timeout
		this.timerTask = new TimerTaskWrapper(this.timerRun);
		AppContext.eciTimer.schedule(this.timerTask, (long) this.timerRun.getTimeoutMillis());
	}

	public void setEchoTimeoutInSeconds(int echoTimeoutInSeconds) {
		this.echoTimeoutInSeconds = echoTimeoutInSeconds;
	}

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
		this.msgSequenceNr.incrementMsgSequenceNr();
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
		SCMPMessage reply = callback.getMessageSync();
		this.pendingRequest = false;
		if (reply.isFault()) {
			throw new SCServiceException("echo failed" + reply.getHeader(SCMPHeaderAttributeKey.SC_ERROR_TEXT));
		}
	}

	/**
	 * The Class SessionTimeouter. Get control at the time a session refresh is needed. Takes care of sending an echo to SC which
	 * gets the session refreshed.
	 */
	private class SessionTimeouter implements ITimerRun {

		/** The timeout in seconds. */
		private int timeoutInSeconds;

		/**
		 * Instantiates a new session timeouter.
		 * 
		 * @param timeoutInSeconds
		 *            the timeout in seconds
		 */
		public SessionTimeouter(int timeoutInSeconds) {
			this.timeoutInSeconds = timeoutInSeconds;
		}

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
				SCSessionService.this.timerTask = new TimerTaskWrapper(SCSessionService.this.timerRun);
				AppContext.eciTimer.schedule(SCSessionService.this.timerTask, (long) this.getTimeoutMillis());
			} catch (Exception e) {
				// echo failed - mark session as dead
				SCSessionService.this.sessionActive = false;
				SCSessionService.this.timerTask.cancel();
			}
		}

		/** {@inheritDoc} */
		@Override
		public int getTimeoutMillis() {
			return this.timeoutInSeconds * Constants.SEC_TO_MILLISEC_FACTOR;
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
}