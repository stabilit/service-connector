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
 * The Class WarningListenerSupport. Allows logging warning - fire warning.
 */
public class WarningListenerSupport extends ListenerSupport<IWarningListener> {

	/** The warning listener support. */
	private static WarningListenerSupport warningListenerSupport = new WarningListenerSupport();

	/**
	 * Instantiates a new warning listener support.
	 */
	private WarningListenerSupport() {
	}

	/**
	 * Gets the single instance of WarningListenerSupport.
	 * 
	 * @return single instance of WarningListenerSupport
	 */
	public static WarningListenerSupport getInstance() {
		return warningListenerSupport;
	}

	/**
	 * Fire warning.
	 * 
	 * @param source
	 *            the source
	 * @param text
	 *            the text
	 */
	public void fireWarning(Object source, String text) {
		if (getInstance().isEmpty() == false) {
			WarningEvent warningEvent = new WarningEvent(source, text);
			WarningListenerSupport.getInstance().fireWarning(warningEvent);
		}
	}

	/**
	 * Fire warning.
	 * 
	 * @param warningEvent
	 *            the warning event
	 */
	public void fireWarning(WarningEvent warningEvent) {
		int localSize = this.size;
		EventListener[] localArray = this.listenerArray;
		for (int i = 0; i < localSize; i++) {
			try {
				IWarningListener warningListener = (IWarningListener) localArray[i];
				warningListener.warningEvent(warningEvent);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
