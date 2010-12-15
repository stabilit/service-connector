/*-----------------------------------------------------------------------------*
 *                                                                             *
 *       Copyright � 2010 STABILIT Informatik AG, Switzerland                  *
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
package org.serviceconnector.cmd.sc;

import org.apache.log4j.Logger;
import org.serviceconnector.Constants;
import org.serviceconnector.cmd.SCMPCommandException;
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.log.SubscriptionLogger;
import org.serviceconnector.net.connection.ConnectionPoolBusyException;
import org.serviceconnector.registry.SubscriptionQueue;
import org.serviceconnector.registry.SubscriptionRegistry;
import org.serviceconnector.scmp.HasFaultResponseException;
import org.serviceconnector.scmp.IRequest;
import org.serviceconnector.scmp.IResponse;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.scmp.SCMPMessageFault;
import org.serviceconnector.scmp.SCMPMsgType;
import org.serviceconnector.scmp.SCMPPart;
import org.serviceconnector.server.StatefulServer;
import org.serviceconnector.service.IPublishTimerRun;
import org.serviceconnector.service.NoFreeServerException;
import org.serviceconnector.service.PublishService;
import org.serviceconnector.service.Subscription;
import org.serviceconnector.service.SubscriptionMask;
import org.serviceconnector.util.ValidatorUtility;

/**
 * The Class ClnSubscribeCommand. Responsible for validation and execution of subscribe command. Allows subscribing to a publish
 * service.
 * 
 * @author JTraber
 */
public class ClnSubscribeCommand extends CommandAdapter {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(ClnSubscribeCommand.class);

	/**
	 * Instantiates a ClnSubscribeCommand.
	 */
	public ClnSubscribeCommand() {
	}

	/** {@inheritDoc} */
	@Override
	public SCMPMsgType getKey() {
		return SCMPMsgType.CLN_SUBSCRIBE;
	}

	/** {@inheritDoc} */
	@Override
	public void run(IRequest request, IResponse response) throws Exception {
		SCMPMessage reqMessage = request.getMessage();
		String serviceName = reqMessage.getServiceName();
		String mask = reqMessage.getHeader(SCMPHeaderAttributeKey.MASK);
		// check service is present
		PublishService service = this.validatePublishService(serviceName);

		SubscriptionMask subscriptionMask = new SubscriptionMask(mask);
		String ipAddressList = (String) reqMessage.getHeader(SCMPHeaderAttributeKey.IP_ADDRESS_LIST);
		String sessionInfo = (String) reqMessage.getHeader(SCMPHeaderAttributeKey.SESSION_INFO);
		// create subscription
		Subscription subscription = new Subscription(subscriptionMask, sessionInfo, ipAddressList);
		reqMessage.setSessionId(subscription.getId());

		int noDataIntervalSeconds = reqMessage.getHeaderInt(SCMPHeaderAttributeKey.NO_DATA_INTERVAL);
		reqMessage.removeHeader(SCMPHeaderAttributeKey.NO_DATA_INTERVAL);

		StatefulServer server = null;
		CommandCallback callback = null;
		try {
			int oti = reqMessage.getHeaderInt(SCMPHeaderAttributeKey.OPERATION_TIMEOUT);

			int tries = (int) ((oti * basicConf.getOperationTimeoutMultiplier()) / Constants.WAIT_FOR_BUSY_CONNECTION_INTERVAL_MILLIS);
			// Following loop implements the wait mechanism in case of a busy connection pool
			int i = 0;
			int otiOnServerMillis = 0;
			do {
				callback = new CommandCallback(true);
				try {
					otiOnServerMillis = oti - (i * Constants.WAIT_FOR_BUSY_CONNECTION_INTERVAL_MILLIS);
					server = service.allocateServerAndSubscribe(reqMessage, callback, subscription, otiOnServerMillis);
					// no exception has been thrown - get out of wait loop
					break;
				} catch (NoFreeServerException ex) {
					if (i >= (tries - 1)) {
						// only one loop outstanding - don't continue throw current exception
						throw ex;
					}
				} catch (ConnectionPoolBusyException ex) {
					if (i >= (tries - 1)) {
						// only one loop outstanding - don't continue throw current exception
						SCMPCommandException scmpCommandException = new SCMPCommandException(SCMPError.NO_FREE_CONNECTION,
								"no free connection on server for service " + reqMessage.getServiceName());
						scmpCommandException.setMessageType(this.getKey());
						throw scmpCommandException;
					}
				} catch (Exception ex) {
					throw ex;
				}
				// sleep for a while and then try again
				Thread.sleep(Constants.WAIT_FOR_BUSY_CONNECTION_INTERVAL_MILLIS);
			} while (++i < tries);

			SCMPMessage reply = callback.getMessageSync(otiOnServerMillis);

			if (reply.isFault() == false) {
				boolean rejectSubscriptionFlag = reply.getHeaderFlag(SCMPHeaderAttributeKey.REJECT_SESSION);
				if (rejectSubscriptionFlag == false) {
					// subscription has not been rejected, add server to subscription
					subscription.setServer(server);
					// finally add subscription to the registry
					this.subscriptionRegistry.addSubscription(subscription.getId(), subscription);

					SubscriptionQueue<SCMPMessage> subscriptionQueue = service.getSubscriptionQueue();

					IPublishTimerRun timerRun = new PublishTimerRun(subscriptionQueue, noDataIntervalSeconds
							* Constants.SEC_TO_MILLISEC_FACTOR);
					SubscriptionLogger.logSubscribe(serviceName, subscription.getId(), mask);
					subscriptionQueue.subscribe(subscription.getId(), subscriptionMask, timerRun);
				} else {
					// subscription has been rejected - remove subscription id from header
					reply.removeHeader(SCMPHeaderAttributeKey.SESSION_ID);
					// creation failed remove from server
					server.removeSession(subscription);
				}
			} else {
				reply.removeHeader(SCMPHeaderAttributeKey.SESSION_ID);
				// creation failed remove from server
				server.removeSession(subscription);
			}
			// forward reply to client
			reply.setIsReply(true);
			reply.setMessageType(getKey());
			response.setSCMP(reply);
		} catch (Exception e) {
			if (server != null) {
				// creation failed remove from server
				server.removeSession(subscription);
			}
			throw e;
		}
	}

