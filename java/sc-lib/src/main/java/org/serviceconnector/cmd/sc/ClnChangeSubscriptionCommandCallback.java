package org.serviceconnector.cmd.sc;

import java.io.IOException;

import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.log.SubscriptionLogger;
import org.serviceconnector.net.req.netty.IdleTimeoutException;
import org.serviceconnector.net.res.IResponderCallback;
import org.serviceconnector.registry.SubscriptionQueue;
import org.serviceconnector.registry.SubscriptionRegistry;
import org.serviceconnector.scmp.IRequest;
import org.serviceconnector.scmp.IResponse;
import org.serviceconnector.scmp.ISCMPMessageCallback;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.scmp.SCMPMessageFault;
import org.serviceconnector.scmp.SCMPMsgType;
import org.serviceconnector.service.PublishService;
import org.serviceconnector.service.Subscription;
import org.serviceconnector.service.SubscriptionMask;

/**
 * The Class ClnChangeSubscriptionCommandCallback.
 */
public class ClnChangeSubscriptionCommandCallback implements ISCMPMessageCallback {

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
				SubscriptionQueue<SCMPMessage> queue = ((PublishService) subscription.getServer().getService())
						.getSubscriptionQueue();
				SubscriptionMask mask = new SubscriptionMask(newMask);
				SubscriptionLogger.logChangeSubscribe(serviceName, subscriptionId, newMask);
				queue.changeSubscription(subscriptionId, mask);
				subscription.setMask(mask);
				String ipAddressList = reqMessage.getHeader(SCMPHeaderAttributeKey.IP_ADDRESS_LIST);
				subscription.setIpAddressList(ipAddressList);
			}
		}
		// forward reply to client
		reply.setIsReply(true);
		reply.setServiceName(serviceName);
		reply.setMessageType(SCMPMsgType.CLN_CHANGE_SUBSCRIPTION);
		reply.setSessionId(subscriptionId);
		response.setSCMP(reply);
		// schedule subscription timeout
		Subscription subscription = this.subscriptionRegistry.getSubscription(subscriptionId);
		this.subscriptionRegistry.scheduleSubscriptionTimeout(subscription);
		this.responderCallback.responseCallback(request, response);
	}

	/** {@inheritDoc} */
	@Override
	public void receive(Exception ex) {
		SCMPMessage fault = null;
		if (ex instanceof IdleTimeoutException) {
			// operation timeout handling
			fault = new SCMPMessageFault(SCMPError.OPERATION_TIMEOUT,
					"Operation timeout expired on SC cln change subscription");
		} else if (ex instanceof IOException) {
			fault = new SCMPMessageFault(SCMPError.CONNECTION_EXCEPTION, "broken connection on SC cln change subscription");
		} else {
			fault = new SCMPMessageFault(SCMPError.SC_ERROR, "executing cln change subscription failed");
		}
		this.receive(fault);
	}
}
