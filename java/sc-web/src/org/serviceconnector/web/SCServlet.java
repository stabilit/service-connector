package org.serviceconnector.web;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.serviceconnector.Constants;
import org.serviceconnector.api.SCMessage;
import org.serviceconnector.api.SCServiceException;
import org.serviceconnector.call.SCMPDeRegisterServerCall;
import org.serviceconnector.call.SCMPRegisterServerCall;
import org.serviceconnector.cmd.srv.ServerCommandFactory;
import org.serviceconnector.conf.RemoteNodeConfiguration;
import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.net.ConnectionType;
import org.serviceconnector.net.req.SCRequester;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.util.DateTimeUtility;
import org.serviceconnector.util.SynchronousCallback;

/**
 * Servlet implementation class SCServlet
 */
public class SCServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private boolean registered;
	private SCRequester requester;
	private String serviceName;
	private int listenerPort;

	static {
		// Initialize server command factory one time
		AppContext.initCommands(new ServerCommandFactory());
	}

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public SCServlet() {
		this.registered = false;
		this.requester = null;
		this.serviceName = null;
		this.listenerPort = 8080;
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

		this.serviceName = config.getInitParameter("serviceName");
		int maxConnections = Integer.parseInt(config.getInitParameter("maxConnections"));
		int maxSessions = Integer.parseInt(config.getInitParameter("maxSessions"));
		requester = new SCRequester(new RemoteNodeConfiguration(this.listenerPort + "server", "localhost", 7000,
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
		InputStream in = request.getInputStream();
		byte[] buffer = new byte[1024];
		in.read(buffer);
		System.out.println(new String(buffer));
	}

	/**
	 * Creates the session.
	 * 
	 * @param message
	 *            the message
	 * @param operationTimeoutMillis
	 *            the allowed time in milliseconds to complete the operation, observed by SC
	 * @return the sC message
	 */
	public SCMessage createSession(SCMessage message, int operationTimeoutMillis) {
		return message;
	}

	/**
	 * Delete session.
	 * 
	 * @param message
	 *            the message
	 * @param operationTimeoutMillis
	 *            the allowed time in milliseconds to complete the operation, observed by SC
	 */
	public void deleteSession(SCMessage message, int operationTimeoutMillis) {
	}

	/**
	 * Abort session.
	 * 
	 * @param message
	 *            the message
	 * @param operationTimeoutMillis
	 *            the allowed time in milliseconds to complete the operation, observed by SC
	 */
	public void abortSession(SCMessage message, int operationTimeoutMillis) {
	}

	/**
	 * Execute.
	 * 
	 * @param message
	 *            the message
	 * @param operationTimeoutMillis
	 *            the allowed time in milliseconds to complete the operation, observed by SC
	 * @return the sC message
	 */
	public SCMessage execute(SCMessage message, int operationTimeoutMillis) {
		return message;
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
}
