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
package org.serviceconnector.web;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.serviceconnector.Constants;
import org.serviceconnector.api.SCServiceException;
import org.serviceconnector.api.srv.SrvPublishService;
import org.serviceconnector.api.srv.SrvService;
import org.serviceconnector.api.srv.SrvServiceRegistry;
import org.serviceconnector.api.srv.SrvSessionService;
import org.serviceconnector.call.SCMPCheckRegistrationCall;
import org.serviceconnector.call.SCMPDeRegisterServerCall;
import org.serviceconnector.call.SCMPRegisterServerCall;
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.conf.RemoteNodeConfiguration;
import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.log.ConnectionLogger;
import org.serviceconnector.net.ConnectionType;
import org.serviceconnector.net.IEncoderDecoder;
import org.serviceconnector.net.req.SCRequester;
import org.serviceconnector.net.res.SCMPSessionCompositeRegistry;
import org.serviceconnector.scmp.SCMPCompositeReceiver;
import org.serviceconnector.scmp.SCMPCompositeSender;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.scmp.SCMPMessageFault;
import org.serviceconnector.scmp.SCMPMessageSequenceNr;
import org.serviceconnector.scmp.SCMPMsgType;
import org.serviceconnector.scmp.SCMPPart;
import org.serviceconnector.util.DateTimeUtility;
import org.serviceconnector.util.ITimeout;
import org.serviceconnector.util.Statistics;
import org.serviceconnector.util.SynchronousCallback;
import org.serviceconnector.util.TimeoutWrapper;
import org.serviceconnector.util.ValidatorUtility;

/**
 * The Class SCBaseServlet. Base servlet for service implementations.
 * SCBaseServlet is implemented by SCBaseSessionServlet and SCBasePublishServlet. User of servlet API must extend
 * SCBaseSessionServlet or SCBasePublishServlet depending on kind of service he likes to implement.<br/>
 * <br/>
 * Communication between server and SC starts with registering the server for specific service on the SC. This initial steps are
 * done by SCBaseServlet in startup phase. Settings like keep alive interval or max number of connections are extracted from the
 * web.xml. After successful register the interfaces ISCSessionServerCallback/ISCPublishServerCallback implemented by the base
 * classes get informed about client actions (create session, execute, subscribe, abort session).<br />
 * <br />
 * One servlet serves one service (defined in web.xml) at the time. However two servlets may serve the same service. Looks like two
 * different servers for an SC instance.<br />
 * <br />
 * Deregister of the service is done by the SCBaseServlet and happens automatically when servlet gets destroyed. <br />
 * <br />
 * Required parameters in web.xml, example for service "session-1":<br />
 * 
 * <pre>
 *	<context-param>
 *		<param-name>scPort</param-name>
 *		<param-value>7000</param-value>
 *	</context-param>
 *	<context-param>
 *		<param-name>scHost</param-name>
 *		<param-value>localhost</param-value>
 *	</context-param>
 *	<context-param>
 *		<param-name>toSCKeepAliveIntervalSeconds</param-name>
 *		<param-value>10</param-value>
 *	</context-param>
 * 	<context-param>
 *		<param-name>keepAliveTimeoutSeconds</param-name>
 *		<param-value>10</param-value>
 *	</context-param>
 *	<context-param>
 *		<param-name>checkRegistrationIntervalSeconds</param-name>
 *		<param-value>300</param-value>
 *	</context-param>
 *	<context-param>
 *		<param-name>tomcatPort</param-name>
 *		<param-value>8080</param-value>
 *	</context-param>
 *
 *	Example of a servlet service definition:
 *	<servlet>
 *		<description>Demo servlet for session service</description>
 *		<display-name>DemoSCSessionServlet</display-name>
 *		<servlet-name>DemoSCSessionServlet</servlet-name>
 *		<servlet-class>org.serviceconnector.web.example.DemoSCSessionServlet</servlet-class>
 *		<init-param>
 *			<param-name>serviceName</param-name>
 *			<param-value>session-1</param-value>
 *		</init-param>
 *		<init-param>
 *			<param-name>maxConnections</param-name>
 *			<param-value>100</param-value>
 *		</init-param>
 *		<init-param>
 *			<param-name>maxSessions</param-name>
 *			<param-value>100</param-value>
 *		</init-param>
 *		<load-on-startup>0</load-on-startup>
 *	</servlet>
 *	<servlet-mapping>
 *		<servlet-name>DemoSCSessionServlet</servlet-name>
 *		<url-pattern>/DemoSCSessionServlet</url-pattern>
 *	</servlet-mapping>
 * </pre>
 */
