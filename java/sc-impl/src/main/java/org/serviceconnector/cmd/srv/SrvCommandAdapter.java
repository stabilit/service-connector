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
import org.serviceconnector.cmd.ICommand;
import org.serviceconnector.cmd.ICommandValidator;
import org.serviceconnector.cmd.SCMPCommandException;
import org.serviceconnector.factory.IFactoryable;
import org.serviceconnector.net.res.SCMPSessionCompositeRegistry;
import org.serviceconnector.scmp.IRequest;
import org.serviceconnector.scmp.IResponse;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.scmp.SCMPMsgType;
import org.serviceconnector.srv.SrvService;
import org.serviceconnector.srv.SrvServiceRegistry;


/**
 * The Class SrvCommandAdapter. Command adapter for every kind of command on server.
 */
public abstract class SrvCommandAdapter implements ICommand {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(SrvCommandAdapter.class);
	
	/** The command validator. */
	protected ICommandValidator commandValidator;
	/** The session composite registry. */
	protected SCMPSessionCompositeRegistry sessionCompositeRegistry;

	/**
	 * Instantiates a new SrvCommandAdapter.
	 */
	public SrvCommandAdapter() {
		this.sessionCompositeRegistry = SCMPSessionCompositeRegistry.getCurrentInstance();
	}

	/** {@inheritDoc} */
	@Override
	public ICommandValidator getCommandValidator() {
		return this.commandValidator;
	}

	/** {@inheritDoc} */
	@Override
	public abstract SCMPMsgType getKey();

	/** {@inheritDoc} */
	@Override
	public abstract void run(IRequest request, IResponse response) throws Exception;

	/** {@inheritDoc} */
	@Override
	public boolean isAsynchronous() {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public IFactoryable newInstance() {
		return this;
	}

	/**
	 * Gets the server service by service name.
	 * 
	 * @param serviceName
	 *            the service name
	 * @return the server service by service name
	 * @throws SCMPCommandException
	 *             the sCMP command exception
	 */
	protected SrvService getSrvServiceByServiceName(String serviceName) throws SCMPCommandException {
		SrvServiceRegistry srvServiceRegistry = SrvServiceRegistry.getCurrentInstance();
		SrvService srvService = srvServiceRegistry.getSrvService(serviceName);

		if (srvService == null) {
			// incoming srvService not found
			logger.warn("command error: no srvService found for serviceName :" + serviceName);
			SCMPCommandException scmpCommandException = new SCMPCommandException(SCMPError.NOT_FOUND,
					"no service found for " + serviceName);
			scmpCommandException.setMessageType(this.getKey());
			throw scmpCommandException;
		}
		return srvService;
	}
}
