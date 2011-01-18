package org.serviceconnector.cmd.sc;

import java.io.IOException;

import org.serviceconnector.Constants;
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
import org.serviceconnector.server.StatefulServer;
import org.serviceconnector.service.PublishService;
import org.serviceconnector.service.PublishTimeout;
import org.serviceconnector.service.Subscription;
import org.serviceconnector.service.SubscriptionMask;

/**
 * The Class ClnSubscribeCommandCallback.
 */
public class ClnSubscribeCommandCallback implements ISCMPMessageCallback {

	/** The callback. */
	private IResponderCallback responderCallback;
	/** The request. */
	private IRequest request;
	/** The response. */
	private IResponse response;
	/** The subscription. */
	private Subscription subscription;
	/** The server. */
	private StatefulServer server;
	/** The subscription registry. */
	private SubscriptionRegistry subscriptionRegistry = AppContext.getSubscriptionRegistry();

	/**
	 * Instantiates a new ClnExecuteCommandCallback.
	 * 
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @param callback
	 *            the callback
	 * @param subscription
	 *            the subscription
	 */
	public ClnSubscribeCommandCallback(IRequest request, IResponse response, IResponderCallback callback, Subscription subscription) {
		this.responderCallback = callback;
		this.request = request;
		this.response = response;
		this.subscription = subscription;
	}

	/** {@inheritDoc} */
	@Override
	public void receive(SCMPMessage reply) {
		String serviceName = reply.getServiceName();
		int noDataIntervalSeconds = this.subscription.getNoDataInterval();

		if (reply.isFault() == false) {
			boolean rejectSubscriptionFlag = reply.getHeaderFlag(SCMPHeaderAttributeKey.REJECT_SESSION);
			if (rejectSubscriptionFlag == false) {
				// subscription has not been rejected, add server to subscription
				subscription.setServer(server);
				SubscriptionQueue<SCMPMessage> subscriptionQueue = ((PublishService) this.server.getService())
						.getSubscriptionQueue();
				PublishTimeout publishTimeout = new PublishTimeout(subscriptionQueue, noDataIntervalSeconds
						* Constants.SEC_TO_MILLISEC_FACTOR);
				SubscriptionMask subscriptionMask = subscription.getMask();
				subscriptionQueue.subscribe(subscription.getId(), subscriptionMask, publishTimeout);
				// finally add subscription to the registry & schedule subscription timeout internal
				this.subscriptionRegistry.addSubscription(subscription.getId(), subscription);
				SubscriptionLogger.logSubscribe(serviceName, subscription.getId(), subscriptionMask.getValue());
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
		reply.setServiceName(serviceName);
		reply.setMessageType(SCMPMsgType.CLN_SUBSCRIBE);
		response.setSCMP(reply);
		this.responderCallback.responseCallback(request, response);
	}

	/** {@inheritDoc} */
	@Override
	public void receive(Exception ex) {
		SCMPMessage fault = null;
		if (ex instanceof IdleTimeoutException) {
			// operation timeout handling
			fault = new SCMPMessageFault(SCMPError.OPERATION_TIMEOUT, "Operation timeout expired on SC cln subscribe");
		} else if (ex instanceof IOException) {
			fault = new SCMPMessageFault(SCMPError.CONNECTION_EXCEPTION, "broken connection on SC cln subscribe");
		} else {
			fault = new SCMPMessageFault(SCMPError.SC_ERROR, "executing cln subscribe failed");
		}
		this.receive(fault);
	}

	/**
	 * Sets the server.
	 * 
	 * @param server
	 *            the new server
	 */
	public void setServer(StatefulServer server) {
		this.server = server;
	}
}
