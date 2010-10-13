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

package org.serviceconnector.console;

/**
 * The Enum SCConsoleCommands.
 */
public enum ConsoleCommand {

	/** The ENABLE. */
	ENABLE("enable"),
	/** The DISABLE. */
	DISABLE("disable"),
	/** The SHOW. */
	STATE("state"),
	/** The SESSIONS. */
	SESSIONS("sessions"),
	/** The KILL. */
	KILL("kill"),
	/** The UNDEFINED. */
	UNDEFINED("undefined");

	/** The key. */
	private String key;

	/**
	 * Instantiates a new SC console commands.
	 * 
	 * @param key
	 *            the key
	 */
	private ConsoleCommand(String key) {
		this.key = key;
	}

	public String getKey() {
		return key;
	}

	public static ConsoleCommand getCommand(String key) {
		if (ENABLE.key.equals(key)) {
			return ENABLE;
		}
		if (DISABLE.key.equals(key)) {
			return DISABLE;
		}
		if (STATE.key.equals(key)) {
			return STATE;
		}
		if (SESSIONS.key.equals(key)) {
			return SESSIONS;
		}
		if (KILL.key.equals(key)) {
			return KILL;
		}
		return UNDEFINED;
	}
}
