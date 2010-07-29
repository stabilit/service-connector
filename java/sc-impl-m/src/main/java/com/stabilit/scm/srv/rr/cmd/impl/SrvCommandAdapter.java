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
package com.stabilit.scm.srv.rr.cmd.impl;

import com.stabilit.scm.common.cmd.ICommand;
import com.stabilit.scm.common.cmd.ICommandValidator;
import com.stabilit.scm.common.cmd.SCMPCommandException;
import com.stabilit.scm.common.factory.IFactoryable;
import com.stabilit.scm.common.listener.LoggerPoint;
import com.stabilit.scm.common.net.res.SCMPSessionCompositeRegistry;
import com.stabilit.scm.common.scmp.IRequest;
import com.stabilit.scm.common.scmp.IResponse;
import com.stabilit.scm.common.scmp.SCMPError;
import com.stabilit.scm.common.scmp.SCMPMsgType;
import com.stabilit.scm.srv.SrvService;
import com.stabilit.scm.srv.SrvServiceRegistry;

/**
 * The Class SrvCommandAdapter.
 */
public abstract class SrvCommandAdapter implements ICommand {

	/** The command validator. */
	protected ICommandValidator commandValidator;
	protected SCMPSessionCompositeRegistry sessionCompositeRegistry;
	
	public SrvCommandAdapter() {
		this.sessionCompositeRegistry = SCMPSessionCompositeRegistry.getCurrentInstance();
	}
	
	@Override
	public ICommandValidator getCommandValidator() {
		return this.commandValidator;
	}

	@Override
	public abstract SCMPMsgType getKey();

	@Override
	public abstract void run(IRequest request, IResponse response) throws Exception;

	protected SrvService getSrvServiceByServiceName(String serviceName) throws SCMPCommandException {
		SrvServiceRegistry srvServiceRegistry = SrvServiceRegistry.getCurrentInstance();
		SrvService srvService = srvServiceRegistry.getSrvService(serviceName);

		if (srvService == null) {
			// incoming srvService not found
			if (LoggerPoint.getInstance().isWarn()) {
				LoggerPoint.getInstance().fireWarn(this,
						"command error: no srvService found for serviceName :" + serviceName);
			}
			SCMPCommandException scmpCommandException = new SCMPCommandException(SCMPError.NOT_FOUND);
			scmpCommandException.setMessageType(this.getKey());
			throw scmpCommandException;
		}
		return srvService;
	}

	@Override
	public boolean isAsynchronous() {
		return false;
	}

	@Override
	public IFactoryable newInstance() {
		return this;
	}
}
