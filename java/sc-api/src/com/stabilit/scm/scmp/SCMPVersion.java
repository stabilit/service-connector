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
 * The Enum SCMPVersion. Responsible to provide SCMPVersion and compatibility checks.
 * 
 * @author JTraber
 */
public enum SCMPVersion {

	/** The UNDEFINED. */
	UNDEFINED("0.0", 0, 0, 0),
	/** The ONE. */
	ONE("1.0", 1, 1, 1),
	/** The TWO. */
	TWO("2.0", 2, 1, 2);

	/** The text. */
	private String text;
	/** The value. */
	private int value;
	/** The minor. */
	private int minor;
	/** The major. */
	private int major;

	/**
	 * Instantiates a new sCMP version.
	 * 
	 * @param text
	 *            the text
	 * @param value
	 *            the value
	 * @param minor
	 *            the minor
	 * @param major
	 *            the major
	 */
	private SCMPVersion(String text, int value, int minor, int major) {
		this.text = text;
		this.value = value;
		this.minor = minor;
		this.major = major;
	}

	/**
	 * Gets the version.
	 * 
	 * @param text
	 *            the text
	 * @return the version
	 */
	public static SCMPVersion getVersion(String text) {
		if (ONE.text.equals(text)) {
			return ONE;
		}
		return UNDEFINED;
	}

	/**
	 * Checks if is supported.
	 * 
	 * @param scmpVersion
	 *            the scmp version
	 * @return true, if is supported
	 */
	public boolean isSupported(SCMPVersion scmpVersion) {
		if (this == scmpVersion) {
			return true;
		}
		if (this.minor > scmpVersion.value) {
			return false;
		}
		if (this.major < scmpVersion.value) {
			return false;
		}
		return true;
	}

	/**
	 * To string.
	 * 
	 * @return the string
	 */
	@Override
	public String toString() {
		return text;
	}
}
