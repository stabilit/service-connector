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
import org.serviceconnector.api.SCService;
import org.serviceconnector.call.SCMPCallFactory;
import org.serviceconnector.call.SCMPClnChangeSubscriptionCall;
import org.serviceconnector.call.SCMPClnSubscribeCall;
import org.serviceconnector.call.SCMPClnUnsubscribeCall;
import org.serviceconnector.call.SCMPReceivePublicationCall;
import org.serviceconnector.net.req.SCRequester;
import org.serviceconnector.net.req.RequesterContext;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.scmp.SCMPFault;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.service.ISCContext;
import org.serviceconnector.service.ISCMessageCallback;
import org.serviceconnector.service.SCServiceException;
import org.serviceconnector.util.ValidatorUtility;


/**
 * The Class PublishService. PublishService is a remote interface in client API to a publish service and provides
 * communication functions.
 */
public class SCPublishService extends SCService implements IPublishService {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(SCPublishService.class);

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
	public SCPublishService(String serviceName, ISCContext context) {
		super(serviceName, context);
		this.requester = new SCRequester(new RequesterContext(context.getConnectionPool(), this.msgId));
		this.serviceContext = new ServiceContext(context, this);
		this.noDataInterval = 0;
	}

	/** {@inheritDoc} */
	@Override
	public synchronized void changeSubscription(String mask) throws Exception {
		this.changeSubscription(mask, Constants.DEFAULT_OPERATION_TIMEOUT_SECONDS);
	}

	@Override
	public synchronized void changeSubscription(String mask, int timeoutInSeconds) throws Exception {
		if (this.subscribed == false) {
			throw new SCServiceException("changeSubscription not possible - not subscribed");
		}
		ValidatorUtility.validateStringLength(1, mask, 256, SCMPError.HV_WRONG_MASK);
		if (mask.indexOf('%') != -1) {
			throw new InvalidParameterException("Mask contains percent sign, not allowed.");
		}
		ValidatorUtility.validateInt(1, timeoutInSeconds, 3600, SCMPError.HV_WRONG_OPERATION_TIMEOUT);
		this.msgId.incrementMsgSequenceNr();
		SCMPClnChangeSubscriptionCall changeSubscriptionCall = (SCMPClnChangeSubscriptionCall) SCMPCallFactory.CLN_CHANGE_SUBSCRIPTION
				.newInstance(this.requester, this.serviceName, this.sessionId);
		changeSubscriptionCall.setMask(mask);
		changeSubscriptionCall.invoke(this.callback, timeoutInSeconds * Constants.SEC_TO_MILLISEC_FACTOR);
		this.callback.getMessageSync();
	}

	/** {@inheritDoc} */
	@Override
	public synchronized void subscribe(String mask, String sessionInfo, int noDataInterval, ISCMessageCallback callback)
			throws Exception {
		this.subscribe(mask, sessionInfo, noDataInterval, callback, Constants.DEFAULT_OPERATION_TIMEOUT_SECONDS);
	}

	@Override
	public synchronized void subscribe(String mask, String sessionInfo, int noDataInterval,
			ISCMessageCallback callback, int timeoutInSeconds) throws Exception {
		this.subscribe(mask, sessionInfo, noDataInterval, null, callback, timeoutInSeconds);
	}

	/** {@inheritDoc} */
	@Override
	public synchronized void subscribe(String mask, String sessionInfo, int noDataInterval, String authenticationId,
			ISCMessageCallback callback) throws Exception {
		this.subscribe(mask, sessionInfo, noDataInterval, authenticationId, callback,
				Constants.DEFAULT_OPERATION_TIMEOUT_SECONDS);
	}

	@Override
	public synchronized void subscribe(String mask, String sessionInfo, int noDataInterval, String authenticationId,
			ISCMessageCallback callback, int timeoutInSeconds) throws Exception {
		if (this.subscribed) {
			throw new SCServiceException("already subscribed");
		}
		ValidatorUtility.validateStringLength(1, mask, 256, SCMPError.HV_WRONG_MASK);
		if (mask.indexOf('%') != -1) {
			throw new InvalidParameterException("Mask contains percent sign, not allowed.");
		}
		ValidatorUtility.validateStringLength(1, sessionInfo, 256, SCMPError.HV_WRONG_SESSION_INFO);
		ValidatorUtility.validateInt(1, noDataInterval, 3600, SCMPError.HV_WRONG_NODATA_INTERVAL);
		if (callback == null) {
			throw new InvalidParameterException("Callback must be set.");
		}
		ValidatorUtility.validateInt(1, timeoutInSeconds, 3600, SCMPError.HV_WRONG_OPERATION_TIMEOUT);
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
			subscribeCall.invoke(this.callback, timeoutInSeconds * Constants.SEC_TO_MILLISEC_FACTOR);
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
	private synchronized void receivePublication() throws Exception {
		SCMPReceivePublicationCall receivePublicationCall = (SCMPReceivePublicationCall) SCMPCallFactory.RECEIVE_PUBLICATION
				.newInstance(this.requester, this.serviceName, this.sessionId);
		this.msgId.incrementMsgSequenceNr();
		receivePublicationCall.invoke(this.callback, Constants.SEC_TO_MILLISEC_FACTOR
				* Constants.DEFAULT_OPERATION_TIMEOUT_SECONDS + this.noDataInterval);
	}

	/** {@inheritDoc} */
	@Override
	public synchronized void unsubscribe() throws Exception {
		this.unsubscribe(Constants.DEFAULT_OPERATION_TIMEOUT_SECONDS);
	}

	/** {@inheritDoc} */
	@Override
	public synchronized void unsubscribe(int timeoutInSeconds) throws Exception {
		if (this.subscribed == false) {
			// unsubscribe not possible - not subscribed on this service just ignore
			return;
		}
		ValidatorUtility.validateInt(1, timeoutInSeconds, 3600, SCMPError.HV_WRONG_OPERATION_TIMEOUT);
		try {
			this.subscribed = false;
			this.msgId.incrementMsgSequenceNr();
			SCMPClnUnsubscribeCall unsubscribeCall = (SCMPClnUnsubscribeCall) SCMPCallFactory.CLN_UNSUBSCRIBE_CALL
					.newInstance(this.requester, this.serviceName, this.sessionId);
			try {
				unsubscribeCall.invoke(this.callback, timeoutInSeconds * Constants.SEC_TO_MILLISEC_FACTOR);
			} catch (Exception e) {
				throw new SCServiceException("subscribe failed", e);
			}
			SCMPMessage reply = this.callback.getMessageSync();
			if (reply.isFault()) {
				SCMPFault fault = (SCMPFault) reply;
				throw new SCServiceException("subscribe failed", fault.getCause());
			}
		} finally {
			this.sessionId = null;
			this.msgId = null;
			this.callback = null;
		}
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
			super(SCPublishService.this, messageCallback);
		}

		/** {@inheritDoc} */
		@Override
		public void callback(SCMPMessage reply) {
			if (this.synchronous) {
				// interested thread waits for message
				super.callback(reply);
				return;
			}
			if (SCPublishService.this.subscribed == false) {
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
			if (SCPublishService.this.subscribed) {
				// client is still subscribed - CRP again
				try {
					SCPublishService.this.receivePublication();
				} catch (Exception e) {
					logger.info("callback " + e.getMessage());
					SCMPFault fault = new SCMPFault(e);
					super.callback(fault);
					return;
				}
			}
		}
	}
}