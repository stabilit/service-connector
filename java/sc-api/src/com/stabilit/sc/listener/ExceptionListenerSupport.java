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

import java.util.EventListener;

/**
 * The Class ExceptionListenerSupport. Allows logging exceptions.
 */
public class ExceptionListenerSupport extends ListenerSupport<IExceptionListener> {

	/** The exception listener support. */
	private static ExceptionListenerSupport exceptionListenerSupport = new ExceptionListenerSupport();

	/**
	 * Instantiates a new exception listener support.
	 */
	private ExceptionListenerSupport() {
	}

	/**
	 * Gets the single instance of ExceptionListenerSupport.
	 * 
	 * @return single instance of ExceptionListenerSupport
	 */
	public static ExceptionListenerSupport getInstance() {
		return exceptionListenerSupport;
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
			ExceptionListenerSupport.getInstance().fireException(exceptionEvent);
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
