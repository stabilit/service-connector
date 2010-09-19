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
package org.serviceconnector.cmd;

import org.apache.log4j.Logger;
import org.serviceconnector.scmp.IRequest;
import org.serviceconnector.scmp.SCMPError;


/**
 * The Class NullCommandValidator. Prevents null pointer exception when command does not implement validation. Throws
 * more specific exception (ValidatorException).
 * 
 * @author JTraber
 */
public final class NullCommandValidator implements ICommandValidator {

	/** The Constant logger. */
	protected static final Logger logger = Logger.getLogger(NullCommandValidator.class);
	
	/** The null command validator. */
	private static ICommandValidator nullCommandValidator = new NullCommandValidator();

	/**
	 * New instance.
	 * 
	 * @return the command validator
	 */
	public static ICommandValidator newInstance() {
		return nullCommandValidator;
	}

	/** {@inheritDoc} */
	@Override
	public void validate(IRequest request) throws Exception {
		throw new SCMPValidatorException(SCMPError.HV_ERROR, "no validator implemented - no allowed.");
	}
}
