package org.serviceconnector.cmd.casc;

import java.io.IOException;

import org.apache.log4j.Logger;
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
import org.serviceconnector.service.InvalidMaskLengthException;
import org.serviceconnector.service.Subscription;

public class CscUnsubscribeCommandCallback implements ISCMPMessageCallback {

	/** The Constant logger. */
	private final static Logger logger = Logger.getLogger(CscUnsubscribeCommandCallback.class);
	/** The callback. */
	private IResponderCallback responderCallback;
	/** The request. */
	private IRequest request;
	/** The response. */
	private IResponse response;
	/** The subscription. */
	private Subscription cascSubscription;

	public CscUnsubscribeCommandCallback(IRequest request, IResponse response, IResponderCallback responderCallback,
			Subscription cascSubscription) {
		this.responderCallback = responderCallback;
		this.request = request;
		this.response = response;
		this.cascSubscription = cascSubscription;
	}

	/** {@inheritDoc} */
	@Override
	public void receive(SCMPMessage reply) {
		SCMPMessage reqMessage = request.getMessage();
		String serviceName = reqMessage.getServiceName();
		if (reqMessage.getHeader(SCMPHeaderAttributeKey.CASCADED_MASK) == null) {
			// free server from subscription if cascaded SC unsubscribes himself
			this.cascSubscription.getServer().removeSession(cascSubscription);
		}
		// forward server reply to client
		reply.setIsReply(true);
		reply.setServiceName(serviceName);
		reply.setMessageType(SCMPMsgType.CSC_UNSUBSCRIBE);
		this.response.setSCMP(reply);
		this.responderCallback.responseCallback(request, response);
	}

	/** {@inheritDoc} */
	@Override
	public void receive(Exception ex) {
		logger.warn(ex);
		SCMPMessage reqMessage = request.getMessage();
		String serviceName = reqMessage.getServiceName();
		if (reqMessage.getHeader(SCMPHeaderAttributeKey.CASCADED_MASK) == null) {
			// free server from subscription if cascaded SC unsubscribes himself
			this.cascSubscription.getServer().removeSession(cascSubscription);
		}
		SCMPMessage fault = null;
		if (ex instanceof IdleTimeoutException) {
			// operation timeout handling
			fault = new SCMPMessageFault(SCMPError.OPERATION_TIMEOUT, "Operation timeout expired on SC csc unsubscribe");
		} else if (ex instanceof IOException) {
			fault = new SCMPMessageFault(SCMPError.CONNECTION_EXCEPTION, "broken connection on SC csc unsubscribe");
		} else if (ex instanceof InvalidMaskLengthException) {
			fault = new SCMPMessageFault(SCMPError.HV_WRONG_MASK, ex.getMessage());
		} else {
			fault = new SCMPMessageFault(SCMPError.SC_ERROR, "executing csc unsubscribe failed");
		}
		// forward server reply to client
		fault.setIsReply(true);
		fault.setServiceName(serviceName);
		fault.setMessageType(SCMPMsgType.CSC_UNSUBSCRIBE);
		this.response.setSCMP(fault);
		this.responderCallback.responseCallback(request, response);
	}
}
