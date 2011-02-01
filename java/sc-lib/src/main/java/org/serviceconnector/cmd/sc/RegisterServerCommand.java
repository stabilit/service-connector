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

import org.apache.log4j.Logger;
import org.serviceconnector.cmd.SCMPCommandException;
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.conf.ListenerConfiguration;
import org.serviceconnector.conf.RemoteNodeConfiguration;
import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.net.SCMPCommunicationException;
import org.serviceconnector.net.res.IResponder;
import org.serviceconnector.net.res.IResponderCallback;
import org.serviceconnector.net.res.ResponderRegistry;
import org.serviceconnector.scmp.HasFaultResponseException;
import org.serviceconnector.scmp.IRequest;
import org.serviceconnector.scmp.IResponse;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.scmp.SCMPMsgType;
import org.serviceconnector.server.Server;
import org.serviceconnector.server.StatefulServer;
import org.serviceconnector.service.StatefulService;
import org.serviceconnector.util.DateTimeUtility;
import org.serviceconnector.util.ValidatorUtility;

/**
 * The Class RegisterServerCommand. Responsible for validation and execution of register command. Used to register backend server in
 * SC. Backend server will be registered in server registry of SC.
 * 
 * @author JTraber
 */
public class RegisterServerCommand extends CommandAdapter {

	/** The Constant logger. */
	private final static Logger logger = Logger.getLogger(RegisterServerCommand.class);

	/**
	 * Instantiates a new RegisterServerCommand.
	 */
	public RegisterServerCommand() {
	}

	/** {@inheritDoc} */
	@Override
	public SCMPMsgType getKey() {
		return SCMPMsgType.REGISTER_SERVER;
	}

	/** {@inheritDoc} */
	@Override
	public void run(IRequest request, IResponse response, IResponderCallback responderCallback) throws Exception {
		InetSocketAddress socketAddress = request.getRemoteSocketAddress();

		SCMPMessage message = request.getMessage();
		String serviceName = message.getServiceName();
		// lookup service and checks properness
		StatefulService service = this.getStatefulService(serviceName);

		String serverKey = serviceName + "_" + socketAddress.getHostName() + "/" + socketAddress.getPort();
		// controls that server not has been registered before for specific service
		this.getServerByKeyAndValidateNotRegistered(serverKey);

		int maxSessions = message.getHeaderInt(SCMPHeaderAttributeKey.MAX_SESSIONS);
		int maxConnections = message.getHeaderInt(SCMPHeaderAttributeKey.MAX_CONNECTIONS);
		int portNr = message.getHeaderInt(SCMPHeaderAttributeKey.PORT_NR);
		boolean immediateConnect = message.getHeaderFlag(SCMPHeaderAttributeKey.IMMEDIATE_CONNECT);
		int keepAliveInterval = message.getHeaderInt(SCMPHeaderAttributeKey.KEEP_ALIVE_INTERVAL);

		ResponderRegistry responderRegistry = AppContext.getResponderRegistry();
		IResponder responder = responderRegistry.getCurrentResponder();
		ListenerConfiguration listenerConfig = responder.getListenerConfig();
		String connectionType = listenerConfig.getConnectionType();

		RemoteNodeConfiguration remoteNodeConfiguration = new RemoteNodeConfiguration(serverKey, socketAddress.getHostName(),
				portNr, connectionType, keepAliveInterval, maxConnections, maxSessions);
		// create new server
		StatefulServer server = new StatefulServer(remoteNodeConfiguration, serviceName, socketAddress);
		try {
			if (immediateConnect) {
				// server connections get connected immediately
				server.immediateConnect();
			}
		} catch (Exception ex) {
			logger.error("immediate connect", ex);
			HasFaultResponseException communicationException = new SCMPCommunicationException(SCMPError.CONNECTION_EXCEPTION,
					"immediate connect to server=" + serverKey);
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
		responderCallback.responseCallback(request, response);
	}

	/**
	 * Validate server not registered.
	 * 
	 * @param key
	 *            the key
	 * @return the server by key and validate not registered
	 * @throws SCMPCommandException
	 *             the SCMP command exception
	 */
	private Server getServerByKeyAndValidateNotRegistered(String key) throws SCMPCommandException {
		Server server = this.serverRegistry.getServer(key);
		if (server != null) {
			// server registered two times for this service
			SCMPCommandException cmdExc = new SCMPCommandException(SCMPError.SERVER_ALREADY_REGISTERED, "server=" + key);
			cmdExc.setMessageType(getKey());
			throw cmdExc;
		}
		return server;
	}

	/** {@inheritDoc} */
	@Override
	public void validate(IRequest request) throws Exception {
		SCMPMessage message = request.getMessage();
		try {
			// serviceName mandatory
			String serviceName = (String) message.getServiceName();
			ValidatorUtility.validateStringLength(1, serviceName, 32, SCMPError.HV_WRONG_SERVICE_NAME);
			// scVersion mandatory
			String scVersion = message.getHeader(SCMPHeaderAttributeKey.SC_VERSION);
			SCMPMessage.SC_VERSION.isSupported(scVersion);
			// localDateTime mandatory
			ValidatorUtility.validateDateTime(message.getHeader(SCMPHeaderAttributeKey.LOCAL_DATE_TIME), SCMPError.HV_WRONG_LDT);
			// maxSessions - validate with lower limit 1 mandatory
			String maxSessionsValue = (String) message.getHeader(SCMPHeaderAttributeKey.MAX_SESSIONS);
			ValidatorUtility.validateInt(1, maxSessionsValue, SCMPError.HV_WRONG_MAX_SESSIONS);
			int maxSessions = Integer.parseInt(maxSessionsValue);
			// maxConnections - validate with lower limit 1 & higher limit maxSessions mandatory
			String maxConnectionsValue = (String) message.getHeader(SCMPHeaderAttributeKey.MAX_CONNECTIONS);
			ValidatorUtility.validateInt(1, maxConnectionsValue, maxSessions, SCMPError.HV_WRONG_MAX_CONNECTIONS);
			int maxConnections = Integer.parseInt(maxConnectionsValue);
			if (maxConnections == 1 && maxSessions != 1) {
				// invalid configuration
				throw new SCMPValidatorException(SCMPError.HV_WRONG_MAX_SESSIONS, "maxSessions must be 1 if maxConnections is 1");
			}
			// portNr - portNr >= 1 && portNr <= 0xFFFF mandatory
			String portNr = (String) message.getHeader(SCMPHeaderAttributeKey.PORT_NR);
			ValidatorUtility.validateInt(1, portNr, 0xFFFF, SCMPError.HV_WRONG_PORTNR);
			// keepAliveInterval mandatory
			String kpi = message.getHeader(SCMPHeaderAttributeKey.KEEP_ALIVE_INTERVAL);
			ValidatorUtility.validateInt(0, kpi, 3600, SCMPError.HV_WRONG_KEEPALIVE_INTERVAL);
		} catch (HasFaultResponseException ex) {
			// needs to set message type at this point
			ex.setMessageType(getKey());
			throw ex;
		} catch (Throwable th) {
			logger.error("validation error", th);
			SCMPValidatorException validatorException = new SCMPValidatorException();
			validatorException.setMessageType(getKey());
			throw validatorException;
		}
	}
}