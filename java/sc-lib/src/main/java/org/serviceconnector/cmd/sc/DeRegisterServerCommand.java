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
import org.serviceconnector.net.res.IResponderCallback;
import org.serviceconnector.scmp.HasFaultResponseException;
import org.serviceconnector.scmp.IRequest;
import org.serviceconnector.scmp.IResponse;
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

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(DeRegisterServerCommand.class);

	/**
	 * Instantiates a new DeRegisterServerCommand.
	 */
	public DeRegisterServerCommand() {
	}

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

		String serverKey = serviceName + "_" + socketAddress.getHostName() + "/" + socketAddress.getPort();
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
			String serviceName = (String) message.getServiceName();
			ValidatorUtility.validateStringLength(1, serviceName, 32, SCMPError.HV_WRONG_SERVICE_NAME);
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

	/**
	 * Gets the stateful server by name.
	 * 
	 * @param key
	 *            the key
	 * @return the stateful server by name
	 * @throws SCMPCommandException
	 *             the sCMP command exception
	 */
	public StatefulServer getStatefulServerByName(String key) throws SCMPCommandException {
		Server server = this.serverRegistry.getServer(key);

		if (server == null || (server.getType().equals(ServerType.STATEFUL_SERVER) == false)) {
			// no available server for this service
			SCMPCommandException scmpCommandException = new SCMPCommandException(SCMPError.NOT_FOUND, "server not registered, key="
					+ key);
			scmpCommandException.setMessageType(getKey());
			throw scmpCommandException;
		}
		return (StatefulServer) server;
	}
}