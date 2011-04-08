package org.serviceconnector.web;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.serviceconnector.api.SCServiceException;
import org.serviceconnector.call.SCMPRegisterServerCall;
import org.serviceconnector.conf.RemoteNodeConfiguration;
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
	private static boolean registered = false;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public SCServlet() {
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		try {
			if (registered == false) {
				this.registerTomcatOnSC(config);
				registered = true;
			}
		} catch (SCServiceException e) {
			throw new ServletException("Registering tomcat on SC failed", e);
		}
	}

	private void registerTomcatOnSC(ServletConfig config) throws SCServiceException {

		String serviceName = config.getInitParameter("serviceName");
		int maxConnections = Integer.parseInt(config.getInitParameter("maxConnections"));
		int maxSessions = Integer.parseInt(config.getInitParameter("maxSessions"));
		SCRequester requester = new SCRequester(new RemoteNodeConfiguration(8080 + "server", "localhost", 7000,
				ConnectionType.NETTY_HTTP.getValue(), 0, 1));

		SCMPRegisterServerCall registerServerCall = new SCMPRegisterServerCall(requester, serviceName);

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
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ServletContext ctx = this.getServletContext();
		String value = ctx.getInitParameter("tst");
		response.getOutputStream().write("Hello".getBytes());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

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
