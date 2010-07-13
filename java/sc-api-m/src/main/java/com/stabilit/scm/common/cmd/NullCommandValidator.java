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
package com.stabilit.scm.common.cmd;


import com.stabilit.scm.common.scmp.IRequest;

/**
 * The Class NullCommandValidator. Prevents null pointer exception when command does not implement validation.
 * Throws more specific exception (ValidatorException).
 * 
 * @author JTraber
 */
public final class NullCommandValidator implements ICommandValidator {

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

	/**
	 * Instantiates a new null command validator.
	 */
	private NullCommandValidator() {
	}

	/** {@inheritDoc} */
	@Override
	public void validate(IRequest request) throws Exception {
		throw new SCMPValidatorException("no validator implemented");
	}
}
