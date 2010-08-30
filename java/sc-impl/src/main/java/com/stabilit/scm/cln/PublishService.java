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
package com.stabilit.scm.cln;

import java.security.InvalidParameterException;

import org.apache.log4j.Logger;

import com.stabilit.scm.cln.service.IPublishService;
import com.stabilit.scm.cln.service.Service;
import com.stabilit.scm.common.call.SCMPCallFactory;
import com.stabilit.scm.common.call.SCMPClnChangeSubscriptionCall;
import com.stabilit.scm.common.call.SCMPClnSubscribeCall;
import com.stabilit.scm.common.call.SCMPClnUnsubscribeCall;
import com.stabilit.scm.common.call.SCMPReceivePublicationCall;
import com.stabilit.scm.common.conf.Constants;
import com.stabilit.scm.common.log.IExceptionLogger;
import com.stabilit.scm.common.log.impl.ExceptionLogger;
import com.stabilit.scm.common.net.req.Requester;
import com.stabilit.scm.common.net.req.RequesterContext;
import com.stabilit.scm.common.scmp.SCMPFault;
import com.stabilit.scm.common.scmp.SCMPHeaderAttributeKey;
import com.stabilit.scm.common.scmp.SCMPMessage;
import com.stabilit.scm.common.service.ISCContext;
import com.stabilit.scm.common.service.ISCMessageCallback;
import com.stabilit.scm.common.service.SCServiceException;

/**
 * The Class PublishService. PublishService is a remote interface in client API to a publish service and provides
 * communication functions.
 */
public class PublishService extends Service implements IPublishService {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(PublishService.class);
	
	private boolean subscribed = false;
	private int noDataInterval;

	/**
	 * Instantiates a new publish service.
	 * 
	 * @param serviceName
	 *            the service name
	 * @param context
	 *            the context
	 */
	public PublishService(String serviceName, ISCContext context) {
		super(serviceName, context);
		this.requester = new Requester(new RequesterContext(context.getConnectionPool(), this.msgId));
		this.serviceContext = new ServiceContext(context, this);
		this.noDataInterval = 0;
	}

	/** {@inheritDoc} */
	@Override
	public void changeSubscription(String mask) throws Exception {
		if (mask == null) {
			throw new InvalidParameterException("Mask must be set.");
		}
		if (mask.getBytes().length > 256) {
			throw new InvalidParameterException("Mask too long, over 256 bytes.");
		}
		if (mask.indexOf('%') != -1) {
			throw new InvalidParameterException("Mask contains percent sign, not allowed.");
		}
		if (this.subscribed == false) {
			throw new SCServiceException("changeSubscription not possible - not subscribed");
		}
		this.msgId.incrementMsgSequenceNr();
		SCMPClnChangeSubscriptionCall changeSubscriptionCall = (SCMPClnChangeSubscriptionCall) SCMPCallFactory.CLN_CHANGE_SUBSCRIPTION
				.newInstance(this.requester, this.serviceName, this.sessionId);
		changeSubscriptionCall.setMask(mask);
		changeSubscriptionCall.invoke(this.callback, Constants.DEFAULT_OPERATION_TIMEOUT_SECONDS);
		this.callback.getMessageSync();
	}

	/** {@inheritDoc} */
	@Override
	public void subscribe(String mask, String sessionInfo, int noDataInterval, ISCMessageCallback callback)
			throws Exception {
		this.subscribe(mask, sessionInfo, noDataInterval, null, callback);
	}