public abstract class SCBaseServlet extends HttpServlet {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = Logger.getLogger(SCBaseServlet.class);
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	/** The composite registry. */
	protected static SCMPSessionCompositeRegistry compositeRegistry = AppContext.getSCMPSessionCompositeRegistry();
	/** The registered. */
	protected boolean registered;
	/** The requester. */
	protected SCRequester requester;
	/** The service name. */
	protected String serviceName;
	/** The URL path. Used by SC to call the servlet(URL). */
	private String urlPath;
	/** The max connections the servlet can handle. */
	private int maxConnections;
	/** The max sessions the servlet can handle. */
	private int maxSessions;
	/** The tomcat listening port. */
	private int tomcatPort;
	/** The check registration interval seconds. */
	private int checkRegistrationIntervalSeconds;
	/** The serverTimeout, timeout runs when server entry on SC need to be refreshed. */
	private ScheduledFuture<TimeoutWrapper> serverTimeout;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	protected SCBaseServlet(String urlPath) {
		this.registered = false;
		this.requester = null;
		this.serviceName = null;
		this.urlPath = urlPath;
		this.maxConnections = 0;
		this.maxSessions = 0;
		this.tomcatPort = 0;
		this.checkRegistrationIntervalSeconds = Constants.DEFAULT_CHECK_REGISTRATION_INTERVAL_SECONDS;
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		try {
			if (this.registered == false) {
				// servlet has not been registered before
				this.loadConfigParams(config);
			}
		} catch (Exception e) {
			LOGGER.error("Loading configuration web.xml failed", e);
			throw new ServletException("Loading configuration web.xml failed", e);
		}
		try {
			if (this.registered == false) {
				// servlet has not been registered before
				this.registerServletOnSC(config);
				this.registered = true;
			}
		} catch (Exception e) {
			LOGGER.error("Registering servlet on SC failed", e);
			throw new ServletException("Registering servlet on SC failed", e);
		}
	}

	/**
	 * Load configuration parameters.
	 * 
	 * @param config
	 *            the configuration
	 * @throws SCMPValidatorException
	 *             validation of configuration parameters failed
	 */
	private void loadConfigParams(ServletConfig config) throws SCMPValidatorException {
		this.serviceName = config.getInitParameter(WebConstants.PROPERTY_SERVICE_NAME);
		this.maxConnections = Integer.parseInt(config.getInitParameter(WebConstants.PROPERTY_MAX_CONNECTIONS));
		this.maxSessions = Integer.parseInt(config.getInitParameter(WebConstants.PROPERTY_MAX_SESSIONS));

		// prepare requester to register servlet on SC
		ServletContext context = config.getServletContext();
		this.tomcatPort = Integer.parseInt(context.getInitParameter(WebConstants.PROPERTY_TOMCAT_PORT));
		String remotNodeName = this.tomcatPort + this.getServletName();
		String scHost = context.getInitParameter(WebConstants.PROPERTY_SC_HOST);
		int scPort = Integer.parseInt(context.getInitParameter(WebConstants.PROPERTY_SC_PORT));
		int keepAliveIntervalToSCSeconds = Integer
				.parseInt(context.getInitParameter(WebConstants.PROPERTY_KEEPALIVE_INTERVAL_TOSC));
		int keepAliveOTISeconds = Integer.parseInt(context.getInitParameter(WebConstants.PROPERTY_KEEPALIVE_OTI));
		this.checkRegistrationIntervalSeconds = Integer.parseInt(context
				.getInitParameter(WebConstants.PROPERTY_CHECK_REGRISTRATION_INTERVAL));

		if (scHost == null) {
			throw new SCMPValidatorException("Host must be set.");
		}
		ValidatorUtility.validateInt(Constants.MIN_PORT_VALUE, scPort, Constants.MAX_PORT_VALUE, SCMPError.HV_WRONG_PORTNR);
		ValidatorUtility
				.validateInt(Constants.MIN_PORT_VALUE, this.tomcatPort, Constants.MAX_PORT_VALUE, SCMPError.HV_WRONG_PORTNR);
		if (scPort == this.tomcatPort) {
			throw new SCMPValidatorException("SC port and tomcat port must not be the same.");
		}
		// init the requester to communicate to SC
		RemoteNodeConfiguration remoteNodeConf = new RemoteNodeConfiguration(remotNodeName, scHost, scPort,
				ConnectionType.NETTY_HTTP.getValue(), keepAliveIntervalToSCSeconds, checkRegistrationIntervalSeconds, 1);
		this.requester = new SCRequester(remoteNodeConf, keepAliveOTISeconds * Constants.SEC_TO_MILLISEC_FACTOR);
	}

