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
package com.stabilit.scm.common.log.listener;

import java.util.EventListener;

/**
 * The Class ExceptionPoint. Allows logging on exception level - fire exception.
 */
public final class ExceptionPoint extends ListenerSupport<IExceptionListener> {

	/** The exception point. */
	private static ExceptionPoint exceptionPoint = new ExceptionPoint();

	/**
	 * Instantiates a new ExceptionPoint.
	 */
	private ExceptionPoint() {
	}

	/**
	 * Gets the single instance of ExceptionPoint.
	 * 
	 * @return single instance of ExceptionPoint
	 */
	public static ExceptionPoint getInstance() {
		return exceptionPoint;
	}

	/**
	 * Fire exception.
	 * 
	 * @param source
	 *            the source
	 * @param th
	 *            the th
	 */
	public void fireException(Object source, Throwable th) {
		if (getInstance().isEmpty() == false) {
			ExceptionEvent exceptionEvent = new ExceptionEvent(source, th);
			this.fireException(exceptionEvent);
		}
	}

	/**
	 * Fire exception.
	 * 
	 * @param exceptionEvent
	 *            the exception event
	 */
	public void fireException(ExceptionEvent exceptionEvent) {
		int localSize = this.size;
		EventListener[] localArray = this.listenerArray;
		for (int i = 0; i < localSize; i++) {
			try {
				IExceptionListener exceptionListener = (IExceptionListener) localArray[i];
				exceptionListener.exceptionEvent(exceptionEvent);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