	/** {@inheritDoc} */
	@Override
	public void subscribe(String mask, String sessionInfo, int noDataInterval, String authenticationId,
			ISCMessageCallback callback) throws Exception {
		if (mask == null) {
			throw new InvalidParameterException("Mask must be set.");
		}
		if (mask.getBytes().length > 256) {
			throw new InvalidParameterException("Mask too long, over 256 bytes.");
		}
		if (mask.indexOf('%') != -1) {
			throw new InvalidParameterException("Mask contains percent sign, not allowed.");
		}
		if (sessionInfo == null) {
			throw new InvalidParameterException("Session info must be set.");
		}
		if (sessionInfo.getBytes().length > 256) {
			throw new InvalidParameterException("Session info too long, over 256 bytes.");
		}
		if (noDataInterval < 1 || noDataInterval > 3600) {
			throw new InvalidParameterException("No data interval not within limits 1 to 3600.");
		}
		if (callback == null) {
			throw new InvalidParameterException("Callback must be set.");
		}
		if (this.subscribed) {
			throw new SCServiceException("already subscribed");
		}
		this.subscribed = true;
		this.noDataInterval = noDataInterval;
		this.msgId.reset();
		this.callback = new PublishServiceCallback(callback);
		SCMPClnSubscribeCall subscribeCall = (SCMPClnSubscribeCall) SCMPCallFactory.CLN_SUBSCRIBE_CALL.newInstance(
				this.requester, this.serviceName);
		subscribeCall.setMask(mask);
		subscribeCall.setSessionInfo(sessionInfo);
		subscribeCall.setNoDataIntervalSeconds(noDataInterval);
		if (authenticationId != null) {
			subscribeCall.setAuthenticationId(authenticationId);
		}
		try {
			subscribeCall.invoke(this.callback, Constants.DEFAULT_OPERATION_TIMEOUT_SECONDS);
		} catch (Exception e) {
			throw new SCServiceException("subscribe failed", e);
		}
		SCMPMessage reply = this.callback.getMessageSync();
		if (reply.isFault()) {
			SCMPFault fault = (SCMPFault) reply;
			throw new SCServiceException("subscribe failed", fault.getCause());
		}
		this.sessionId = reply.getSessionId();
		this.receivePublication();
	}

	/** {@inheritDoc} */
	@Override
	public boolean isSubscribed() {
		return this.subscribed;
	}

	/**
	 * Sends a receive publication to the SC.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	private void receivePublication() throws Exception {
		SCMPReceivePublicationCall receivePublicationCall = (SCMPReceivePublicationCall) SCMPCallFactory.RECEIVE_PUBLICATION
				.newInstance(this.requester, this.serviceName, this.sessionId);
		this.msgId.incrementMsgSequenceNr();
		receivePublicationCall.invoke(this.callback, Constants.DEFAULT_OPERATION_TIMEOUT_SECONDS + this.noDataInterval);
	}

	/** {@inheritDoc} */
	@Override
	public void unsubscribe() throws Exception {
		if (this.subscribed == false) {
			// unsubscribe not possible - not subscribed on this service just ignore
			return;
		}
		this.subscribed = false;
		this.msgId.incrementMsgSequenceNr();
		SCMPClnUnsubscribeCall unsubscribeCall = (SCMPClnUnsubscribeCall) SCMPCallFactory.CLN_UNSUBSCRIBE_CALL
				.newInstance(this.requester, this.serviceName, this.sessionId);
		try {
			unsubscribeCall.invoke(this.callback, Constants.DEFAULT_OPERATION_TIMEOUT_SECONDS);
		} catch (Exception e) {
			throw new SCServiceException("subscribe failed", e);
		}
		SCMPMessage reply = this.callback.getMessageSync();
		if (reply.isFault()) {
			SCMPFault fault = (SCMPFault) reply;
			throw new SCServiceException("subscribe failed", fault.getCause());
		}
		this.msgId = null;
		this.callback = null;
	}

	/**
	 * The Class PublishServiceCallback. Responsible for handling the right communication sequence for publish subscribe
	 * protocol.
	 */
	private class PublishServiceCallback extends ServiceCallback {

		/**
		 * Instantiates a new publish service callback.
		 * 
		 * @param messageCallback
		 *            the message callback
		 */
		public PublishServiceCallback(ISCMessageCallback messageCallback) {
			super(PublishService.this, messageCallback);
		}

		/** {@inheritDoc} */
		@Override
		public void callback(SCMPMessage reply) {
			if (this.synchronous) {
				// interested thread waits for message
				super.callback(reply);
				return;
			}
			if (PublishService.this.subscribed == false) {
				// client is not subscribed anymore - stop continuing
				return;
			}
			if (reply.isFault()) {
				// operation failed
				SCMPFault fault = (SCMPFault) reply;
				super.callback(fault.getCause());
				return;
			}
			// check if reply is real answer
			boolean noData = reply.getHeaderFlag(SCMPHeaderAttributeKey.NO_DATA);
			if (noData == false) {
				// data reply received - give to application
				super.callback(reply);
			}
			if (PublishService.this.subscribed) {
				// client is still subscribed - CRP again
				try {
					PublishService.this.receivePublication();
				} catch (Exception e) {
					IExceptionLogger exceptionLogger = ExceptionLogger.getInstance();
					exceptionLogger.logDebugException(logger, this.getClass().getName(), "callback", e);
					SCMPFault fault = new SCMPFault(e);
					super.callback(fault);
					return;
				}
			}
		}
	}
}
