package org.serviceconnector.web;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.serviceconnector.Constants;
import org.serviceconnector.api.SCServiceException;
import org.serviceconnector.call.SCMPDeRegisterServerCall;
import org.serviceconnector.call.SCMPRegisterServerCall;
import org.serviceconnector.conf.RemoteNodeConfiguration;
import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.log.ConnectionLogger;
import org.serviceconnector.net.ConnectionType;
import org.serviceconnector.net.IEncoderDecoder;
import org.serviceconnector.net.req.SCRequester;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.scmp.SCMPMessageFault;
import org.serviceconnector.scmp.SCMPMsgType;
import org.serviceconnector.util.DateTimeUtility;
import org.serviceconnector.util.Statistics;
import org.serviceconnector.util.SynchronousCallback;

/**
 * Servlet implementation class SCBaseServlet. Base servlet for SC servlet implementation.
 */
public abstract class SCBaseServlet extends HttpServlet {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = Logger.getLogger(SCBaseServlet.class);
	private static final long serialVersionUID = 1L;
	protected boolean registered;
	protected SCRequester requester;
	protected String serviceName;
	private String urlPath;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	protected SCBaseServlet(String urlPath) {
		this.registered = false;
		this.requester = null;
		this.serviceName = null;
		this.urlPath = urlPath;
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		try {
			if (this.registered == false) {
				this.registerServletOnSC(config);
				this.registered = true;
			}
		} catch (Exception e) {
			LOGGER.error("Registering tomcat on SC failed", e);
			throw new ServletException("Registering tomcat on SC failed", e);
		}
	}

	private void registerServletOnSC(ServletConfig config) throws SCServiceException {
		this.serviceName = config.getInitParameter(WebConstants.PROPERTY_SERVICE_NAME);
		int maxConnections = Integer.parseInt(config.getInitParameter(WebConstants.PROPERTY_MAX_CONNECTIONS));
		int maxSessions = Integer.parseInt(config.getInitParameter(WebConstants.PROPERTY_MAX_SESSIONS));
		int keepAliveFromSC = Integer.parseInt(config.getInitParameter(WebConstants.PROPERTY_KEEPALIVE_FROMSC));

		// prepare requester to register servlet on SC
		ServletContext context = config.getServletContext();
		int tomcatPort = Integer.parseInt(context.getInitParameter(WebConstants.PROPERTY_TOMCAT_PORT));
		String remotNodeName = tomcatPort + this.getServletName();
		String scHost = context.getInitParameter(WebConstants.PROPERTY_SC_HOST);
		int scPort = Integer.parseInt(context.getInitParameter(WebConstants.PROPERTY_SC_PORT));
		int keepAliveToSC = Integer.parseInt(context.getInitParameter(WebConstants.PROPERTY_KEEPALIVE_TOSC));
		this.requester = new SCRequester(new RemoteNodeConfiguration(remotNodeName, scHost, scPort,
				ConnectionType.NETTY_HTTP.getValue(), keepAliveToSC, 1));

		// get lock on scServer - only one server is allowed to communicate over the initial connection
		synchronized (AppContext.communicatorsLock) {
			// get communicator lock - avoids interference with other clients or scServers
			AppContext.init();
			this.requester.getSCMPMsgSequenceNr().reset();

			SCMPRegisterServerCall registerServerCall = new SCMPRegisterServerCall(requester, this.serviceName);

			registerServerCall.setMaxSessions(maxSessions);
			registerServerCall.setMaxConnections(maxConnections);
			registerServerCall.setPortNumber(tomcatPort);
			registerServerCall.setImmediateConnect(false);
			registerServerCall.setKeepAliveInterval(keepAliveFromSC);
			registerServerCall.setVersion(SCMPMessage.SC_VERSION.toString());
			registerServerCall.setLocalDateTime(DateTimeUtility.getCurrentTimeZoneMillis());
			registerServerCall.setUrlPath(this.urlPath);
			SCServerCallback callback = new SCServerCallback(true);
			try {
				registerServerCall.invoke(callback, Constants.DEFAULT_OPERATION_TIMEOUT_SECONDS);
			} catch (Exception e) {
				throw new SCServiceException("Register server failed. ", e);
			}
			SCMPMessage reply = callback.getMessageSync(Constants.DEFAULT_OPERATION_TIMEOUT_SECONDS);
			if (reply.isFault()) {
				SCServiceException ex = new SCServiceException("Register server failed.");
				ex.setSCErrorCode(reply.getHeaderInt(SCMPHeaderAttributeKey.SC_ERROR_CODE));
				ex.setSCErrorText(reply.getHeader(SCMPHeaderAttributeKey.SC_ERROR_TEXT));
				throw ex;
			}
			AppContext.attachedCommunicators.incrementAndGet();
		}
	}

