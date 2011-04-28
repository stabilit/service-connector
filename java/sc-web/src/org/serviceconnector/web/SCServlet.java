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
import org.serviceconnector.api.SCMessage;
import org.serviceconnector.api.SCServiceException;
import org.serviceconnector.api.srv.ISCSessionServerCallback;
import org.serviceconnector.call.SCMPDeRegisterServerCall;
import org.serviceconnector.call.SCMPRegisterServerCall;
import org.serviceconnector.cmd.srv.ServerCommandFactory;
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
 * Servlet implementation class SCServlet
 */
public abstract class SCServlet extends HttpServlet implements ISCSessionServerCallback {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = Logger.getLogger(SCServlet.class);

	private static final long serialVersionUID = 1L;
	private boolean registered;
	private SCRequester requester;
	private String serviceName;
	private String urlPath;

	static {
		// Initialize server command factory one time
		AppContext.initCommands(new ServerCommandFactory());
	}

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public SCServlet(String urlPath) {
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

	@Override
	public void destroy() {
		super.destroy();
		try {
			this.deregisterServletFromSC();
		} catch (SCServiceException e) {
			// TODO Logging JOT ???
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
			String messageTypeString = reqMessage.getMessageType();
			int oti = reqMessage.getHeaderInt(SCMPHeaderAttributeKey.OPERATION_TIMEOUT);

			switch (SCMPMsgType.getMsgType(messageTypeString)) {
			case SRV_CREATE_SESSION:
				scReply = this.baseCreateSession(reqMessage, oti);
				break;
			case SRV_DELETE_SESSION:
				scReply = this.baseDeleteSession(reqMessage, oti);
				break;
			case SRV_ABORT_SESSION:
				scReply = this.baseAbortSession(reqMessage, oti);
				break;
			case SRV_EXECUTE:
				scReply = this.baseExecute(reqMessage, oti);
				break;
			default:
				scReply = new SCMPMessageFault(SCMPError.BAD_REQUEST, "Unknown message type received.");
				break;
			}

		} catch (Throwable th) {
			// LOGGER.error("receive message", th);
		}
		// write reply to servlet output stream
		OutputStream serlvetOutStream = response.getOutputStream();
		encoderDecoder = AppContext.getEncoderDecoderFactory().createEncoderDecoder(scReply);
		try {
			encoderDecoder.encode(serlvetOutStream, scReply);
			response.flushBuffer();
		} catch (Exception e) {
			// LOGGER.error("receive message", th);
		}
	}

	private SCMPMessage baseCreateSession(SCMPMessage reqMessage, int operationTimeoutMillis) {
		// create scMessage
		SCMessage scMessage = new SCMessage();
		scMessage.setData(reqMessage.getBody());
		scMessage.setDataLength(reqMessage.getBodyLength());
		scMessage.setCompressed(reqMessage.getHeaderFlag(SCMPHeaderAttributeKey.COMPRESSION));
		scMessage.setMessageInfo(reqMessage.getHeader(SCMPHeaderAttributeKey.MSG_INFO));
		scMessage.setSessionId(reqMessage.getSessionId());
		scMessage.setSessionInfo(reqMessage.getHeader(SCMPHeaderAttributeKey.SESSION_INFO));
		scMessage.setServiceName(reqMessage.getServiceName());

		// call TOMCAT SC server API interface
		SCMessage scReply = this.createSession(scMessage, operationTimeoutMillis);

		// set up reply
		SCMPMessage reply = new SCMPMessage();
		// long msgSequenceNr = this.requester.getSCMPMsgSequenceNr().incrementAndGetMsgSequenceNr();
		// reply.setHeader(SCMPHeaderAttributeKey.MESSAGE_SEQUENCE_NR, msgSequenceNr);

		if (scReply != null) {
			reply.setBody(scReply.getData());
			if (scReply.isCompressed()) {
				reply.setHeaderFlag(SCMPHeaderAttributeKey.COMPRESSION);
			}
			if (scReply.getAppErrorCode() != Constants.EMPTY_APP_ERROR_CODE) {
				reply.setHeader(SCMPHeaderAttributeKey.APP_ERROR_CODE, scReply.getAppErrorCode());
			}
			if (scReply.getAppErrorText() != null) {
				reply.setHeader(SCMPHeaderAttributeKey.APP_ERROR_TEXT, scReply.getAppErrorText());
			}
			if (scReply.isReject()) {
				// session rejected
				reply.setHeaderFlag(SCMPHeaderAttributeKey.REJECT_SESSION);
			}
			if (scReply.getSessionInfo() != null) {
				reply.setHeader(SCMPHeaderAttributeKey.SESSION_INFO, scReply.getSessionInfo());
			}
		}
		reply.setSessionId(reqMessage.getSessionId());
		reply.setServiceName(serviceName);
		reply.setMessageType(reqMessage.getMessageType());
		return reply;
	}

	private SCMPMessage baseDeleteSession(SCMPMessage reqMessage, int operationTimeoutMillis) {
		// create scMessage
		SCMessage scMessage = new SCMessage();
		scMessage.setData(reqMessage.getBody());
		scMessage.setDataLength(reqMessage.getBodyLength());
		scMessage.setCompressed(reqMessage.getHeaderFlag(SCMPHeaderAttributeKey.COMPRESSION));
		scMessage.setMessageInfo(reqMessage.getHeader(SCMPHeaderAttributeKey.MSG_INFO));
		scMessage.setSessionId(reqMessage.getSessionId());
		scMessage.setServiceName(reqMessage.getServiceName());
		scMessage.setSessionInfo(reqMessage.getHeader(SCMPHeaderAttributeKey.SESSION_INFO));

		this.deleteSession(scMessage, operationTimeoutMillis);
		// set up reply
		SCMPMessage reply = new SCMPMessage();
		reply.setServiceName(serviceName);
		reply.setSessionId(reqMessage.getSessionId());
		reply.setMessageType(reqMessage.getMessageType());
		long msgSequenceNr = this.requester.getSCMPMsgSequenceNr().incrementAndGetMsgSequenceNr();
		reply.setHeader(SCMPHeaderAttributeKey.MESSAGE_SEQUENCE_NR, msgSequenceNr);
		return reply;
	}

	private SCMPMessage baseAbortSession(SCMPMessage reqMessage, int operationTimeoutMillis) {
		// create scMessage
		SCMessage scMessage = new SCMessage();
		scMessage.setData(reqMessage.getBody());
		scMessage.setDataLength(reqMessage.getBodyLength());
		scMessage.setCompressed(reqMessage.getHeaderFlag(SCMPHeaderAttributeKey.COMPRESSION));
		scMessage.setMessageInfo(reqMessage.getHeader(SCMPHeaderAttributeKey.MSG_INFO));
		scMessage.setSessionId(reqMessage.getSessionId());
		scMessage.setServiceName(reqMessage.getServiceName());

		this.abortSession(scMessage, operationTimeoutMillis);
		// set up reply
		SCMPMessage reply = new SCMPMessage();
		reply.setServiceName(serviceName);
		reply.setSessionId(reqMessage.getSessionId());
		reply.setMessageType(reqMessage.getMessageType());
		return reply;
	}

	private SCMPMessage baseExecute(SCMPMessage reqMessage, int operationTimeoutMillis) {
		// create scMessage
		SCMessage scMessage = new SCMessage();
		scMessage.setData(reqMessage.getBody());
		scMessage.setDataLength(reqMessage.getBodyLength());
		scMessage.setCompressed(reqMessage.getHeaderFlag(SCMPHeaderAttributeKey.COMPRESSION));
		scMessage.setMessageInfo(reqMessage.getHeader(SCMPHeaderAttributeKey.MSG_INFO));
		scMessage.setCacheId(reqMessage.getCacheId());
		scMessage.setCachePartNr(reqMessage.getCachePartNr());
		scMessage.setServiceName(reqMessage.getServiceName());
		scMessage.setSessionId(reqMessage.getSessionId());

		SCMessage scReply = this.execute(scMessage, operationTimeoutMillis);

		// set up reply
		SCMPMessage reply = new SCMPMessage();
		reply.setIsReply(true);
		reply.setServiceName(serviceName);
		reply.setSessionId(reqMessage.getSessionId());
		// long msgSequenceNr = this.requester.getSCMPMsgSequenceNr().incrementAndGetMsgSequenceNr();
		// reply.setHeader(SCMPHeaderAttributeKey.MESSAGE_SEQUENCE_NR, msgSequenceNr);
		reply.setMessageType(reqMessage.getMessageType());
		if (scReply != null) {
			reply.setBody(scReply.getData());
			if (scReply.getCacheExpirationDateTime() != null) {
				reply.setHeader(SCMPHeaderAttributeKey.CACHE_EXPIRATION_DATETIME,
						DateTimeUtility.getDateTimeAsString(scReply.getCacheExpirationDateTime()));
			}
			reply.setCacheId(scReply.getCacheId());
			if (scReply.getMessageInfo() != null) {
				reply.setHeader(SCMPHeaderAttributeKey.MSG_INFO, scReply.getMessageInfo());
			}
			if (scReply.isCompressed()) {
				reply.setHeaderFlag(SCMPHeaderAttributeKey.COMPRESSION);
			}
			if (scReply.getAppErrorCode() != Constants.EMPTY_APP_ERROR_CODE) {
				reply.setHeader(SCMPHeaderAttributeKey.APP_ERROR_CODE, scReply.getAppErrorCode());
			}
			if (scReply.getAppErrorText() != null) {
				reply.setHeader(SCMPHeaderAttributeKey.APP_ERROR_TEXT, scReply.getAppErrorText());
			}
			reply.setPartSize(scReply.getPartSize());
		}
		return reply;
	}

	@Override
	public abstract SCMessage createSession(SCMessage message, int operationTimeoutMillis);

	@Override
	public abstract void deleteSession(SCMessage message, int operationTimeoutMillis);

	@Override
	public abstract void abortSession(SCMessage message, int operationTimeoutMillis);

	@Override
	public abstract SCMessage execute(SCMessage message, int operationTimeoutMillis);

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
}
