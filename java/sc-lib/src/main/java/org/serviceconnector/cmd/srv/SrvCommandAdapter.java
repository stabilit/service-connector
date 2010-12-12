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
package org.serviceconnector.cmd.srv;

import org.apache.log4j.Logger;
import org.serviceconnector.api.srv.SrvPublishService;
import org.serviceconnector.api.srv.SrvService;
import org.serviceconnector.api.srv.SrvServiceRegistry;
import org.serviceconnector.api.srv.SrvSessionService;
import org.serviceconnector.cmd.ICommand;
import org.serviceconnector.cmd.SCMPCommandException;
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.net.res.IResponder;
import org.serviceconnector.net.res.SCMPSessionCompositeRegistry;
import org.serviceconnector.scmp.IRequest;
import org.serviceconnector.scmp.IResponse;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.scmp.SCMPMsgType;

/**
 * The Class SrvCommandAdapter. Command adapter for every kind of command on server.
 */
public abstract class SrvCommandAdapter implements ICommand {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(SrvCommandAdapter.class);

	/** The session composite registry. */
	protected static SCMPSessionCompositeRegistry sessionCompositeRegistry = AppContext.getSCMPSessionCompositeRegistry();

	/** {@inheritDoc} */
	@Override
	public abstract SCMPMsgType getKey();

	/** {@inheritDoc} */
	@Override
	public abstract void run(IRequest request, IResponse response) throws Exception;

	/** {@inheritDoc} */
	@Override
	public void validate(IRequest request) throws Exception {
		throw new SCMPValidatorException(SCMPError.HV_ERROR, "no validator implemented");
	}

	/** {@inheritDoc} */
	@Override
	public boolean isAsynchronous() {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isPassThroughPartMsg() {
		return false;
	}

	/**
	 * Gets the srv session service by service name.
	 * 
	 * @param serviceName
	 *            the service name
	 * @return the srv session service by service name
	 * @throws SCMPCommandException
	 *             the sCMP command exception
	 */
	protected SrvSessionService getSrvSessionServiceByServiceName(String serviceName) throws SCMPCommandException {
		SrvServiceRegistry srvServiceRegistry = AppContext.getSrvServiceRegistry();
		IResponder responder = AppContext.getResponderRegistry().getCurrentResponder();
		int listenerPort = responder.getResponderConfig().getPort();
		SrvService srvService = srvServiceRegistry.getSrvService(serviceName + "_" + listenerPort);

		if (srvService == null || (srvService instanceof SrvSessionService == false)) {
			// incoming srvService not found
			logger.warn("command error: service " + serviceName + " not found in service registry");
			SCMPCommandException scmpCommandException = new SCMPCommandException(SCMPError.NOT_FOUND, serviceName
					+ " not found in service registry");
			scmpCommandException.setMessageType(this.getKey());
			throw scmpCommandException;
		}
		return (SrvSessionService) srvService;
	}

	/**
	 * Gets the srv publish service by service name.
	 * 
	 * @param serviceName
	 *            the service name
	 * @return the srv publish service by service name
	 * @throws SCMPCommandException
	 *             the sCMP command exception
	 */
	protected SrvPublishService getSrvPublishServiceByServiceName(String serviceName) throws SCMPCommandException {
		SrvServiceRegistry srvServiceRegistry = AppContext.getSrvServiceRegistry();
		IResponder responder = AppContext.getResponderRegistry().getCurrentResponder();
		int listenerPort = responder.getResponderConfig().getPort();
		SrvService srvService = srvServiceRegistry.getSrvService(serviceName + "_" + listenerPort);

		if (srvService == null || (srvService instanceof SrvPublishService == false)) {
			// incoming srvService not found
			logger.warn("command error: service " + serviceName + " not found in service registry");
			SCMPCommandException scmpCommandException = new SCMPCommandException(SCMPError.NOT_FOUND, serviceName
					+ " not found in service registry");
			scmpCommandException.setMessageType(this.getKey());
			throw scmpCommandException;
		}
		return (SrvPublishService) srvService;
	}
}
