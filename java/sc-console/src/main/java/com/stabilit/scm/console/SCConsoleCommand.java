/*
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
 */

package com.stabilit.scm.console;

/**
 * The Enum SCConsoleCommands.
 */
public enum SCConsoleCommand {

	/** The ENABLE. */
	ENABLE("enable"),
	/** The DISABLE. */
	DISABLE("disable"),
	/** The SHOW. */
	SHOW("show"),
	/** The UNDEFINED. */
	UNDEFINED("undefined");

	/** The key. */
	private String key;

	/**
	 * Instantiates a new sC console commands.
	 * 
	 * @param key
	 *            the key
	 */
	private SCConsoleCommand(String key) {
		this.key = key;
	}

	public String getKey() {
		return key;
	}

	public static SCConsoleCommand getCommand(String key) {
		if (ENABLE.key.equals(key)) {
			return ENABLE;
		}
		if (DISABLE.key.equals(key)) {
			return DISABLE;
		}
		if (SHOW.key.equals(key)) {
			return SHOW;
		}
		return UNDEFINED;
	}
}