	/**
	 * Register servlet on SC.
	 * 
	 * @param config
	 *            the configuration
	 * @throws SCServiceException
	 *             register server on SC failed<br />
	 *             error message received from SC <br />
	 */
	private void registerServletOnSC(ServletConfig config) throws SCServiceException {
		int keepAliveFromSCSeconds = 0; // inactive keep alive from SC

		// get lock on scServer - only one server is allowed to communicate over the initial connection
		synchronized (AppContext.communicatorsLock) {
			// get communicator lock - avoids interference with other clients or scServers
			AppContext.init();
			this.requester.getSCMPMsgSequenceNr().reset();

			SCMPRegisterServerCall registerServerCall = new SCMPRegisterServerCall(requester, this.serviceName);

			registerServerCall.setMaxSessions(this.maxSessions);
			registerServerCall.setMaxConnections(this.maxConnections);
			registerServerCall.setPortNumber(this.tomcatPort);
			registerServerCall.setImmediateConnect(false);
			registerServerCall.setKeepAliveIntervalSeconds(keepAliveFromSCSeconds);
			registerServerCall.setCheckRegistrationIntervalSeconds(this.checkRegistrationIntervalSeconds);
			registerServerCall.setVersion(SCMPMessage.SC_VERSION.toString());
			registerServerCall.setLocalDateTime(DateTimeUtility.getCurrentTimeZoneMillis());
			registerServerCall.setUrlPath(this.urlPath);
			SCServerCallback callback = new SCServerCallback(true);
			try {
				registerServerCall.invoke(callback, Constants.DEFAULT_OPERATION_TIMEOUT_SECONDS * Constants.SEC_TO_MILLISEC_FACTOR);
			} catch (Exception e) {
				throw new SCServiceException("Register server failed. ", e);
			}
			SCMPMessage reply = callback.getMessageSync(Constants.DEFAULT_OPERATION_TIMEOUT_SECONDS
					* Constants.SEC_TO_MILLISEC_FACTOR);
			if (reply.isFault()) {
				SCServiceException ex = new SCServiceException("Register server failed.");
				ex.setSCErrorCode(reply.getHeaderInt(SCMPHeaderAttributeKey.SC_ERROR_CODE));
				ex.setSCErrorText(reply.getHeader(SCMPHeaderAttributeKey.SC_ERROR_TEXT));
				throw ex;
			}
			AppContext.attachedCommunicators.incrementAndGet();
		}
		// set up server timeout thread
		this.triggerServerTimeout();
	}

	/**
	 * Deregister servlet from SC.
	 * 
	 * @throws SCServiceException
	 *             deregister failed<br />
	 *             error message received from SC<br />
	 */
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
		SCMPMessage reqMessage = null;
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
			reqMessage = (SCMPMessage) encoderDecoder.decode(bais);

			if (reqMessage.isKeepAlive() == true) {
				// keep alive received, just reply nothing more to do.
				reqMessage.setIsReply(true);
				// write reply to servlet output stream
				this.writeResponse(reqMessage, scReply, response);
				return;
			}

