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
package com.stabilit.scm.common.log;

/**
 * The Enum Level. States of logging.
 */
public enum Level {

	/** The ERROR. */
	ERROR("ERR", 1),
	/** The EXCEPTION. */
	EXCEPTION("EXC", 2),
	/** The WARN. */
	WARN("WRN", 3),
	/** The INFO. */
	INFO("INF", 4),
	/** The DEBUG. */
	DEBUG("DBG", 5),
	/** The TRACE. */
	TRACE("TRC", 6);

	/** The name. */
	private String name;
	/** The level. */
	private int level;

	/**
	 * Instantiates a new level.
	 * 
	 * @param name
	 *            the name
	 * @param level
	 *            the level
	 */
	private Level(String name, int level) {
		this.name = name;
		this.level = level;
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
	 * Gets the level.
	 * 
	 * @return the level
	 */
	public int getLevel() {
		return level;
	}

	/**
	 * Supports level of logging.
	 * 
	 * @param level
	 *            the level
	 * @return true, if successful
	 */
	public boolean supportsLevel(Level level) {
		return this.level >= level.level;
	}
}
