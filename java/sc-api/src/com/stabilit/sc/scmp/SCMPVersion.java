/*
 *-----------------------------------------------------------------------------*
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
 *-----------------------------------------------------------------------------*
/*
/**
 * 
 */
package com.stabilit.sc.scmp;

/**
 * @author JTraber
 */
public enum SCMPVersion {

	UNDEFINED("0.0", 0, 0, 0), ONE("1.0", 1, 1, 1), TWO("2.0", 2,1,2);

	private String text;
	private int value;
	private int minor;
	private int major;

	/**
	 * 
	 */
	private SCMPVersion(String text, int value, int minor, int major) {
		this.text = text;
		this.value = value;
		this.minor = minor;
		this.major = major;
	}

	public static SCMPVersion getVersion(String text) {
		if (ONE.text.equals(text)) {
			return ONE;
		}
		return UNDEFINED;
	}
	
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
	
	@Override
	public String toString() {
		return text;
	}
}
