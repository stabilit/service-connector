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
package com.stabilit.scm.common.listener;

import java.util.EventObject;

import com.stabilit.scm.common.log.Level;

/**
 * The Class LoggerEvent. Event for logging purpose.
 */
public class LoggerEvent extends EventObject {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 8286024613410908732L;
	/** The text. */
	private String text;
	/** The level. */
	private Level level;

	/**
	 * Instantiates a new logger event.
	 * 
	 * @param source
	 *            the source
	 * @param text
	 *            the text
	 * @param level
	 *            the level
	 */
	public LoggerEvent(Object source, String text, Level level) {
		super(source);
		this.text = text;
		this.level = level;
	}

	/**
	 * Gets the text.
	 * 
	 * @return the text
	 */
	public String getText() {
		return text;
	}

	/**
	 * Gets the level of logging.
	 * 
	 * @return the level
	 */
	public Level getLevel() {
		return level;
	}
}
