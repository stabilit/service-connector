package org.serviceconnector.net.res.netty;

import org.apache.log4j.Logger;
import org.jboss.netty.channel.Channel;
import org.serviceconnector.Constants;
import org.serviceconnector.cmd.ICommand;
import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.log.PerformanceLogger;
import org.serviceconnector.net.res.IResponderCallback;
import org.serviceconnector.net.res.ResponderRegistry;
import org.serviceconnector.net.res.SCMPSessionCompositeRegistry;
import org.serviceconnector.scmp.HasFaultResponseException;
import org.serviceconnector.scmp.IRequest;
import org.serviceconnector.scmp.IResponse;
import org.serviceconnector.scmp.SCMPCompositeReceiver;
import org.serviceconnector.scmp.SCMPCompositeSender;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.scmp.SCMPMessageFault;
import org.serviceconnector.scmp.SCMPMessageSequenceNr;
import org.serviceconnector.scmp.SCMPMsgType;
import org.serviceconnector.scmp.SCMPPart;

public class NettyResponderRequestHandlerTask implements IResponderCallback, Runnable {
	/** The Constant logger. */
	private final static Logger logger = Logger.getLogger(NettyResponderRequestHandlerTask.class);
	/** The Constant performanceLogger. */
	private final static PerformanceLogger performanceLogger = PerformanceLogger.getInstance();
	/** The composite registry. */
	private static SCMPSessionCompositeRegistry compositeRegistry = AppContext.getSCMPSessionCompositeRegistry();

	private IRequest request;
	private IResponse response;
	private Channel channel;

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
			SCMPMessageSequenceNr msgSequenceNr = NettyResponderRequestHandlerTask.compositeRegistry.getSCMPMsgSequenceNr(sessionId);

			if (scmpReq == null) {
				// no scmp protocol used - nothing to return
				this.sendUnknownRequestError(response);
				return;
			}
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
			responderRegistry.setThreadLocal(channel.getParent().getId());

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
						response.setSCMP(nextSCMP);
						// handling msgSequenceNr
						if (SCMPMessageSequenceNr.necessaryToWrite(nextSCMP.getMessageType())) {
							msgSequenceNr.incrementMsgSequenceNr();
							nextSCMP.setHeader(SCMPHeaderAttributeKey.MESSAGE_SEQUENCE_NR, msgSequenceNr.getCurrentNr());
						}
						response.write();
						return;
					}
					NettyResponderRequestHandlerTask.compositeRegistry.removeSCMPLargeResponse(sessionId);
				}
				// gets the large request or creates a new one if necessary
				SCMPCompositeReceiver largeRequest = this.getSCMPLargeRequest(request, response);

				if (largeRequest != null && largeRequest.isComplete() == false) {
					// request is not complete yet
					SCMPMessage message = response.getSCMP();
					// handling msgSequenceNr
					if (SCMPMessageSequenceNr.necessaryToWrite(message.getMessageType())) {
						msgSequenceNr.incrementMsgSequenceNr();
						message.setHeader(SCMPHeaderAttributeKey.MESSAGE_SEQUENCE_NR, msgSequenceNr.getCurrentNr());
					}
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
			performanceLogger.begin(this.getClass().getName(), "run");
			command.run(request, response, this);
			performanceLogger.end(this.getClass().getName(), "run");
		} catch (HasFaultResponseException ex) {
			// exception carries response inside
			logger.warn("run " + ex.toString());
			ex.setSessionIdAndServiceName(request);
			ex.setFaultResponse(response);
			try {
				response.write();
			} catch (Exception e) {
				logger.error("Sending a response failed.", ex);
			}
		} catch (Exception e2) {
			logger.warn("run " + e2.toString());
			SCMPMessageFault scmpFault = new SCMPMessageFault(SCMPError.SERVER_ERROR, e2.getMessage());
			scmpFault.setMessageType(SCMPMsgType.UNDEFINED);
			scmpFault.setLocalDateTime();
			response.setSCMP(scmpFault);
			try {
				response.write();
			} catch (Exception ex) {
				logger.error("Sending a response failed.", ex);
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
				return largeRequest;
			}
			// first part of a large request received - create large response
			largeRequest = new SCMPCompositeReceiver(scmpReq, (SCMPMessage) scmpReq);
			// add largeResponse to the registry
			NettyResponderRequestHandlerTask.compositeRegistry.addSCMPLargeRequest(sessionId, largeRequest);
		} else {
			// next part of a large request received - add to large response
			largeRequest.add(scmpReq);
		}

		if (scmpReq.isPart()) {
			// received message part - request not complete yet
			largeRequest.incomplete();
			// set up poll request
			SCMPMessage scmpReply = new SCMPPart(true);
			scmpReply.setIsReply(true);
			scmpReply.setMessageType(scmpReq.getMessageType());
			response.setSCMP(scmpReply);
		} else {
			// last message of a chunk message received - request complete now
			largeRequest.complete();
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
			// adding compositeReceiver to the composite registry
			NettyResponderRequestHandlerTask.compositeRegistry.addSCMPLargeResponse(sessionId, largeResponse);
		}
		try {
			// reply to client
			response.write();
		} catch (Exception ex) {
			logger.error("send response failed", ex);
		}
	}

	/**
	 * Send unknown request error.
	 * 
	 * @param response
	 *            the response
	 * @throws Exception
	 *             the exception
	 */
	protected void sendUnknownRequestError(IResponse response) throws Exception {
		SCMPMessage message = new SCMPMessage();
		message.setMessageType(SCMPMsgType.UNDEFINED);
		this.sendBadRequestError(response, message);
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
