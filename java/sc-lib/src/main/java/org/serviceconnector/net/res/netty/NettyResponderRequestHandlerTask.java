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
package org.serviceconnector.net.res.netty;

import java.net.InetSocketAddress;

import org.apache.log4j.Logger;
import org.jboss.netty.channel.Channel;
import org.serviceconnector.Constants;
import org.serviceconnector.cmd.ICommand;
import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.log.PerformanceLogger;
import org.serviceconnector.net.req.IRequest;
import org.serviceconnector.net.res.IResponderCallback;
import org.serviceconnector.net.res.IResponse;
import org.serviceconnector.net.res.ResponderRegistry;
import org.serviceconnector.net.res.SCMPSessionCompositeRegistry;
import org.serviceconnector.scmp.HasFaultResponseException;
import org.serviceconnector.scmp.SCMPCompositeReceiver;
import org.serviceconnector.scmp.SCMPCompositeSender;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.scmp.SCMPMessageFault;
import org.serviceconnector.scmp.SCMPMessageSequenceNr;
import org.serviceconnector.scmp.SCMPMsgType;
import org.serviceconnector.scmp.SCMPPart;

/**
 * The Class NettyResponderRequestHandlerTask. Is responsible for processing a request. It has to be a new thread because of NETTY
 * threading concept.
 * A worker thread owns a channel pipeline. If block the thread nothing will be sent on that channel.
 * More information about this issue: http://www.jboss.org/netty/community.html#nabble-td5441049
 */
public class NettyResponderRequestHandlerTask implements IResponderCallback, Runnable {
	/** The Constant LOGGER. */
	private static final Logger LOGGER = Logger.getLogger(NettyResponderRequestHandlerTask.class);
	/** The composite registry. */
	private static SCMPSessionCompositeRegistry compositeRegistry = AppContext.getSCMPSessionCompositeRegistry();

	/** The request. */
	private IRequest request;
	/** The response. */
	private IResponse response;
	/** The channel. */
	private Channel channel;

	/**
	 * Instantiates a new netty responder request handler task.
	 * 
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @param channel
	 *            the channel
	 */
	public NettyResponderRequestHandlerTask(IRequest request, IResponse response, Channel channel) {
		this.request = request;
		this.response = response;
		this.channel = channel;
	}

	/** {@inheritDoc} */
	@Override
	public void run() {
		try {
			// loading message
			request.load();
			SCMPMessage scmpReq = request.getMessage();
			String sessionId = scmpReq.getSessionId();
			SCMPMessageSequenceNr msgSequenceNr = NettyResponderRequestHandlerTask.compositeRegistry
					.getSCMPMsgSequenceNr(sessionId);

			if (scmpReq.isKeepAlive()) {
				scmpReq.setIsReply(true);
				response.setSCMP(scmpReq);
				response.write();
				return;
			}
			if (scmpReq.isFault()) {
				// fault received nothing to to return - delete largeRequest/largeResponse
				this.sendBadRequestError(response, scmpReq);
				NettyResponderRequestHandlerTask.compositeRegistry.removeSCMPLargeResponse(sessionId);
				NettyResponderRequestHandlerTask.compositeRegistry.removeSCMPLargeRequest(sessionId);
				return;
			}

			// needs to set a key in thread local to identify thread later and get access to the responder
			ResponderRegistry responderRegistry = AppContext.getResponderRegistry();
			int port = ((InetSocketAddress) channel.getLocalAddress()).getPort();
			responderRegistry.setThreadLocal(port);

			ICommand command = AppContext.getCommandFactory().getCommand(request.getKey());
			// gets the command
			if (command == null) {
				this.sendBadRequestError(response, scmpReq);
				return;
			}

			if (command.isPassThroughPartMsg() == false) {
				// large messages needs to be handled
				SCMPCompositeSender largeResponse = NettyResponderRequestHandlerTask.compositeRegistry
						.getSCMPLargeResponse(sessionId);

				if (largeResponse != null && scmpReq.isPart()) {
					// sending of a large response has already been started and incoming scmp is a pull request
					if (largeResponse.hasNext()) {
						// there are still parts to send to complete request
						SCMPMessage nextSCMP = largeResponse.getNext();
						// handling msgSequenceNr
						msgSequenceNr.incrementAndGetMsgSequenceNr();
						nextSCMP.setHeader(SCMPHeaderAttributeKey.MESSAGE_SEQUENCE_NR, msgSequenceNr.getCurrentNr());
						response.setSCMP(nextSCMP);
						response.write();
						return;
					}
					NettyResponderRequestHandlerTask.compositeRegistry.removeSCMPLargeResponse(sessionId);
				}
				// gets the large request or creates a new one if necessary
				SCMPCompositeReceiver largeRequest = this.getSCMPLargeRequest(request, response);

				if (largeRequest != null && largeRequest.isComplete() == false) {
					// request is not complete yet
					response.write();
					return;
				}
				// removes largeResponse - request is complete don't need to know preceding messages any more
				NettyResponderRequestHandlerTask.compositeRegistry.removeSCMPLargeRequest(sessionId);
			}
			// validate request and run command
			if (Constants.COMMAND_VALIDATION_ENABLED) {
				command.validate(request);
			}
			PerformanceLogger.beginThreadBound();
			command.run(request, response, this);
			PerformanceLogger.endThreadBound(command.getKey().getValue());
		} catch (HasFaultResponseException ex) {
			// exception carries response inside
			LOGGER.warn("run " + ex.toString());
			ex.setSessionIdAndServiceName(request);
			ex.setFaultResponse(response);
			try {
				response.write();
			} catch (Exception e) {
				LOGGER.error("Sending a response failed.", ex);
			}
		} catch (Exception e2) {
			LOGGER.error("run ", e2);
			SCMPMessageFault scmpFault = new SCMPMessageFault(SCMPError.SERVER_ERROR, e2.getMessage());
			scmpFault.setMessageType(SCMPMsgType.UNDEFINED);
			scmpFault.setLocalDateTime();
			response.setSCMP(scmpFault);
			try {
				response.write();
			} catch (Exception ex) {
				LOGGER.error("Sending a response failed.", ex);
			}
		}
	}