	/** {@inheritDoc} */
	@Override
	public void validate(IRequest request) throws Exception {
		SCMPMessage message = request.getMessage();

		try {
			// msgSequenceNr
			String msgSequenceNr = message.getMessageSequenceNr();
			if (msgSequenceNr == null || msgSequenceNr.equals("")) {
				throw new SCMPValidatorException(SCMPError.HV_WRONG_MESSAGE_SEQUENCE_NR, "msgSequenceNr must be set");
			}
			// serviceName
			String serviceName = message.getServiceName();
			if (serviceName == null || serviceName.equals("")) {
				throw new SCMPValidatorException(SCMPError.HV_WRONG_SERVICE_NAME, "serviceName must be set");
			}
			// mask
			String mask = (String) message.getHeader(SCMPHeaderAttributeKey.MASK);
			ValidatorUtility.validateStringLength(1, mask, 256, SCMPError.HV_WRONG_MASK);
			// operation timeout
			String otiValue = message.getHeader(SCMPHeaderAttributeKey.OPERATION_TIMEOUT.getValue());
			ValidatorUtility.validateInt(10, otiValue, 3600000, SCMPError.HV_WRONG_OPERATION_TIMEOUT);
			// ipAddressList
			String ipAddressList = (String) message.getHeader(SCMPHeaderAttributeKey.IP_ADDRESS_LIST);
			ValidatorUtility.validateIpAddressList(ipAddressList);
			// sessionInfo is optional
			String sessionInfo = (String) message.getHeader(SCMPHeaderAttributeKey.SESSION_INFO);
			if (sessionInfo != null) {
				ValidatorUtility.validateStringLength(1, sessionInfo, 256, SCMPError.HV_WRONG_SESSION_INFO);
			}
			// noDataInterval
			String noDataIntervalValue = message.getHeader(SCMPHeaderAttributeKey.NO_DATA_INTERVAL);
			ValidatorUtility.validateInt(1, noDataIntervalValue, 3600, SCMPError.HV_WRONG_NODATA_INTERVAL);
		} catch (HasFaultResponseException ex) {
			// needs to set message type at this point
			ex.setMessageType(getKey());
			throw ex;
		} catch (Throwable th) {
			logger.error("validation error", th);
			SCMPValidatorException validatorException = new SCMPValidatorException();
			validatorException.setMessageType(getKey());
			throw validatorException;
		}
	}

	/**
	 * The Class PublishTimerRun. PublishTimerRun defines action to get in place when subscription times out.
	 */
	private class PublishTimerRun implements IPublishTimerRun {