			String sessionId = reqMessage.getSessionId();
			if (reqMessage.isFault()) {
				SCBaseServlet.compositeRegistry.removeSCMPLargeResponse(sessionId);
				SCBaseServlet.compositeRegistry.removeSCMPLargeRequest(sessionId);
				// fault received nothing to to return - delete largeRequest/largeResponse
				SCMPMessageFault scmpFault = new SCMPMessageFault(reqMessage.getSCMPVersion(), SCMPError.BAD_REQUEST, "messagType="
						+ reqMessage.getMessageType());
				scmpFault.setMessageType(reqMessage.getMessageType());
				scmpFault.setLocalDateTime();
				// write reply to servlet output stream
				this.writeResponse(reqMessage, scReply, response);
				return;
			}

			if (this.handleLargeResponse(request, response, reqMessage)) {
				// large message and response has been handled no need to continue
				return;
			}

			if (this.handleLargeRequestNeeded(request, response, reqMessage)) {
				SCMPMessage message = this.handleLargeRequest(request, response, reqMessage);
				if (message == null) {
					// reply inside
					return;
				}
				reqMessage = message;
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
				scReply = new SCMPMessageFault(reqMessage.getSCMPVersion(), SCMPError.BAD_REQUEST, "Unknown message type received.");
				break;
			}
		} catch (Exception e) {
			LOGGER.error("Processing message failed.", e);
			// fault received nothing to to return - delete largeRequest/largeResponse
			SCMPMessageFault scmpFault = new SCMPMessageFault(reqMessage.getSCMPVersion(), SCMPError.SERVER_ERROR,
					"Processing message failed when calling servlet API");
			scmpFault.setMessageType(reqMessage.getMessageType());
			scmpFault.setLocalDateTime();
		}
		this.writeResponse(reqMessage, scReply, response);
	}

	/**
	 * Write response.
	 * 
	 * @param requestMessage
	 *            the request message
	 * @param responseMessage
	 *            the message
	 * @param response
	 *            the response
	 */
	private void writeResponse(SCMPMessage requestMessage, SCMPMessage responseMessage, HttpServletResponse response) {
		IEncoderDecoder encoderDecoder;
		try {
			if (responseMessage.isLargeMessage()) {
				// response is large create a large response for reply
				SCMPCompositeSender largeResponse = new SCMPCompositeSender(responseMessage);
				SCMPMessage firstSCMP = largeResponse.getFirst();
				SCMPMessageSequenceNr messageSequenceNr = this.requester.getSCMPMsgSequenceNr();
				firstSCMP.setHeader(SCMPHeaderAttributeKey.MESSAGE_SEQUENCE_NR, messageSequenceNr.incrementAndGetMsgSequenceNr());
				int oti = requestMessage.getHeaderInt(SCMPHeaderAttributeKey.OPERATION_TIMEOUT);
				// adding compositeReceiver to the composite registry
				SCBaseServlet.compositeRegistry.addSCMPLargeResponse(requestMessage.getSessionId(), largeResponse, oti);
				responseMessage = firstSCMP;
			}
			encoderDecoder = AppContext.getEncoderDecoderFactory().createEncoderDecoder(responseMessage);
			encoderDecoder.encode(response.getOutputStream(), responseMessage);
			response.flushBuffer();
		} catch (Exception e) {
			LOGGER.error("Encoding message and replying to SC failed.", e);
		}
	}

	/**
	 * Handle large response.
	 * 
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @param scmpReq
	 *            the message of the request
	 * @return true, if successful
	 */
	private boolean handleLargeResponse(HttpServletRequest request, HttpServletResponse response, SCMPMessage scmpReq) {
		String sessionId = scmpReq.getSessionId();
		// large messages needs to be handled
		SCMPCompositeSender largeResponse = SCBaseServlet.compositeRegistry.getSCMPLargeResponse(sessionId);

		if (largeResponse != null && scmpReq.isPart()) {
			// sending of a large response has already been started and incoming SCMP is a pull request
			if (largeResponse.hasNext()) {
				// there are still parts to send to complete request
				SCMPMessage nextSCMP = largeResponse.getNext();
				// handling msgSequenceNr
				SCMPMessageSequenceNr msgSequenceNr = this.requester.getSCMPMsgSequenceNr();
				nextSCMP.setHeader(SCMPHeaderAttributeKey.MESSAGE_SEQUENCE_NR, msgSequenceNr.incrementAndGetMsgSequenceNr());
				this.writeResponse(scmpReq, nextSCMP, response);
				return true;
			}
			SCBaseServlet.compositeRegistry.removeSCMPLargeResponse(sessionId);
		}
		return false;
	}

	/**
	 * Handle large request needed.
	 * 
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @param scmpReq
	 *            the message of the request
	 * @return true, if successful
	 */
	private boolean handleLargeRequestNeeded(HttpServletRequest request, HttpServletResponse response, SCMPMessage scmpReq) {
		String sessionId = scmpReq.getSessionId();
		// gets the large request or creates a new one if necessary
		SCMPCompositeReceiver largeRequest = SCBaseServlet.compositeRegistry.getSCMPLargeRequest(sessionId);

		if (largeRequest == null) {
			// no compositeReceiver used before
			if (scmpReq.isPart() == false) {
				// request not chunk
				return false;
			}
			// first part of a large request received - create large request
			largeRequest = new SCMPCompositeReceiver(scmpReq, (SCMPMessage) scmpReq);
			int oti = scmpReq.getHeaderInt(SCMPHeaderAttributeKey.OPERATION_TIMEOUT);
			// add largeRequest to the registry
			SCBaseServlet.compositeRegistry.addSCMPLargeRequest(sessionId, largeRequest, oti);
		} else {
			// next part of a large request received - add to large request
			largeRequest.add(scmpReq);
		}
		return true;
	}

	/**
	 * Handle large request.
	 * 
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @param scmpReq
	 *            the message of the request
	 * @return the sCMP message
	 */
	private SCMPMessage handleLargeRequest(HttpServletRequest request, HttpServletResponse response, SCMPMessage scmpReq) {
		String sessionId = scmpReq.getSessionId();
		// gets the large request or creates a new one if necessary
		SCMPCompositeReceiver largeRequest = SCBaseServlet.compositeRegistry.getSCMPLargeRequest(sessionId);
		SCMPMessageSequenceNr msgSequenceNr = this.requester.getSCMPMsgSequenceNr();
		SCMPMessage scmpReply = null;
		if (scmpReq.isPart()) {
			// received message part - request not complete yet
			largeRequest.incomplete();
			// set up poll response - SCMP Version request
			scmpReply = new SCMPPart(scmpReq.getSCMPVersion(), true);
			scmpReply.setHeader(SCMPHeaderAttributeKey.MESSAGE_SEQUENCE_NR, msgSequenceNr.incrementAndGetMsgSequenceNr());
			scmpReply.setIsReply(true);
			scmpReply.setMessageType(scmpReq.getMessageType());
		} else {
			// last message of a chunk message received - request complete now
			largeRequest.complete();
			largeRequest.setHeader(SCMPHeaderAttributeKey.MESSAGE_SEQUENCE_NR, msgSequenceNr.incrementAndGetMsgSequenceNr());
			scmpReply = largeRequest;
		}

		if (largeRequest != null && largeRequest.isComplete() == false) {
			// request is not complete yet
			this.writeResponse(scmpReq, scmpReply, response);
			return null;
		}
		// removes largeResponse - request is complete don't need to know preceding messages any more
		SCBaseServlet.compositeRegistry.removeSCMPLargeRequest(sessionId);
		return scmpReply;
	}

	/**
	 * Check registration with default operation timeout. This message can be sent from the registered server to SC in order to
	 * check its registration.
	 * 
	 * @throws SCServiceException
	 *             server is not registered for a service<br />
	 *             check registration failed<br />
	 *             error message received from SC <br />
	 */
	public synchronized void checkRegistration() throws SCServiceException {
		this.checkRegistration(Constants.DEFAULT_OPERATION_TIMEOUT_SECONDS);
	}

	/**
	 * Check registration. This message can be sent from the registered server to SC in order to check its registration.
	 * 
	 * @param operationTimeoutSeconds
	 *            the allowed time in seconds to complete the operation
	 * @throws SCServiceException
	 *             server is not registered for a service<br />
	 *             check registration failed<br />
	 *             error message received from SC <br />
	 */
	public synchronized void checkRegistration(int operationTimeoutSeconds) throws SCServiceException {
		if (this.registered == false) {
			throw new SCServiceException("Server is not registered for a service.");
		}
		// cancel server timeout not if its running already, you might interrupt current thread
		this.cancelServerTimeout(false);
		SCMPCheckRegistrationCall checkRegistrationCall = new SCMPCheckRegistrationCall(this.requester, this.serviceName);
		SCServerCallback callback = new SCServerCallback(true);
		try {
			checkRegistrationCall.invoke(callback, operationTimeoutSeconds * Constants.SEC_TO_MILLISEC_FACTOR);
		} catch (Exception e) {
			throw new SCServiceException("Check registration failed. ", e);
		}
		SCMPMessage reply = callback.getMessageSync(operationTimeoutSeconds * Constants.SEC_TO_MILLISEC_FACTOR);
		if (reply.isFault()) {
			SCServiceException ex = new SCServiceException("Check registration failed.");
			ex.setSCErrorCode(reply.getHeaderInt(SCMPHeaderAttributeKey.SC_ERROR_CODE));
			ex.setSCErrorText(reply.getHeader(SCMPHeaderAttributeKey.SC_ERROR_TEXT));
			throw ex;
		}
		// set up server timeout thread
		this.triggerServerTimeout();
	}

	/**
	 * Trigger server timeout.
	 */
	@SuppressWarnings("unchecked")
	private void triggerServerTimeout() {
		if (this.checkRegistrationIntervalSeconds == 0) {
			// check registration interval not active
			return;
		}
		SCServerTimeout serverTimeout = new SCServerTimeout();
		TimeoutWrapper timeoutWrapper = new TimeoutWrapper(serverTimeout);
		this.serverTimeout = (ScheduledFuture<TimeoutWrapper>) AppContext.eci_cri_Scheduler.schedule(timeoutWrapper,
				(int) (this.checkRegistrationIntervalSeconds * Constants.SEC_TO_MILLISEC_FACTOR), TimeUnit.MILLISECONDS);
	}

	/**
	 * Cancel server timeout.
	 * 
	 * @param mayInterruptIfRunning
	 *            the may interrupt if running
	 */
	private void cancelServerTimeout(boolean mayInterruptIfRunning) {
		if (this.serverTimeout == null) {
			// not timeout has been set up
			return;
		}
		SCBaseServlet.this.serverTimeout.cancel(mayInterruptIfRunning);
		// removes canceled timeouts
		AppContext.eci_cri_Scheduler.purge();
	}

	@Override
	public void destroy() {
		super.destroy();
		try {
			this.deregisterServletFromSC();
			this.requester.destroy();
			AppContext.destroy();
		} catch (Exception e) {
			LOGGER.warn("Deregistering servlet from SC failed", e);
		}
	}

	/**
	 * The Class SCServerTimeout. Get control at the time a server refresh is needed. Takes care of sending a check registration to
	 * SC which gets the server entry on SC refreshed.
	 */
	private class SCServerTimeout implements ITimeout {

		/**
		 * Time run out, need to send a check registration to SC otherwise server gets destroyed on SC for dead server reason.
		 */
		@Override
		public void timeout() {
			// send echo to SC
			try {
				SCBaseServlet.this.checkRegistration(SCBaseServlet.this.checkRegistrationIntervalSeconds);
			} catch (SCServiceException e) {
				// check registration failed - inform callback
				SrvServiceRegistry srvServiceRegistry = AppContext.getSrvServiceRegistry();
				SrvService srvService = srvServiceRegistry.getSrvService(SCBaseServlet.this.serviceName + Constants.UNDERLINE
						+ SCBaseServlet.this.tomcatPort);
				if (srvService instanceof SrvSessionService) {
					((SrvSessionService) srvService).getCallback().exceptionCaught(e);
				} else if (srvService instanceof SrvPublishService) {
					((SrvPublishService) srvService).getCallback().exceptionCaught(e);
				}
			}
		}

		/** {@inheritDoc} */
		@Override
		public int getTimeoutMillis() {
			return SCBaseServlet.this.checkRegistrationIntervalSeconds * Constants.SEC_TO_MILLISEC_FACTOR;
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
}
