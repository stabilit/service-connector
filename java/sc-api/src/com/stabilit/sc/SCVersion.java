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
package com.stabilit.sc;

import com.stabilit.sc.listener.ExceptionPoint;
import com.stabilit.sc.util.ValidatorException;

/**
 * @author JTraber
 */
public enum SCVersion {

	UNDEFINED("0.0-000", 0, 0, 0), ONE("1.0-000", 1, 0, 0);

	private String text;
	private int release; // e.g : 1
	private int version; // e.g : 0
	private int revision; // e.g : 023 -> all together 1.0-023

	private SCVersion(String text, int release, int version, int revision) {
		this.text = text;
		this.release = release;
		this.version = version;
		this.revision = revision;
	}

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

	@Override
	public String toString() {
		return text;
	}
}
