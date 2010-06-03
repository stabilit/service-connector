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
package com.stabilit.scm.listener;

import java.util.EventObject;

/**
 * The Class ExceptionEvent. Event for logging exception purpose.
 */
public class ExceptionEvent extends EventObject {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -9169664176519752342L;
	/** The throwable. */
	private Throwable throwable;

	/**
	 * Instantiates a new exception event.
	 * 
	 * @param source
	 *            the source
	 * @param throwable
	 *            the throwable
	 */
	public ExceptionEvent(Object source, Throwable throwable) {
		super(source);
		this.throwable = throwable;
	}

	/**
	 * Gets the throwable.
	 * 
	 * @return the throwable
	 */
	public Throwable getThrowable() {
		return throwable;
	}
}
