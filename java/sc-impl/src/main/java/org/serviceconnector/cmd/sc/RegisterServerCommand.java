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
package org.serviceconnector.cmd.sc;

import java.net.InetSocketAddress;
import java.util.Date;

import org.apache.log4j.Logger;
import org.serviceconnector.cmd.ICommandValidator;
import org.serviceconnector.cmd.SCMPCommandException;
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.net.SCMPCommunicationException;
import org.serviceconnector.scmp.HasFaultResponseException;
import org.serviceconnector.scmp.IRequest;
import org.serviceconnector.scmp.IResponse;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.scmp.SCMPMsgType;
import org.serviceconnector.service.Server;
import org.serviceconnector.service.Service;
import org.serviceconnector.util.DateTimeUtility;
import org.serviceconnector.util.ValidatorUtility;

/**
 * The Class RegisterServerCommand. Responsible for validation and execution of register command. Used to register
 * backend server in SC. Backend server will be registered in server registry of SC.
 * 
 * @author JTraber
 */
public class RegisterServerCommand extends CommandAdapter {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(RegisterServerCommand.class);

	/**
	 * Instantiates a new RegisterServerCommand.
	 */
	public RegisterServerCommand() {
		this.commandValidator = new RegisterServerCommandValidator();
	}

	/** {@inheritDoc} */
	@Override
	public SCMPMsgType getKey() {
		return SCMPMsgType.REGISTER_SERVER;
	}

	/** {@inheritDoc} */
	@Override
	public void run(IRequest request, IResponse response) throws Exception {
		InetSocketAddress socketAddress = request.getRemoteSocketAddress();

		SCMPMessage message = request.getMessage();
		String serviceName = message.getServiceName();
		// lookup service and checks properness
		Service service = this.validateServiceName(serviceName);

		String serverKey = serviceName + "_" + socketAddress.getHostName() + "/" + socketAddress.getPort();
		// controls that server not has been registered before for specific service
		this.getServerByKeyAndValidateNotRegistered(serverKey);

		int maxSessions = (Integer) request.getAttribute(SCMPHeaderAttributeKey.MAX_SESSIONS);
		int maxConnections = (Integer) request.getAttribute(SCMPHeaderAttributeKey.MAX_CONNECTIONS);
		int portNr = (Integer) request.getAttribute(SCMPHeaderAttributeKey.PORT_NR);
		boolean immediateConnect = (Boolean) request.getAttribute(SCMPHeaderAttributeKey.IMMEDIATE_CONNECT);
		int keepAliveInterval = (Integer) request.getAttribute(SCMPHeaderAttributeKey.KEEP_ALIVE_INTERVAL);
		// create new server
		Server server = new Server(socketAddress, serviceName, portNr, maxSessions, maxConnections, keepAliveInterval);
		try {
			if (immediateConnect) {
				// server connections get connected immediately
				server.immediateConnect();
			}
		} catch (Exception ex) {
			logger.error("run", ex);
			HasFaultResponseException communicationException = new SCMPCommunicationException(
					SCMPError.CONNECTION_EXCEPTION, "immediate connect failed for server key " + serverKey);
			communicationException.setMessageType(getKey());
			throw communicationException;
		}
		// add server to service
		service.addServer(server);
		// add service to server
		server.setService(service);
		// add server to server registry
		this.serverRegistry.addServer(serverKey, server);

		SCMPMessage scmpReply = new SCMPMessage();
		scmpReply.setIsReply(true);
		scmpReply.setMessageType(getKey());
		scmpReply.setHeader(SCMPHeaderAttributeKey.SERVICE_NAME, serviceName);
		scmpReply.setHeader(SCMPHeaderAttributeKey.LOCAL_DATE_TIME, DateTimeUtility.getCurrentTimeZoneMillis());
		response.setSCMP(scmpReply);
	}

