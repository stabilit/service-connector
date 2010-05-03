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
package com.stabilit.sc.common.listener;

import java.util.Iterator;

public class ExceptionListenerSupport extends
		ListenerSupport<IExceptionListener> {

	private static ExceptionListenerSupport exceptionListenerSupport = new ExceptionListenerSupport();

	private ExceptionListenerSupport() {
	}

	public static ExceptionListenerSupport getInstance() {
		return exceptionListenerSupport;
	}

	public static void fireException(Object source, Throwable th) {
		if (getInstance().isEmpty() == false) {
			ExceptionEvent exceptionEvent = new ExceptionEvent(source, th);
			ExceptionListenerSupport.getInstance()
					.fireException(exceptionEvent);
		}
	}

	public void fireException(ExceptionEvent exceptionEvent) {
		Iterator<IExceptionListener> iter = listenerList.iterator();
		while (iter.hasNext()) {
			try {
				IExceptionListener exceptionListener = iter.next();
				exceptionListener.exceptionEvent(exceptionEvent);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
