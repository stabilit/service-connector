/*-----------------------------------------------------------------------------*
 *                                                                             *
 *       Copyright � 2010 STABILIT Informatik AG, Switzerland                  *
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
package com.stabilit.scm.common;

import java.text.DecimalFormat;

import com.stabilit.scm.common.cmd.SCMPValidatorException;

/**
 * The Enum SCVersion. Responsible to provide SCVersion and compatibility checks.
 * 
 * @author JTraber
 */
public enum SCVersion {

	/** The current version. */
	CURRENT(1, 0, 0),
	/** The test version - DO NOT CHANGE ! */
	TEST(3, 2, 5);
	
	private static final DecimalFormat df = new DecimalFormat("000");
	
	/** The release. */
	private int release; // e.g : 1
	/** The version. */
	private int version; // e.g : 0
	/** The revision. */
	private int revision; // e.g : 023 -> all together 1.0-023

	/**
	 * Instantiates a new SCVersion.
	 * 
	 * @param release
	 *            the release
	 * @param version
	 *            the version
	 * @param revision
	 *            the revision
	 */
	private SCVersion(int release, int version, int revision) {
		this.release = release;
		this.version = version;
		this.revision = revision;
	}

	/**
	 * Checks if is supported.
	 *
	 * @param text the text in format 999.999-999 e.g. 1.5-003
	 * @throws SCMPValidatorException the sCMP validator exception
	 */
	public void isSupported(String text) throws SCMPValidatorException {
		if(text.matches("\\d*\\.\\d*-\\d{3}") == false) {
			throw new SCMPValidatorException("invalid sc version format [" + text + "]");
		}
		String[] splitted = text.split("\\.|-");
		if (splitted.length != 3) {
			throw new SCMPValidatorException("invalid sc version [" + text + "]");
		}
		int release = Integer.parseInt(splitted[0]);
		if (this.release != release) {
			throw new SCMPValidatorException("invalid sc release nr. [" + text + "]");
		}
		int version = Integer.parseInt(splitted[1]);
		if (this.version < version) {
			throw new SCMPValidatorException("invalid sc version nr. [" + text + "]");
		}
		int revision = Integer.parseInt(splitted[2]);
		if ((this.version == version) && (this.revision < revision)) {
			throw new SCMPValidatorException("invalid sc revision nr. [" + text + "]");
		}
		return;
	}
	
	/**
	 * To string.
	 * 
	 * @return the string
	 */
	@Override
	public String toString() {
		return release + "." + version + "-" + df.format(revision);
	}
}
