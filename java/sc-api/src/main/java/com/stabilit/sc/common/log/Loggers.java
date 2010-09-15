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
package com.stabilit.sc.common.log;

/**
 * The Enum Loggers. All available log4j Loggers beside class loggers in SCM.
 */
public enum Loggers {

	CONNECTION("ConnectionLogger"), //
	SESSION("SessionLogger"), //
	SUBSCRIPTION("SubscriptionLogger"), //
	MESSAGE("MessageLogger"), //
	PERFORMANCE("PerformanceLogger");

	/** The value. */
	private String value;

	/**
	 * Instantiates a new logger.
	 * 
	 * @param value
	 *            the value
	 */
	private Loggers(String value) {
		this.value = value;
	}

	/**
	 * Gets the value.
	 * 
	 * @return the value
	 */
	public String getValue() {
		return value;
	}
}
