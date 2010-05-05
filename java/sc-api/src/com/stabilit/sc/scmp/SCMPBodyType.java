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
 * @author JTraber
 * 
 */
public enum SCMPBodyType {

	binary("binary"), text("text"), message("message"), undefined("undefined");

	private String name;

	private SCMPBodyType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public static SCMPBodyType getBodyType(String bodyType) {
		if (bodyType == null) {
			return undefined;
		}
		return SCMPBodyType.valueOf(bodyType);
	}

	// mime types http://msdn.microsoft.com/en-us/library/ms775147%28VS.85%29.aspx
	public String getMimeType() {
		switch (this) {
		case binary:
			return "application/octet-stream";
		case text:
			return "text/plain";
		case message:
			return "text/plain";
		default:
			return "application/octet-stream";
		}
	}
}