	/**
	 * Validate server not registered.
	 * 
	 * @param server
	 *            the server
	 * @throws SCMPCommunicationException
	 *             the SCMP communication exception
	 */
	private Server getServerByKeyAndValidateNotRegistered(String key) throws SCMPCommandException {
		Server server = this.serverRegistry.getServer(key);
		if (server != null) {
			// server registered two times for this service
			SCMPCommandException cmdExc = new SCMPCommandException(SCMPError.SERVER_ALREADY_REGISTERED, "server key "
					+ key);
			cmdExc.setMessageType(getKey());
			throw cmdExc;
		}
		return server;
	}

	/**
	 * The Class RegisterServerCommandValidator.
	 */
	private class RegisterServerCommandValidator implements ICommandValidator {

		/** {@inheritDoc} */
		@Override
		public void validate(IRequest request) throws Exception {
			SCMPMessage message = request.getMessage();
			try {
				// serviceName
				String serviceName = (String) message.getServiceName();
				if (serviceName == null || serviceName.equals("")) {
					throw new SCMPValidatorException(SCMPError.HV_WRONG_SERVICE_NAME, "serviceName must be set");
				}
				// maxSessions - validate with lower limit 1
				String maxSessions = (String) message.getHeader(SCMPHeaderAttributeKey.MAX_SESSIONS);
				int maxSessionsInt = ValidatorUtility.validateInt(1, maxSessions, SCMPError.HV_WRONG_MAX_SESSIONS);
				request.setAttribute(SCMPHeaderAttributeKey.MAX_SESSIONS, maxSessionsInt);
				// maxConnections - validate with lower limit 1
				String maxConnections = (String) message.getHeader(SCMPHeaderAttributeKey.MAX_CONNECTIONS);
				int maxConnectionsInt = ValidatorUtility.validateInt(1, maxConnections,
						SCMPError.HV_WRONG_MAX_CONNECTIONS);
				request.setAttribute(SCMPHeaderAttributeKey.MAX_CONNECTIONS, maxConnectionsInt);
				// immmediateConnect
				boolean immediateConnect = message.getHeaderFlag(SCMPHeaderAttributeKey.IMMEDIATE_CONNECT);
				request.setAttribute(SCMPHeaderAttributeKey.IMMEDIATE_CONNECT, immediateConnect);
				// portNr - portNr >= 0 && portNr <= 0xFFFF
				String portNr = (String) message.getHeader(SCMPHeaderAttributeKey.PORT_NR);
				int portNrInt = ValidatorUtility.validateInt(0, portNr, 0xFFFF, SCMPError.HV_WRONG_PORTNR);
				request.setAttribute(SCMPHeaderAttributeKey.PORT_NR, portNrInt);
				// scVersion
				String scVersion = message.getHeader(SCMPHeaderAttributeKey.SC_VERSION);
				SCMPMessage.SC_VERSION.isSupported(scVersion);
				// localDateTime
				Date localDateTime = ValidatorUtility.validateLocalDateTime(message
						.getHeader(SCMPHeaderAttributeKey.LOCAL_DATE_TIME));
				request.setAttribute(SCMPHeaderAttributeKey.LOCAL_DATE_TIME, localDateTime);
				// keepAliveInterval
				String kpi = message.getHeader(SCMPHeaderAttributeKey.KEEP_ALIVE_INTERVAL);
				int keepAliveInterval = ValidatorUtility.validateInt(0, kpi, 3600,
						SCMPError.HV_WRONG_KEEPALIVE_INTERVAL);
				request.setAttribute(SCMPHeaderAttributeKey.KEEP_ALIVE_INTERVAL, keepAliveInterval);
			} catch (HasFaultResponseException ex) {
				// needs to set message type at this point
				ex.setMessageType(getKey());
				throw ex;
			} catch (Throwable th) {
				logger.error("validate", th);
				SCMPValidatorException validatorException = new SCMPValidatorException();
				validatorException.setMessageType(getKey());
				throw validatorException;
			}
		}
	}
}
