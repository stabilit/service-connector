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
package org.serviceconnector.web.cmd;

import org.apache.log4j.Logger;
import org.serviceconnector.web.IWebRequest;

/**
 * The Class NullCommandValidator. Prevents null pointer exception when command
 * does not implement validation. Throws more specific exception
 * (ValidatorException).
 * 
 * @author JTraber
 */
public final class NullWebCommandValidator implements IWebCommandValidator {

	/** The Constant logger. */
	protected static final Logger logger = Logger.getLogger(NullWebCommandValidator.class);

	/** The null command validator. */
	private static IWebCommandValidator nullCommandValidator = new NullWebCommandValidator();

	/**
	 * New instance.
	 * 
	 * @return the command validator
	 */
	public static IWebCommandValidator newInstance() {
		return nullCommandValidator;
	}

	/** {@inheritDoc} */
	@Override
	public void validate(IWebRequest request) throws Exception {
	}
}
