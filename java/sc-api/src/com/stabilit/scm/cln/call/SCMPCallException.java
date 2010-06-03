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
package com.stabilit.sc.cln.call;

import com.stabilit.sc.scmp.SCMPFault;

/**
 * The Class SCMPCallException. Exception occurs when invoking a call fails.
 * 
 * @author JTraber
 */
public class SCMPCallException extends Exception {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -5469213266177973818L;
	
	/** The fault. */
	private SCMPFault fault;	
	
	/**
	 * Instantiates a new SCMPCallException.
	 * 
	 * @param fault the fault
	 */
	public SCMPCallException(SCMPFault fault) {
		this.fault = fault;
	}
	
	/**
	 * Instantiates a new SCMPCallException.
	 * 
	 * @param string the string
	 */
	public SCMPCallException(String string) {
		super(string);
	}

	/**
	 * Gets the fault.
	 * 
	 * @return the fault
	 */
	public SCMPFault getFault() {
		return fault;
	}
}
