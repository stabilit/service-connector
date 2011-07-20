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
import org.serviceconnector.Constants;
import org.serviceconnector.cmd.SCMPCommandException;
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.net.req.IRequest;
import org.serviceconnector.net.res.IResponderCallback;
import org.serviceconnector.net.res.IResponse;
import org.serviceconnector.scmp.HasFaultResponseException;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.scmp.SCMPMsgType;
import org.serviceconnector.server.Server;
import org.serviceconnector.server.ServerType;
import org.serviceconnector.server.StatefulServer;
import org.serviceconnector.util.ValidatorUtility;

/**
 * The Class DeRegisterServerCommand. Responsible for validation and execution of deregister command. Used to deregisters server from
 * SC service. Server will be removed from server registry.
 * 
 * @author JTraber
 */
public class DeRegisterServerCommand extends CommandAdapter {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = Logger.getLogger(DeRegisterServerCommand.class);

	/** {@inheritDoc} */
	@Override
	public SCMPMsgType getKey() {
		return SCMPMsgType.DEREGISTER_SERVER;
	}

	/** {@inheritDoc} */
	@Override
	public void run(IRequest request, IResponse response, IResponderCallback responderCallback) throws Exception {
		SCMPMessage message = request.getMessage();
		String serviceName = message.getServiceName();
		InetSocketAddress socketAddress = request.getRemoteSocketAddress();

		String serverKey = serviceName + "_" + socketAddress.getHostName() + Constants.SLASH + socketAddress.getPort();
		// looks up server & validate server is registered
		StatefulServer server = this.getStatefulServerByName(serverKey);
		// deregister server from service
		server.getService().removeServer(server);

		server.abortSessionsAndDestroy("deregister of server");
		this.serverRegistry.removeServer(serverKey);

		SCMPMessage scmpReply = new SCMPMessage();
		scmpReply.setIsReply(true);
		scmpReply.setMessageType(getKey());
		scmpReply.setHeader(SCMPHeaderAttributeKey.SERVICE_NAME, serviceName);
		response.setSCMP(scmpReply);
		responderCallback.responseCallback(request, response);
	}

	/** {@inheritDoc} */
	@Override
	public void validate(IRequest request) throws Exception {
		SCMPMessage message = request.getMessage();

		try {
			// serviceName mandatory
			String serviceName = message.getServiceName();
			ValidatorUtility.validateStringLengthTrim(1, serviceName, Constants.MAX_LENGTH_SERVICENAME, SCMPError.HV_WRONG_SERVICE_NAME);
		} catch (HasFaultResponseException ex) {
			// needs to set message type at this point
			ex.setMessageType(getKey());
			throw ex;
		} catch (Throwable th) {
			LOGGER.error("validation error", th);
			SCMPValidatorException validatorException = new SCMPValidatorException();
			validatorException.setMessageType(getKey());
			throw validatorException;
		}
	}

	/**
	 * Gets the stateful server by name.
	 * 
	 * @param key
	 *            the key
	 * @return the stateful server by name
	 * @throws SCMPCommandException
	 *             the SCMP command exception
	 */
	public StatefulServer getStatefulServerByName(String key) throws SCMPCommandException {
		Server server = this.serverRegistry.getServer(key);
		if (server == null) {
			// server not found in registry
			SCMPCommandException scmpCommandException = new SCMPCommandException(SCMPError.SERVER_NOT_FOUND, key);
			scmpCommandException.setMessageType(getKey());
			throw scmpCommandException;
		}
		if (server.getType().equals(ServerType.STATEFUL_SERVER) == false) {
			// server is wrong type
			SCMPCommandException scmpCommandException = new SCMPCommandException(SCMPError.V_WRONG_SERVER_TYPE, key);
			scmpCommandException.setMessageType(getKey());
			throw scmpCommandException;
		}
		return (StatefulServer) server;
	}
}