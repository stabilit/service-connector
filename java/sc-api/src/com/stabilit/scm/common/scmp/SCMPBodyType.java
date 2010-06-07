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
package com.stabilit.scm.scmp;

/**
 * The Enum SCMPBodyType. Defines possible body types in SCMP.
 * 
 * @author JTraber
 */
public enum SCMPBodyType {

	/** binary. */
	binary("binary"),
	/** text. */
	text("text"),
	/** internal message. */
	internalMessage("internalMessage"),
	/** undefined. */
	undefined("undefined");

	/** The name. */
	private String name;

	/**
	 * Instantiates a new SCMP body type.
	 * 
	 * @param name
	 *            the name
	 */
	private SCMPBodyType(String name) {
		this.name = name;
	}

	/**
	 * Gets the name.
	 * 
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets the body type by string.
	 * 
	 * @param bodyType
	 *            the body type
	 * @return the body type
	 */
	public static SCMPBodyType getBodyType(String bodyType) {
		if (bodyType == null) {
			return undefined;
		}
		return SCMPBodyType.valueOf(bodyType);
	}

	/**
	 * Gets the mime type. mime types http://msdn.microsoft.com/en-us/library/ms775147%28VS.85%29.aspx
	 * 
	 * @return the mime type
	 */
	public String getMimeType() {
		switch (this) {
		case binary:
			return "application/octet-stream";
		case text:
			return "text/plain";
		case internalMessage:
			return "text/plain";
		default:
			return "application/octet-stream";
		}
	}
}
