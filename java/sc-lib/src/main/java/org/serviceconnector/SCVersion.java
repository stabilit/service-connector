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
package org.serviceconnector;

import java.text.DecimalFormat;

import org.apache.log4j.Logger;
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.scmp.SCMPError;

/**
 * Provides actual SC version and method to check compatibility. 
 * The SC versioning schema follows this philosophy <br />
 * 
 * <pre>
 * 	A99.99-999 (Ex. V2.4-265)<br />
 *    | |  |   |<br />
 *    | |  |   +-- revision number<br />
 *    | |  +-- version number<br />
 *    | +-- release number<br />
 *    +-- version type<br />
 * </pre>
 * 
 * Version type designates the target and scope and can be: <br />
 * <ul>
 * <li>X – experimental, usually not distributed</li>
 * <li>T – field test, deployed to selected customers</li>
 * <li>V – final version, publically available</li>
 * </ul>
 *<p>
 * Release number designates the major release (design) of the product. It starts at 1 and is incremented by 1. New release is
 * usually a complete rewrite. V(i+1).xx-zzz is not compatible with V(i).xx-zzz
 *<p>
 * Version number designates the interface signature. It starts at 0 and is incremented by 1. New versions are compatible, but may
 * contain additional features. V2.(x+1)-zzz is compatible with V2.(x)-zzz but not the other way round. 
 * It is recommended to recompilate and review of the application code.
 *<p>
 * Revision number designates the actual development stage. It starts at 1 and is incremented by 1. New
 * versions are fully compatible. V2.4-(z+1) is compatible with V2.4-z but not the other way round.<br />
 * <br />
 * See the SC_0_SCMP_E.PDF for more details.
 * 
 * @author JTraber
 */
public enum SCVersion {

	/** The current version. */
	CURRENT(1, 2, 1),
	/** The version to make tests - DO NOT CHANGE ! */
	TEST(3, 2, 5);

	/** The Constant LOGGER. */
	@SuppressWarnings("unused")
	private static final Logger LOGGER = Logger.getLogger(SCVersion.class);

	/** The Constant df. */
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
	 * @param scVersion
	 *            the text in format 999.999-999 e.g. 1.5-003
	 * @throws SCMPValidatorException
	 *             the SCMP validator exception
	 */
	public void isSupported(String scVersion) throws SCMPValidatorException {
		if (scVersion == null) {
			throw new SCMPValidatorException(SCMPError.HV_ERROR, "SC version is missing");
		}
		if (scVersion.matches("\\d*\\.\\d*-\\d{3}") == false) {
			throw new SCMPValidatorException(SCMPError.HV_WRONG_SC_VERSION_FORMAT, scVersion);
		}
		String[] splitted = scVersion.split("\\.|-");
		if (splitted.length != 3) {
			throw new SCMPValidatorException(SCMPError.HV_WRONG_SC_VERSION_FORMAT, scVersion);
		}
		int release = Integer.parseInt(splitted[0]);
		if (this.release != release) {
			throw new SCMPValidatorException(SCMPError.HV_WRONG_SC_RELEASE_NR, scVersion);
		}
		int version = Integer.parseInt(splitted[1]);
		if (this.version < version) {
			throw new SCMPValidatorException(SCMPError.HV_WRONG_SC_VERSION_NR, scVersion);
		}
		int revision = Integer.parseInt(splitted[2]);
		if ((this.version == version) && (this.revision < revision)) {
			throw new SCMPValidatorException(SCMPError.HV_WRONG_SC_REVISION_NR, scVersion);
		}
		return;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return release + "." + version + "-" + df.format(revision);
	}
}
