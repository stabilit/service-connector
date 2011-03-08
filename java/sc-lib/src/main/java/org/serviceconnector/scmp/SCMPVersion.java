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
package org.serviceconnector.scmp;

import org.apache.log4j.Logger;
import org.serviceconnector.cmd.SCMPValidatorException;

/**
 * Provides actual SCMP version and method to check compatibility. 
 * The SCMP versioning schema follows this philosophy </br>
 * 
 * <pre>
 * 	9.9 (Ex. 2.4)</br>
 *  | | </br>
 *  | +-- version number</br>
 *  +-- release number</br>
 * </pre>
 * 
 *<p>
 * Release number designates the major release (design) of the protocol. It starts at 1 and is incremented by 1. 
 * New protocol is by definition not compatible with the old one. E.g. if the release numver is not the same an error occurs.
 *<p>
 * Version number designates the minor improvements of the protocol. It starts at 0 and is incremented by 1. 
 * New versions may contain additional features and are compatible.
 * E.g. 2.(x+1) is compatible with V2.(x) but not the other way round.</br>
 * </br>
 * See the SC_0_SCMP_E.PDF for more details.
 * 
 * @author JTraber
 */
public enum SCMPVersion {

	/** 1.0, the current version */
	CURRENT(0x31, 0x30),
	/** 3.2, the version to make tests - DO NOT CHANGE ! */
	TEST(0x33, 0x32);

	/** The Constant LOGGER. */
	@SuppressWarnings("unused")
	private static final Logger LOGGER = Logger.getLogger(SCMPVersion.class);
	
	/** The release. */
	private byte release;
	/** The version. */
	private byte version;

	/** The Constant DOT_HEX. */
	private static final byte DOT_HEX = 0x2E;

	/**
	 * Instantiates a new SCMP version.
	 * 
	 * @param release
	 *            the release number
	 * @param version
	 *            the version number
	 */
	private SCMPVersion(Integer release, Integer version) {
		this.version = version.byteValue();
		this.release = release.byteValue();
	}

	/**
	 * Checks if is supported.
	 *
	 * @param buffer the buffer containing the version number
	 * @throws SCMPValidatorException
	 */
	public void isSupported(byte[] buffer) throws SCMPValidatorException {

		if (this.release != buffer[0]) {
			throw new SCMPValidatorException(SCMPError.HV_WRONG_SCMP_RELEASE_NR, new String(buffer));
		}
		if (buffer[1] != SCMPVersion.DOT_HEX) {
			throw new SCMPValidatorException(SCMPError.HV_WRONG_SCMP_VERSION_FORMAT, new String(buffer));
		}
		if (this.version < buffer[2]) {
			throw new SCMPValidatorException(SCMPError.HV_WRONG_SCMP_VERSION_NR, new String(buffer));
		}
		return;
	}

	@Override
	public String toString() {
		return new String(new byte[] { this.release, SCMPVersion.DOT_HEX, this.version });
	}
}
