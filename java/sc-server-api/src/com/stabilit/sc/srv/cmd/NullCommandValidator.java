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

import com.stabilit.sc.common.scmp.IRequest;

/**
 * @author JTraber
 *
 */
public class NullCommandValidator implements ICommandValidator {

	private static ICommandValidator nullCommandValidator = new NullCommandValidator();
		
	public static ICommandValidator newInstance() {
		return nullCommandValidator;
	}

	private NullCommandValidator() {
	}
	
	@Override
	public void validate(IRequest request) throws ValidatorException {
        throw new ValidatorException("no validator implemented");
	}


}
