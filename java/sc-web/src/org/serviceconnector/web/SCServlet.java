package org.serviceconnector.web;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.scmp.SCMPMsgType;
import org.serviceconnector.util.DateTimeUtility;
import org.serviceconnector.util.Statistics;
import org.serviceconnector.util.SynchronousCallback;

/**
 * Servlet implementation class SCServlet
 */
public abstract class SCServlet extends HttpServlet implements ISCSessionServerCallback {
	private static final long serialVersionUID = 1L;
	private boolean registered;
	private SCRequester requester;
	private String serviceName;
	private int listenerPort;
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
		this.listenerPort = 8080;
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
		} catch (SCServiceException e) {
			throw new ServletException("Registering tomcat on SC failed", e);
		}
	}

	private void registerServletOnSC(ServletConfig config) throws SCServiceException {

		this.serviceName = config.getInitParameter(WebConstants.PROPERTY_SERVICE_NAME);
		int maxConnections = Integer.parseInt(config.getInitParameter("maxConnections"));
		int maxSessions = Integer.parseInt(config.getInitParameter("maxSessions"));
		this.requester = new SCRequester(new RemoteNodeConfiguration(this.listenerPort + "server", "localhost", 7000,
				ConnectionType.NETTY_HTTP.getValue(), 0, 1));

		// get lock on scServer - only one server is allowed to communicate over the initial connection
		synchronized (AppContext.communicatorsLock) {
			// get communicator lock - avoids interference with other clients or scServers
			AppContext.init();
			this.requester.getSCMPMsgSequenceNr().reset();

			SCMPRegisterServerCall registerServerCall = new SCMPRegisterServerCall(requester, this.serviceName);

			registerServerCall.setMaxSessions(maxSessions);
			registerServerCall.setMaxConnections(maxConnections);
			registerServerCall.setPortNumber(8080);
			registerServerCall.setImmediateConnect(false);
			registerServerCall.setKeepAliveInterval(0);
			registerServerCall.setVersion(SCMPMessage.SC_VERSION.toString());
			registerServerCall.setLocalDateTime(DateTimeUtility.getCurrentTimeZoneMillis());
			registerServerCall.setUrlPath(this.urlPath);
			SCServerCallback callback = new SCServerCallback(true);
			try {
				registerServerCall.invoke(callback, 60000);
			} catch (Exception e) {
				throw new SCServiceException("Register server failed. ", e);
			}
			SCMPMessage reply = callback.getMessageSync(60000);
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
			// sc server not registered - deregister not necessary
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
		InputStream in = request.getInputStream();
		byte[] buffer = new byte[1024];
		in.read(buffer);
		System.out.println(new String(buffer));
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			byte[] buffer = new byte[request.getContentLength()];
			request.getInputStream().read(buffer);
			Statistics.getInstance().incrementTotalMessages(buffer.length);
			if (ConnectionLogger.isEnabledFull()) {

				ConnectionLogger.logReadBuffer(this.getClass().getSimpleName(), request.getServerName(), request.getServerPort(),
						buffer, 0, buffer.length);
			}
			ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
			IEncoderDecoder encoderDecoder = AppContext.getEncoderDecoderFactory().createEncoderDecoder(buffer);
			SCMPMessage reqMessage = (SCMPMessage) encoderDecoder.decode(bais);
			String messageTypeString = reqMessage.getMessageType();
			int oti = reqMessage.getHeaderInt(SCMPHeaderAttributeKey.OPERATION_TIMEOUT);

			SCMPMessage scReply = null;

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
				// TODO JOT fault
				break;
			}

			// write reply to servlet output stream
			OutputStream serlvetOutStream = response.getOutputStream();
			encoderDecoder = AppContext.getEncoderDecoderFactory().createEncoderDecoder(scReply);
			encoderDecoder.encode(serlvetOutStream, scReply);
			response.flushBuffer();

		} catch (Throwable th) {
			// LOGGER.error("receive message", th);
			// TODO JOT write a scmpfault to the
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
		long msgSequenceNr = this.requester.getSCMPMsgSequenceNr().incrementAndGetMsgSequenceNr();
		reply.setHeader(SCMPHeaderAttributeKey.MESSAGE_SEQUENCE_NR, msgSequenceNr);

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
		long msgSequenceNr = this.requester.getSCMPMsgSequenceNr().incrementAndGetMsgSequenceNr();
		reply.setHeader(SCMPHeaderAttributeKey.MESSAGE_SEQUENCE_NR, msgSequenceNr);
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
