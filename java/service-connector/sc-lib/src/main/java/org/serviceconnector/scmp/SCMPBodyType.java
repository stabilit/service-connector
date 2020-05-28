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

import org.serviceconnector.util.IReversibleEnum;
import org.serviceconnector.util.ReverseEnumMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Enum SCMPBodyType. Defines possible body types in SCMP.
 *
 * @author JTraber
 */
public enum SCMPBodyType implements IReversibleEnum<String, SCMPBodyType> {

	/** binary. */
	BINARY("bin"),
	/** text. */
	TEXT("txt"),
	/** The INPUT_STREAM. */
	INPUT_STREAM("inputStream"),
	/** undefined. */
	UNDEFINED("undefined");

	/** The Constant LOGGER. */
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(SCMPBodyType.class);

	/** The value. */
	private String value;
	/** The REVERSE_MAP, to get access to the enum constants by string value. */
	private static final ReverseEnumMap<String, SCMPBodyType> REVERSE_MAP = new ReverseEnumMap<String, SCMPBodyType>(SCMPBodyType.class);

	/**
	 * Instantiates a new SCMP body type.
	 *
	 * @param value the name
	 */
	private SCMPBodyType(String value) {
		this.value = value;
	}

	/**
	 * Gets the body type by string.
	 *
	 * @param bodyTypeString the body type
	 * @return the body type
	 */
	public static SCMPBodyType getBodyType(String bodyTypeString) {
		SCMPBodyType bodyType = REVERSE_MAP.get(bodyTypeString);
		if (bodyType == null) {
			// bodyTypeString doesn't match to a valid SCMPBodyType
			return SCMPBodyType.UNDEFINED;
		}
		return bodyType;
	}

	/**
	 * Gets the mime type. mime types http://msdn.microsoft.com/en-us/library/ms775147%28VS.85%29.aspx
	 *
	 * @return the mime type
	 */
	public String getMimeType() {
		switch (this) {
			case BINARY:
			case INPUT_STREAM:
				return "application/octet-stream";
			case TEXT:
				return "text/plain";
			default:
				return "application/octet-stream";
		}
	}

	@Override
	public String getValue() {
		return this.value;
	}

	@Override
	public SCMPBodyType reverse(String key) {
		return SCMPBodyType.getBodyType(key);
	}
}