	/**
	 * Gets the SCMP large request.
	 * 
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @return the SCMP large response
	 * @throws Exception
	 *             the exception
	 */
	private SCMPCompositeReceiver getSCMPLargeRequest(IRequest request, IResponse response) throws Exception {
		SCMPMessage scmpReq = request.getMessage();
		String sessionId = scmpReq.getSessionId();
		SCMPCompositeReceiver largeRequest = NettyResponderRequestHandlerTask.compositeRegistry.getSCMPLargeRequest(scmpReq
				.getSessionId());

		if (largeRequest == null) {
			// no compositeReceiver used before
			if (scmpReq.isPart() == false) {
				// request not chunk
				return null;
			}
			// first part of a large request received - create large request
			largeRequest = new SCMPCompositeReceiver(scmpReq, (SCMPMessage) scmpReq);
			int oti = scmpReq.getHeaderInt(SCMPHeaderAttributeKey.OPERATION_TIMEOUT);
			// add largeResponse to the registry
			NettyResponderRequestHandlerTask.compositeRegistry.addSCMPLargeRequest(sessionId, largeRequest, oti);
		} else {
			// next part of a large request received - add to large request
			largeRequest.add(scmpReq);
		}

		SCMPMessageSequenceNr msgSequenceNr = NettyResponderRequestHandlerTask.compositeRegistry.getSCMPMsgSequenceNr(sessionId);

		if (scmpReq.isPart()) {
			// received message part - request not complete yet
			largeRequest.incomplete();
			// set up poll response
			SCMPMessage scmpReply = new SCMPPart(true);
			scmpReply.setHeader(SCMPHeaderAttributeKey.MESSAGE_SEQUENCE_NR, msgSequenceNr.incrementAndGetMsgSequenceNr());
			scmpReply.setIsReply(true);
			scmpReply.setMessageType(scmpReq.getMessageType());
			response.setSCMP(scmpReply);
		} else {
			// last message of a chunk message received - request complete now
			largeRequest.complete();
			largeRequest.setHeader(SCMPHeaderAttributeKey.MESSAGE_SEQUENCE_NR, msgSequenceNr.incrementAndGetMsgSequenceNr());
			request.setMessage(largeRequest);
		}
		return largeRequest;
	}

	/** {@inheritDoc} */
	@Override
	public void responseCallback(IRequest request, IResponse response) {

		SCMPMessage scmpRequest = request.getMessage();
		String sessionId = scmpRequest.getSessionId();
		SCMPMessageSequenceNr msgSequenceNr = NettyResponderRequestHandlerTask.compositeRegistry.getSCMPMsgSequenceNr(sessionId);
		if (response.isLarge() && AppContext.isScEnvironment() == false) {
			// response is large & not on SC, create a large response for reply
			SCMPCompositeSender largeResponse = new SCMPCompositeSender(response.getSCMP());
			SCMPMessage firstSCMP = largeResponse.getFirst();
			response.setSCMP(firstSCMP);
			if (SCMPMessageSequenceNr.necessaryToWrite(firstSCMP.getMessageType())) {
				// no incrementation necessary - already done inside commands
				firstSCMP.setHeader(SCMPHeaderAttributeKey.MESSAGE_SEQUENCE_NR, msgSequenceNr.getCurrentNr());
			}
			int oti = scmpRequest.getHeaderInt(SCMPHeaderAttributeKey.OPERATION_TIMEOUT);
			// adding compositeReceiver to the composite registry
			NettyResponderRequestHandlerTask.compositeRegistry.addSCMPLargeResponse(sessionId, largeResponse, oti);
		}
		try {
			// reply to client
			response.write();
		} catch (Exception ex) {
			LOGGER.error("send response failed", ex);
		}
	}

	/**
	 * Send bad request error.
	 * 
	 * @param response
	 *            the response
	 * @param scmpReq
	 *            the scmp req
	 * @throws Exception
	 *             the exception
	 */
	protected void sendBadRequestError(IResponse response, SCMPMessage scmpReq) throws Exception {
		SCMPMessageFault scmpFault = new SCMPMessageFault(SCMPError.BAD_REQUEST, "messagType=" + scmpReq.getMessageType());
		scmpFault.setMessageType(scmpReq.getMessageType());
		scmpFault.setLocalDateTime();
		response.setSCMP(scmpFault);
		response.write();
	}
}
