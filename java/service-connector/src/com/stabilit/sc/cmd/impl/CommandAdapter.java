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
package com.stabilit.sc.cmd.impl;

import com.stabilit.sc.listener.LoggerListenerSupport;
import com.stabilit.sc.registry.SessionRegistry;
import com.stabilit.sc.scmp.SCMPErrorCode;
import com.stabilit.sc.scmp.Session;
import com.stabilit.sc.srv.cmd.ICommand;
import com.stabilit.sc.srv.cmd.ICommandValidator;
import com.stabilit.sc.srv.cmd.NullCommandValidator;
import com.stabilit.sc.srv.cmd.SCMPCommandException;

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

	protected Session getSessionById(String sessionId) throws SCMPCommandException {
		SessionRegistry sessionRegistry = SessionRegistry.getCurrentInstance();
		Session session = sessionRegistry.get(sessionId);

		if (session == null) {
			// incoming session not found
			if (LoggerListenerSupport.getInstance().isWarn()) {
				LoggerListenerSupport.getInstance().fireWarn(this,
						"command error: no session found for id :" + sessionId);
			}
			SCMPCommandException scmpCommandException = new SCMPCommandException(SCMPErrorCode.NO_SESSION);
			scmpCommandException.setMessageType(getKey().getResponseName());
			throw scmpCommandException;
		}
		return session;
	}

	/*
	 * (non-Javadoc)
	 * @see com.stabilit.sc.srv.cmd.ICommand#getCommandValidator()
	 */
	@Override
	public ICommandValidator getCommandValidator() {
		return commandValidator;
	}

	/*
	 * (non-Javadoc)
	 * @see com.stabilit.sc.srv.cmd.ICommand#getRequestKeyName()
	 */
	@Override
	public String getRequestKeyName() {
		return this.getKey().getRequestName();
	}

	/*
	 * (non-Javadoc)
	 * @see com.stabilit.sc.srv.cmd.ICommand#getResponseKeyName()
	 */
	@Override
	public String getResponseKeyName() {
		return this.getKey().getResponseName();
	}
}
