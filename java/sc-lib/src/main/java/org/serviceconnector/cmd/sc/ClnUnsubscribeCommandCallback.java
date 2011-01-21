package org.serviceconnector.cmd.sc;

import java.io.IOException;

import org.serviceconnector.net.req.netty.IdleTimeoutException;
import org.serviceconnector.net.res.IResponderCallback;
import org.serviceconnector.scmp.IRequest;
import org.serviceconnector.scmp.IResponse;
import org.serviceconnector.scmp.ISCMPMessageCallback;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.scmp.SCMPMessageFault;
import org.serviceconnector.scmp.SCMPMsgType;
import org.serviceconnector.server.StatefulServer;
import org.serviceconnector.service.Subscription;

/**
 * The Class ClnUnsubscribeCommandCallback.
 */
public class ClnUnsubscribeCommandCallback implements ISCMPMessageCallback {

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

	public ClnUnsubscribeCommandCallback(IRequest request, IResponse response, IResponderCallback responderCallback,
			Subscription subscription, StatefulServer server) {
		this.responderCallback = responderCallback;
		this.request = request;
		this.response = response;
		this.subscription = subscription;
		this.server = server;
	}

	/** {@inheritDoc} */
	@Override
	public void receive(SCMPMessage reply) {
		// free server from subscription
		server.removeSession(subscription);
		String serviceName = reply.getServiceName();
		// forward server reply to client
		reply.removeHeader(SCMPHeaderAttributeKey.SESSION_ID);
		reply.setIsReply(true);
		reply.setServiceName(serviceName);
		reply.setMessageType(SCMPMsgType.CLN_UNSUBSCRIBE);
		this.response.setSCMP(reply);
		this.responderCallback.responseCallback(request, response);
		if (reply.isFault()) {
			// delete subscription failed
			server.abortSession(subscription, "unsubscribe failed");
		}
	}

	/** {@inheritDoc} */
	@Override
	public void receive(Exception ex) {
		SCMPMessage fault = null;
		if (ex instanceof IdleTimeoutException) {
			// operation timeout handling
			fault = new SCMPMessageFault(SCMPError.OPERATION_TIMEOUT, "Operation timeout expired on SC cln unsubscribe");
		} else if (ex instanceof IOException) {
			fault = new SCMPMessageFault(SCMPError.CONNECTION_EXCEPTION, "broken connection on SC cln unsubscribe");
		} else {
			fault = new SCMPMessageFault(SCMPError.SC_ERROR, "executing cln unsubscribe failed");
		}
		this.receive(fault);
	}
}
