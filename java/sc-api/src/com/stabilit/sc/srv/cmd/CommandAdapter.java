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
package com.stabilit.sc.srv.cmd;


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
		commandValidator = NullCommandValidator.newInstance();  // www.refactoring.com Introduce NULL Object
	}

	/* (non-Javadoc)
	 * @see com.stabilit.sc.srv.cmd.ICommand#getCommandValidator()
	 */
	@Override
	public ICommandValidator getCommandValidator() {
		return commandValidator;
	}
		
	/* (non-Javadoc)
	 * @see com.stabilit.sc.srv.cmd.ICommand#getRequestKeyName()
	 */
	@Override
	public String getRequestKeyName() {
		return this.getKey().getRequestName();
	}
	
	/* (non-Javadoc)
	 * @see com.stabilit.sc.srv.cmd.ICommand#getResponseKeyName()
	 */
	@Override
	public String getResponseKeyName() {
		return this.getKey().getResponseName();
	}
}
