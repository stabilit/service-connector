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
package com.stabilit.sc.listener;

import java.util.EventObject;

/**
 * The Class PerformanceEvent. Event for logging performance purpose.
 */
public class PerformanceEvent extends EventObject {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -2561926364455371080L;

	/** The method name where performance event fired. */
	private String methodName;

	/** The time. */
	private long time;

	/**
	 * Instantiates a new performance event.
	 * 
	 * @param source
	 *            the source
	 * @param methodName
	 *            the method name
	 */
	public PerformanceEvent(Object source, String methodName) {
		super(source);
		this.time = System.nanoTime();
		this.methodName = methodName;
	}

	/**
	 * Gets the time.
	 * 
	 * @return the time
	 */
	public long getTime() {
		return time;
	}

	/**
	 * Gets the method name.
	 * 
	 * @return the methodName
	 */
	public String getMethodName() {
		return methodName;
	}
}