		/** The noDataIntervalInMillis. */
		private int noDataIntervalInMillis;
		/** The subscription queue. */
		private SubscriptionQueue<SCMPMessage> subscriptionQueue;
		/** The request. */
		private IRequest request;
		/** The response. */
		private IResponse response;

		/**
		 * Instantiates a new publish timer run.
		 * 
		 * @param subscriptionQueue
		 *            the subscription place
		 * @param noDataIntervalInMillis
		 *            the timeout
		 */
		public PublishTimerRun(SubscriptionQueue<SCMPMessage> subscriptionQueue, int noDataIntervalInMillis) {
			this.request = null;
			this.response = null;
			this.noDataIntervalInMillis = noDataIntervalInMillis;
			this.subscriptionQueue = subscriptionQueue;
		}

		/** {@inheritDoc} */
		@Override
		public int getTimeoutMillis() {
			return this.noDataIntervalInMillis;
		}

		/** {@inheritDoc} */
		@Override
		public void setRequest(IRequest request) {
			this.request = request;
		}

		/** {@inheritDoc} */
		@Override
		public void setResponse(IResponse response) {
			this.response = response;
		}

		/** {@inheritDoc} */
		@Override
		public void timeout() {
			logger.debug("timeout publishTimer");
			String subscriptionId = null;
			try {
				// extracting subscriptionId from request message
				SCMPMessage reqMsg = request.getMessage();
				// set up subscription timeout
				SubscriptionRegistry subscriptionRegistry = AppContext.getSubscriptionRegistry();
				subscriptionId = reqMsg.getSessionId();

				logger.debug("timeout publishTimer datapointer subscriptionId " + subscriptionId);
				Subscription subscription = subscriptionRegistry.getSubscription(subscriptionId);
				if (subscription == null) {
					logger.debug("subscription not found - subscription has already been deleted subscriptionId " + subscriptionId);
					// subscription has already been deleted
					SCMPMessageFault fault = new SCMPMessageFault(SCMPError.NOT_FOUND, "subscription not found");
					response.setSCMP(fault);
				} else {
					// tries polling from queue
					SCMPMessage message = this.subscriptionQueue.getMessage(subscriptionId);
					if (message == null) {
						logger.debug("no message found on queue - subscription timeout set up no data message subscriptionId "
								+ subscriptionId);
						// no message found on queue - subscription timeout set up no data message
						reqMsg.setHeaderFlag(SCMPHeaderAttributeKey.NO_DATA);
						reqMsg.setIsReply(true);
						this.response.setSCMP(reqMsg);
					} else {
						logger.debug("message found on queue - subscription timeout set up reply message subscriptionId "
								+ subscriptionId);
						// set up reply
						SCMPMessage reply = null;
						if (message.isPart()) {
							// message from queue is of type part - outgoing must be part too, no poll request
							reply = new SCMPPart(false);
						} else {
							reply = new SCMPMessage();
						}
						reply.setServiceName(reqMsg.getHeader(SCMPHeaderAttributeKey.SERVICE_NAME));
						reply.setSessionId(subscriptionId);
						reply.setMessageType(reqMsg.getMessageType());
						reply.setIsReply(true);

						// message polling successful
						reply.setBody(message.getBody());
						reply.setHeader(SCMPHeaderAttributeKey.MESSAGE_SEQUENCE_NR, message.getMessageSequenceNr());
						String messageInfo = message.getHeader(SCMPHeaderAttributeKey.MSG_INFO);
						if (messageInfo != null) {
							reply.setHeader(SCMPHeaderAttributeKey.MSG_INFO, messageInfo);
						}
						reply.setHeader(SCMPHeaderAttributeKey.MASK, message.getHeader(SCMPHeaderAttributeKey.MASK));
						reply.setBody(message.getBody());
						this.response.setSCMP(reply);
					}
				}
			} catch (Exception ex) {
				logger.warn("timeout expired procedure failed :" + ex.getMessage());
				SCMPMessageFault scmpFault = new SCMPMessageFault(SCMPError.SERVER_ERROR, ex.getMessage());
				scmpFault.setMessageType(SCMPMsgType.UNDEFINED);
				scmpFault.setLocalDateTime();
				response.setSCMP(scmpFault);
			} finally {
				// send message back to client
				try {
					this.response.write();
				} catch (Exception e) {
					logger.warn("timeout expired procedure failed :" + e.getMessage());
				}
				subscriptionRegistry.scheduleSubscriptionTimeout(subscriptionId);
			}
		}
	}
}