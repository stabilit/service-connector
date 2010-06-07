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
package com.stabilit.scm.common;

import com.stabilit.scm.common.listener.ExceptionPoint;
import com.stabilit.scm.common.util.ValidatorException;

/**
 * The Enum SCVersion. Responsible to provide SCVersion and compatibility checks.
 * 
 * @author JTraber
 */
public enum SCVersion {

	/** The UNDEFINED. */
	UNDEFINED("0.0-000", 0, 0, 0),
	/** The ONE. */
	ONE("1.0-000", 1, 0, 0);

	/** The text. */
	private String text;
	/** The release. */
	private int release; // e.g : 1
	/** The version. */
	private int version; // e.g : 0
	/** The revision. */
	private int revision; // e.g : 023 -> all together 1.0-023

	/**
	 * Instantiates a new SCVersion.
	 * 
	 * @param text
	 *            the text
	 * @param release
	 *            the release
	 * @param version
	 *            the version
	 * @param revision
	 *            the revision
	 */
	private SCVersion(String text, int release, int version, int revision) {
		this.text = text;
		this.release = release;
		this.version = version;
		this.revision = revision;
	}

	/**
	 * Checks if is supported.
	 * 
	 * @param text
	 *            the text
	 * @return true, if is supported
	 */
	public boolean isSupported(String text) {
		try {
			String[] splitted = text.split("\\.|-");
			if (splitted.length != 3) {
				throw new ValidatorException("invalid sc version [" + text + "]");
			}
			int release = Integer.parseInt(splitted[0]);

			if (this.release != release) {
				return false;
			}
			int version = Integer.parseInt(splitted[1]);
			if (this.version < version) {
				return false;
			}
			int revision = Integer.parseInt(splitted[2]);
			if (this.revision < revision) {
				return false;
			}
			return true;
		} catch (Exception e) {
			ExceptionPoint.getInstance().fireException(this, e);
		}
		return false;
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
