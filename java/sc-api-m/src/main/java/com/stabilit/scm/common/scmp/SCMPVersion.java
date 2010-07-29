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
package com.stabilit.scm.common.scmp;

import com.stabilit.scm.common.cmd.SCMPValidatorException;

/**
 * The Enum SCMPVersion. Responsible to provide SCMPVersion and compatibility checks.
 * 
 * @author JTraber
 */
public enum SCMPVersion {

	/** The current version */
	CURRENT(1, 0),
	/** The version to make tests - DO NOT CHANGE ! */
	TEST(3, 2);

	/** The release. */
	private int release;
	/** The version. */
	private int version;

	/**
	 * Instantiates a new SCMP version.
	 * 
	 * @param release
	 *            the release number
	 * @param version
	 *            the version number
	 */
	private SCMPVersion(int release, int version) {
		this.version = version;
		this.release = release;
	}

	/**
	 * Checks if is supported.
	 * 
	 * @param scmpVersion
	 *            the scmp version to be checked
	 * @throws SCMPValidatorException
	 *             the sCMP validator exception
	 */
	public void isSupported(String text) throws SCMPValidatorException {

		if (text.matches("\\d\\.\\d") == false) {
			throw new SCMPValidatorException("invalid scmp version format [" + text + "]");
		}
		String[] splitted = text.split("\\.");
		if (splitted.length != 2) {
			throw new SCMPValidatorException("invalid scmp version [" + text + "]");
		}
		int release = Integer.parseInt(splitted[0]);
		if (this.release != release) {
			throw new SCMPValidatorException("invalid scmp release nr. [" + text + "]");
		}
		int version = Integer.parseInt(splitted[1]);
		if (this.version < version) {
			throw new SCMPValidatorException("invalid scmp version nr. [" + text + "]");
		}
		return;
	}

	@Override
	public String toString() {
		return release + "." + version;
	}
}
