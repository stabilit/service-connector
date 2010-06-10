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
package com.stabilit.scm.sc.cmd.impl;

import com.stabilit.scm.common.cmd.ICommand;
import com.stabilit.scm.common.cmd.ICommandValidator;
import com.stabilit.scm.common.cmd.NullCommandValidator;
import com.stabilit.scm.common.cmd.SCMPCommandException;
import com.stabilit.scm.common.listener.LoggerPoint;
import com.stabilit.scm.common.scmp.SCMPError;
import com.stabilit.scm.sc.registry.SessionRegistry;
import com.stabilit.scm.sc.service.Session;

/**
 * The Class CommandAdapter.
 * 
 * @author JTraber
 */
public abstract class CommandAdapter implements ICommand {

	/** The command validator. */
	protected ICommandValidator commandValidator;

	/**
	 * Instantiates a new command adapter.
	 */
	public CommandAdapter() {
		commandValidator = NullCommandValidator.newInstance(); // www.refactoring.com Introduce NULL Object
	}

	/**
	 * Gets the session by id.
	 * 
	 * @param sessionId
	 *            the session id
	 * @return the session by id
	 * @throws SCMPCommandException
	 *             occurs when session is not in registry, invalid session id
	 */
	protected Session getSessionById(String sessionId) throws SCMPCommandException {
		SessionRegistry sessionRegistry = SessionRegistry.getCurrentInstance();
		Session session = sessionRegistry.getSession(sessionId);

		if (session == null) {
			// incoming session not found
			if (LoggerPoint.getInstance().isWarn()) {
				LoggerPoint.getInstance().fireWarn(this, "command error: no session found for id :" + sessionId);
			}
			SCMPCommandException scmpCommandException = new SCMPCommandException(SCMPError.NO_SESSION);
			scmpCommandException.setMessageType(getKey().getResponseName());
			throw scmpCommandException;
		}
		return session;
	}

	/** {@inheritDoc} */
	@Override
	public ICommandValidator getCommandValidator() {
		return commandValidator;
	}

	/** {@inheritDoc} */
	@Override
	public String getRequestKeyName() {
		return this.getKey().getRequestName();
	}

	/** {@inheritDoc} */
	@Override
	public String getResponseKeyName() {
		return this.getKey().getResponseName();
	}
}
