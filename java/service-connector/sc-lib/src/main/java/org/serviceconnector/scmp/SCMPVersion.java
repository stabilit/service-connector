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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.serviceconnector.Constants;
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.util.IReversibleEnum;
import org.serviceconnector.util.ReverseEnumMap;

/**
 * Provides actual SCMP version and method to check compatibility. The SCMP version schema follows this philosophy <br />
 *
 * <pre>
 * 	9.9 (E.g. 2.4)<br />
 *  | | <br />
 *  | +-- version number<br />
 *  +-- release number<br />
 * </pre>
 * <p>
 * Release number designates the major release (design) of the protocol. It starts at 1 and is incremented by 1. New protocol is by definition not compatible with the old one. E.g.
 * if the release number is not the same an error occurs.
 * <p>
 * Version number designates the minor improvements of the protocol. It starts at 0 and is incremented by 1. New versions may contain additional features and are compatible. E.g.
 * 2.(x+1) is compatible with V2.(x) but not the other way round.<br />
 * <br />
 * Handling of the SCMP Version in a network hierarchy works according to the following rules and restrictions. The components supporting a SCMP Version have to follow the top down
 * restrictions. This means the requester is able to connect to components supporting equal or higher protocol version. In other words server and SC on level zero have to support
 * equal or higher SCMP Version than connecting clients. Clients and Proxies may connect using older versions.<br>
 * <br>
 * In the context of a session service request the server has to respond with protocol version given in the request. Protocol version is received from the client and nowhere
 * changes for the whole communication flow. In the context of a publish service the server has to publish messages with protocol version compatible to the clients version (lowest
 * version in field). For example the server supports SCMP Version 1.3 and connected clients version 1.2 the server has to publish with version 1.2. Otherwise clients are not able
 * to receive messages. Fault messages from the SC to the server may have version 1.2 even if SC and Server support 1.3. This happens because supported protocol version number of
 * the server is unknown for the SC. In conclusion components supporting an SCMP Version have to implement the matching rules and version logic properly in any case.<br>
 * See the SC_0_SCMP_E.PDF for more details.
 *
 * @author JTraber
 */
public enum SCMPVersion implements IReversibleEnum<String, SCMPVersion> {
	/**
	 * PAY ATTENTION: Its important to enumerate any potential SCMP Version used by requesters. E.g. if CURRENT is 1.4 and requesters may connect using 1.2, VERSION_1_2('1','2')
	 * must be defined here to. LOWEST Version correlates to the lowest requester version in field.
	 **/

	/** 1.3, the current version. */
	CURRENT('1', '3'),
	/** 1.2, old version, correlates to lowest requester version in field. */
	LOWEST('1', '2'),
	/** 3.2, the version to make tests - DO NOT CHANGE ! */
	TEST('3', '2'),
	/** The UNDEFINED SCMP Version. */
	UNDEFINED(' ', ' ');

	/** The Constant LOGGER. */
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(SCMPVersion.class);

	/** The release. */
	private byte release;
	/** The version. */
	private byte version;

	/** The REVERSE_MAP, to get access to the enumeration constants by string value. */
	private static final ReverseEnumMap<String, SCMPVersion> REVERSE_MAP = new ReverseEnumMap<String, SCMPVersion>(SCMPVersion.class);

	/**
	 * Instantiates a new SCMP version.
	 *
	 * @param release the release number
	 * @param version the version number
	 */
	private SCMPVersion(char release, char version) {
		this.version = (byte) version;
		this.release = (byte) release;
	}

	/**
	 * Checks if is supported.
	 *
	 * @param buffer the buffer containing the version number
	 * @throws SCMPValidatorException the SCMP validation exception
	 */
	public void isSupported(byte[] buffer) throws SCMPValidatorException {
		if (this.release != buffer[0]) {
			throw new SCMPValidatorException(SCMPError.HV_WRONG_SCMP_RELEASE_NR, new String(buffer));
		}
		if (buffer[1] != Constants.DOT_HEX) {
			throw new SCMPValidatorException(SCMPError.HV_WRONG_SCMP_VERSION_FORMAT, new String(buffer));
		}
		if (this.version < buffer[2]) {
			throw new SCMPValidatorException(SCMPError.HV_WRONG_SCMP_VERSION_NR, new String(buffer));
		}
		return;
	}

	/**
	 * Gets the sCMP version by byte array.
	 *
	 * @param versionBuffer the version buffer
	 * @return the sCMP version by byte array
	 */
	public static SCMPVersion getSCMPVersionByByteArray(byte[] versionBuffer) {
		SCMPVersion scmpVersion = REVERSE_MAP.get(new String(versionBuffer));
		if (scmpVersion == null) {
			// SCMP version doesn't match to a valid SCMPVersion
			return SCMPVersion.UNDEFINED;
		}
		return scmpVersion;
	}

	/**
	 * Gets the version number.
	 *
	 * @return the version number
	 */
	public byte getVersionNumber() {
		return this.version;
	}

	/**
	 * Gets the release number.
	 *
	 * @return the release number
	 */
	public byte getReleaseNumber() {
		return this.release;
	}

	/** {@inheritDoc} */
	@Override
	public String getValue() {
		StringBuilder sb = new StringBuilder();
		sb.append((char) this.release);
		sb.append((char) Constants.DOT_HEX);
		sb.append((char) this.version);
		return sb.toString();
	}

	/** {@inheritDoc} */
	@Override
	public SCMPVersion reverse(String buffer) {
		SCMPVersion scmpVersion = REVERSE_MAP.get(buffer);
		if (scmpVersion == null) {
			// SCMP version doesn't match to a valid SCMPVersion
			return SCMPVersion.UNDEFINED;
		}
		return scmpVersion;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append((char) this.release);
		sb.append((char) Constants.DOT_HEX);
		sb.append((char) this.version);
		return sb.toString();
	}
}
