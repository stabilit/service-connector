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
package org.serviceconnector.net;

import org.serviceconnector.scmp.HasFaultResponseException;
import org.serviceconnector.scmp.SCMPError;

/**
 * The Class SCMPCommunicationException. Communication on SCMP level failed.
 * 
 * @author JTraber
 */
public class SCMPCommunicationException extends HasFaultResponseException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -7198688558643060L;

	/**
	 * Instantiates a new SCMPCommunicationException.
	 * 
	 * @param error
	 *            the error
	 * @param additionalInfo
	 *            the additional info
	 */
	public SCMPCommunicationException(SCMPError error, String additionalInfo) {
		super(error, additionalInfo);
	}
}
