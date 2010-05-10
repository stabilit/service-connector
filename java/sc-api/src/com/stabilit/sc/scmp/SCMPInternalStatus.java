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
package com.stabilit.sc.scmp;

/**
 * The Enum SCMPInternalStatus. Defines possible internal states in SCMP. Internal status reflects state of
 * communication. Is used when large messages are requested/responded or group calls are made.
 * 
 * @author JTraber
 */
public enum SCMPInternalStatus {

	/** The NONE. */
	NONE,
	/** The PRQ. */
	PRQ,
	/** The REQ. */
	REQ,
	/** The FAILED. */
	FAILED,
	/** The GROUP. */
	GROUP;

	/**
	 * Gets the internal status.
	 * 
	 * @param headerKey
	 *            the header key
	 * @return the internal status
	 */
	public static SCMPInternalStatus getInternalStatus(SCMPHeadlineKey headerKey) {
		switch (headerKey) {
		case PRQ:
			return SCMPInternalStatus.PRQ;
		case REQ:
			return SCMPInternalStatus.REQ;
		}
		return SCMPInternalStatus.NONE;
	}
}
