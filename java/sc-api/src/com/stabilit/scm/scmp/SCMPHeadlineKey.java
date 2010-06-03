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
 * The Enum SCMPHeadlineKey. Defines possible headline key in SCMP.
 * 
 * @author JTraber
 */
public enum SCMPHeadlineKey {

	/** The UNDEF, UNDEFINED. */
	UNDEF,
	/** The REQ, REQUEST. */
	REQ,
	/** The RES, RESPONSE. */
	RES,
	/** The EXC, EXCEPTION. */
	EXC,
	/** The PRQ, PART REQUEST. */
	PRQ,
	/** The PRS, PART RESPONSE. */
	PRS;

	/**
	 * Gets the msg header key by string.
	 * 
	 * @param headerKey
	 *            the header key
	 * @return the msg header key
	 */
	public static SCMPHeadlineKey getMsgHeaderKey(String headerKey) {
		return SCMPHeadlineKey.valueOf(headerKey);
	}

	/**
	 * Gets the key by headline (byte buffer).
	 * 
	 * @param b
	 *            the b
	 * @return the key by headline
	 */
	public static SCMPHeadlineKey getKeyByHeadline(byte[] b) {
		if (b == null) {
			return UNDEF;
		}
		if (b.length < 3) {
			return UNDEF;
		}
		if (b[0] == 'R' && b[1] == 'E') {
			if (b[2] == 'Q') {
				return REQ;
			}
			if (b[2] == 'S') {
				return RES;
			}
			return UNDEF;
		}
		if (b[0] == 'P' && b[1] == 'R') {
			if (b[2] == 'Q') {
				return PRQ;
			}
			if (b[2] == 'S') {
				return PRS;
			}
			return UNDEF;
		}
		if (b[0] == 'E' && b[1] == 'X' && b[2] == 'C') {
			return EXC;
		}
		return UNDEF;
	}

	/**
	 * Gets the key by headline (string).
	 * 
	 * @param string
	 *            the string
	 * @return the key by headline
	 */
	public static SCMPHeadlineKey getKeyByHeadline(String string) {
		byte[] b = string.getBytes();
		return getKeyByHeadline(b);
	}
}
