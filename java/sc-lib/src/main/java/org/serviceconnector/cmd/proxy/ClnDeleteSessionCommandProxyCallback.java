package org.serviceconnector.cmd.proxy;

import java.io.IOException;

import org.serviceconnector.net.req.netty.IdleTimeoutException;
import org.serviceconnector.net.res.IResponderCallback;
import org.serviceconnector.scmp.IRequest;
import org.serviceconnector.scmp.IResponse;
import org.serviceconnector.scmp.ISCMPMessageCallback;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.scmp.SCMPMessageFault;
import org.serviceconnector.scmp.SCMPMsgType;

/**
 * The Class ClnDeleteSessionCommandProxyCallback.
 */
public class ClnDeleteSessionCommandProxyCallback implements ISCMPMessageCallback {

	/** The callback. */
	private IResponderCallback responderCallback;
	/** The request. */
	private IRequest request;
	/** The response. */
	private IResponse response;

	/**
	 * Instantiates a new cln delete session command callback.
	 * 
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @param callback
	 *            the callback
	 * @param session
	 *            the session
	 * @param server
	 *            the server
	 */
	public ClnDeleteSessionCommandProxyCallback(IRequest request, IResponse response, IResponderCallback callback) {
		this.responderCallback = callback;
		this.request = request;
		this.response = response;
	}

	/** {@inheritDoc} */
	@Override
	public void receive(SCMPMessage reply) {
		String serviceName = reply.getServiceName();
		// forward server reply to client
		reply.setIsReply(true);
		reply.setServiceName(serviceName);
		reply.setMessageType(SCMPMsgType.CLN_DELETE_SESSION);
		this.response.setSCMP(reply);
		this.responderCallback.responseCallback(request, response);
	}

	/** {@inheritDoc} */
	@Override
	public void receive(Exception ex) {
		SCMPMessage fault = null;
		if (ex instanceof IdleTimeoutException) {
			// operation timeout handling
			fault = new SCMPMessageFault(SCMPError.OPERATION_TIMEOUT, "Operation timeout expired on SC cln delete session");
		} else if (ex instanceof IOException) {
			fault = new SCMPMessageFault(SCMPError.CONNECTION_EXCEPTION, "broken connection on SC cln delete session");
		} else {
			fault = new SCMPMessageFault(SCMPError.SC_ERROR, "executing cln delete session failed");
		}
		this.receive(fault);
	}
}