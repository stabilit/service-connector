package org.serviceconnector.service;

import org.apache.log4j.Logger;
import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.registry.SubscriptionQueue;
import org.serviceconnector.registry.SubscriptionRegistry;
import org.serviceconnector.scmp.IRequest;
import org.serviceconnector.scmp.IResponse;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.scmp.SCMPMessageFault;
import org.serviceconnector.scmp.SCMPMsgType;
import org.serviceconnector.scmp.SCMPPart;
import org.serviceconnector.util.ITimeout;

/**
 * The Class PublishTimeout. PublishTimeout defines action to get in place when subscription times out.
 */
public class PublishTimeout implements ITimeout {

	/** The Constant logger. */
	private final static Logger logger = Logger.getLogger(PublishTimeout.class);
	/** The subscription registry. */
	private SubscriptionRegistry subscriptionRegistry = AppContext.getSubscriptionRegistry();

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
	public PublishTimeout(SubscriptionQueue<SCMPMessage> subscriptionQueue, int noDataIntervalInMillis) {
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

	/**
	 * Sets the request.
	 * 
	 * @param request
	 *            the new request
	 */
	public void setRequest(IRequest request) {
		this.request = request;
	}

	/**
	 * Sets the response.
	 * 
	 * @param response
	 *            the new response
	 */
	public void setResponse(IResponse response) {
		this.response = response;
	}

	/** {@inheritDoc} */
	@Override
	public void timeout() {
		logger.trace("timeout publishTimer");
		String subscriptionId = null;
		try {
			// extracting subscriptionId from request message
			SCMPMessage reqMsg = request.getMessage();
			// set up subscription timeout
			SubscriptionRegistry subscriptionRegistry = AppContext.getSubscriptionRegistry();
			subscriptionId = reqMsg.getSessionId();

			logger.trace("timeout publishTimer datapointer subscriptionId " + subscriptionId);
			Subscription subscription = subscriptionRegistry.getSubscription(subscriptionId);
			if (subscription == null) {
				logger.trace("subscription not found - already deleted subscriptionId=" + subscriptionId);
				// subscription has already been deleted
				SCMPMessageFault fault = new SCMPMessageFault(SCMPError.SUBSCRIPTION_NOT_FOUND, subscriptionId);
				fault.setMessageType(reqMsg.getMessageType());
				response.setSCMP(fault);
			} else {
				// tries polling from queue
				SCMPMessage message = this.subscriptionQueue.getMessage(subscriptionId);
				if (message == null) {
					logger.trace("no message found on queue - subscription timeout set up no data message subscriptionId="
							+ subscriptionId);
					// no message found on queue - subscription timeout set up no data message
					reqMsg.setHeaderFlag(SCMPHeaderAttributeKey.NO_DATA);
					reqMsg.setIsReply(true);
					this.response.setSCMP(reqMsg);
				} else {
					logger.trace("message found on queue - subscription timeout set up reply message subscriptionId="
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
			subscriptionRegistry.scheduleSubscriptionTimeout(subscriptionId);
			// send message back to client
			try {
				this.response.write();
			} catch (Exception e) {
				logger.warn("timeout expired procedure failed :" + e.getMessage());
			}
		}
	}
}