	private void deregisterServletFromSC() throws SCServiceException {
		if (this.registered == false) {
			// SC server not registered - deregister not necessary
			return;
		}
		synchronized (AppContext.communicatorsLock) {
			// get communicator lock - avoids interference with other clients or scServers
			try {
				SCMPDeRegisterServerCall deRegisterServerCall = new SCMPDeRegisterServerCall(this.requester, this.serviceName);
				SCServerCallback callback = new SCServerCallback(true);
				try {
					deRegisterServerCall.invoke(callback, Constants.DEFAULT_OPERATION_TIMEOUT_SECONDS
							* Constants.SEC_TO_MILLISEC_FACTOR);
				} catch (Exception e) {
					throw new SCServiceException("Deregister server failed. ", e);
				}
				SCMPMessage reply = callback.getMessageSync(Constants.DEFAULT_OPERATION_TIMEOUT_SECONDS
						* Constants.SEC_TO_MILLISEC_FACTOR);
				if (reply.isFault()) {
					SCServiceException ex = new SCServiceException("Deregister server failed.");
					ex.setSCErrorCode(reply.getHeaderInt(SCMPHeaderAttributeKey.SC_ERROR_CODE));
					ex.setSCErrorText(reply.getHeader(SCMPHeaderAttributeKey.SC_ERROR_TEXT));
					throw ex;
				}
			} finally {
				this.registered = false;
				AppContext.attachedCommunicators.decrementAndGet();
			}
		}
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// SC is never doing a GET. Process would be like doPost.
		this.doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		IEncoderDecoder encoderDecoder = null;
		SCMPMessage scReply = null;
		// write reply to servlet output stream
		OutputStream serlvetOutStream = response.getOutputStream();
		try {
			byte[] buffer = new byte[request.getContentLength()];
			request.getInputStream().read(buffer);
			Statistics.getInstance().incrementTotalMessages(buffer.length);
			if (ConnectionLogger.isEnabledFull()) {
				ConnectionLogger.logReadBuffer(this.getClass().getSimpleName(), request.getServerName(), request.getServerPort(),
						buffer, 0, buffer.length);
			}
			ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
			encoderDecoder = AppContext.getEncoderDecoderFactory().createEncoderDecoder(buffer);
			SCMPMessage reqMessage = (SCMPMessage) encoderDecoder.decode(bais);

			if (reqMessage.isKeepAlive() == true) {
				// keep alive received, just reply nothing more to do.
				reqMessage.setIsReply(true);
				// write reply to servlet output stream
				encoderDecoder = AppContext.getEncoderDecoderFactory().createEncoderDecoder(reqMessage);
				encoderDecoder.encode(serlvetOutStream, reqMessage);
				response.flushBuffer();
				return;
			}

			if (reqMessage.isFault()) {
				// fault received nothing to to return - delete largeRequest/largeResponse
				SCMPMessageFault scmpFault = new SCMPMessageFault(SCMPError.BAD_REQUEST, "messagType="
						+ reqMessage.getMessageType());
				scmpFault.setMessageType(reqMessage.getMessageType());
				scmpFault.setLocalDateTime();
				// write reply to servlet output stream
				encoderDecoder = AppContext.getEncoderDecoderFactory().createEncoderDecoder(scmpFault);
				encoderDecoder.encode(serlvetOutStream, scmpFault);
				response.flushBuffer();
				return;
			}

			String messageTypeString = reqMessage.getMessageType();
			int oti = reqMessage.getHeaderInt(SCMPHeaderAttributeKey.OPERATION_TIMEOUT);

			switch (SCMPMsgType.getMsgType(messageTypeString)) {
			case SRV_CREATE_SESSION:
				scReply = ((SCBaseSessionServlet) this).baseCreateSession(reqMessage, oti);
				break;
			case SRV_DELETE_SESSION:
				scReply = ((SCBaseSessionServlet) this).baseDeleteSession(reqMessage, oti);
				break;
			case SRV_ABORT_SESSION:
				scReply = ((SCBaseSessionServlet) this).baseAbortSession(reqMessage, oti);
				break;
			case SRV_EXECUTE:
				scReply = ((SCBaseSessionServlet) this).baseExecute(reqMessage, oti);
				break;
			case SRV_SUBSCRIBE:
				scReply = ((SCBasePublishServlet) this).baseSubscribe(reqMessage, oti);
				break;
			case SRV_CHANGE_SUBSCRIPTION:
				scReply = ((SCBasePublishServlet) this).baseChangeSubscription(reqMessage, oti);
				break;
			case SRV_UNSUBSCRIBE:
				scReply = ((SCBasePublishServlet) this).baseUnsubscribe(reqMessage, oti);
				break;
			case SRV_ABORT_SUBSCRIPTION:
				scReply = ((SCBasePublishServlet) this).baseAbortSubscription(reqMessage, oti);
				break;
			default:
				scReply = new SCMPMessageFault(SCMPError.BAD_REQUEST, "Unknown message type received.");
				break;
			}
		} catch (Exception e) {
			LOGGER.error("Processing message failed.", e);
		}
		try {
			encoderDecoder = AppContext.getEncoderDecoderFactory().createEncoderDecoder(scReply);
			encoderDecoder.encode(serlvetOutStream, scReply);
			response.flushBuffer();
		} catch (Exception e) {
			LOGGER.error("Encoding message and replying to SC failed.", e);
		}
	}

	/**
	 * The Class SCServerCallback.
	 */
	protected class SCServerCallback extends SynchronousCallback {

		/**
		 * Instantiates a new sC server callback.
		 * 
		 * @param synchronous
		 *            the synchronous
		 */
		public SCServerCallback(boolean synchronous) {
			this.synchronous = synchronous;
		}
		// nothing to implement in this case - everything is done by super-class
	}

	@Override
	public void destroy() {
		super.destroy();
		try {
			this.deregisterServletFromSC();
			this.requester.destroy();
			AppContext.destroy();
		} catch (Exception e) {
			LOGGER.warn("Deregistering tomcat from SC failed", e);
		}
	}
}
