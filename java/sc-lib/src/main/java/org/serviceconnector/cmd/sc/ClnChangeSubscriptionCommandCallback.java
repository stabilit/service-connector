package org.serviceconnector.cmd.sc;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.log.SubscriptionLogger;
import org.serviceconnector.net.req.IRequest;
import org.serviceconnector.net.req.netty.IdleTimeoutException;
import org.serviceconnector.net.res.IResponderCallback;
import org.serviceconnector.net.res.IResponse;
import org.serviceconnector.registry.PublishMessageQueue;
import org.serviceconnector.registry.SubscriptionRegistry;
import org.serviceconnector.scmp.ISCMPMessageCallback;
import org.serviceconnector.scmp.ISubscriptionCallback;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.scmp.SCMPMessageFault;
import org.serviceconnector.scmp.SCMPMsgType;
import org.serviceconnector.service.IPublishService;
import org.serviceconnector.service.InvalidMaskLengthException;
import org.serviceconnector.service.Subscription;
import org.serviceconnector.service.SubscriptionMask;

/**
 * The Class ClnChangeSubscriptionCommandCallback.
 */
public class ClnChangeSubscriptionCommandCallback implements ISCMPMessageCallback, ISubscriptionCallback {
	/** The Constant logger. */
	private final static Logger logger = Logger.getLogger(ClnChangeSubscriptionCommandCallback.class);
	/** The callback. */
	private IResponderCallback responderCallback;
	/** The request. */
	private IRequest request;
	/** The response. */
	private IResponse response;
	/** The subscription. */
	private Subscription subscription;

	private SubscriptionRegistry subscriptionRegistry = AppContext.getSubscriptionRegistry();

	/**
	 * Instantiates a new cln change subscription command callback.
	 * 
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @param responderCallback
	 *            the responder callback
	 * @param subscription
	 *            the subscription
	 */
	public ClnChangeSubscriptionCommandCallback(IRequest request, IResponse response, IResponderCallback responderCallback,
			Subscription subscription) {
		this.responderCallback = responderCallback;
		this.request = request;
		this.response = response;
		this.subscription = subscription;
	}

	/** {@inheritDoc} */
	@Override
	public void receive(SCMPMessage reply) {

		SCMPMessage reqMessage = request.getMessage();
		String serviceName = reqMessage.getServiceName();
		String subscriptionId = subscription.getId();
		if (reply.isFault() == false) {
			boolean rejectSubscriptionFlag = reply.getHeaderFlag(SCMPHeaderAttributeKey.REJECT_SESSION);
			if (rejectSubscriptionFlag == false) {
				// session has not been rejected
				String newMask = reqMessage.getHeader(SCMPHeaderAttributeKey.MASK);
				PublishMessageQueue<SCMPMessage> queue = ((IPublishService) subscription.getService()).getMessageQueue();
				SubscriptionMask mask = new SubscriptionMask(newMask);
				SubscriptionLogger.logChangeSubscribe(serviceName, subscriptionId, newMask);
				queue.changeSubscription(subscriptionId, mask);
				subscription.setMask(mask);
			}
		}
		// forward reply to client
		reply.setIsReply(true);
		reply.setServiceName(serviceName);
		reply.setMessageType(SCMPMsgType.CLN_CHANGE_SUBSCRIPTION);
		reply.setSessionId(subscriptionId);
		response.setSCMP(reply);
		this.responderCallback.responseCallback(request, response);
	}

	/** {@inheritDoc} */
	@Override
	public void receive(Exception ex) {
		logger.warn(ex);
		SCMPMessage fault = null;
		SCMPMessage reqMessage = request.getMessage();
		String serviceName = reqMessage.getServiceName();
		String subscriptionId = subscription.getId();
		if (ex instanceof IdleTimeoutException) {
			// operation timeout handling
			fault = new SCMPMessageFault(SCMPError.OPERATION_TIMEOUT, "Operation timeout expired on SC cln change subscription");
		} else if (ex instanceof IOException) {
			fault = new SCMPMessageFault(SCMPError.CONNECTION_EXCEPTION, "broken connection on SC cln change subscription");
		} else if (ex instanceof InvalidMaskLengthException) {
			fault = new SCMPMessageFault(SCMPError.HV_WRONG_MASK, ex.getMessage());
		} else {
			fault = new SCMPMessageFault(SCMPError.SC_ERROR, "executing cln change subscription failed");
		}
		fault.setIsReply(true);
		fault.setServiceName(serviceName);
		fault.setMessageType(SCMPMsgType.CLN_CHANGE_SUBSCRIPTION);
		fault.setSessionId(subscriptionId);
		response.setSCMP(fault);
		// schedule subscription timeout
		Subscription subscription = this.subscriptionRegistry.getSubscription(subscriptionId);
		this.subscriptionRegistry.scheduleSubscriptionTimeout(subscription);
		this.responderCallback.responseCallback(request, response);
	}

	/** {@inheritDoc} */
	@Override
	public IRequest getRequest() {
		return this.request;
	}

	/** {@inheritDoc} */
	@Override
	public Subscription getSubscription() {
		return this.subscription;
	}
}