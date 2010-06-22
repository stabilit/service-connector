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

import java.util.EventListener;

/**
 * The Class PerformancePoint. Allows logging performance - fire begin/end.
 */
public final class PerformancePoint extends ListenerSupport<IPerformanceListener> {

	/** The performance point. */
	private static PerformancePoint performancePoint = new PerformancePoint();

	/** The on, indicates that performance logging is active. */
	private boolean on = false;

	/**
	 * Instantiates a new performance point.
	 */
	private PerformancePoint() {
	}

	/**
	 * Gets the single instance of PerformancePoint.
	 * 
	 * @return single instance of PerformancePoint
	 */
	public static PerformancePoint getInstance() {
		return performancePoint;
	}

	/**
	 * Checks if is on.
	 * 
	 * @return true, if is on
	 */
	public boolean isOn() {
		return on;
	}

	/**
	 * Sets the on.
	 * 
	 * @param on
	 *            the new on
	 */
	public void setOn(boolean on) {
		this.on = on;
	}

	/**
	 * Fire begin.
	 * 
	 * @param source
	 *            the source
	 * @param method
	 *            the method
	 */
	public void fireBegin(Object source, String method) {
		if (this.isOn() == false) {
			return;
		}
		if (getInstance().isEmpty() == false) {
			PerformanceEvent performanceEvent = new PerformanceEvent(source, method);
			PerformancePoint.getInstance().performanceBeginEvent(performanceEvent);
		}
	}

	/**
	 * Fire end.
	 * 
	 * @param source
	 *            the source
	 * @param method
	 *            the method
	 */
	public void fireEnd(Object source, String method) {
		if (this.isOn() == false) {
			return;
		}
		if (getInstance().isEmpty() == false) {
			PerformanceEvent performanceEvent = new PerformanceEvent(source, method);
			PerformancePoint.getInstance().performanceEndEvent(performanceEvent);
		}
	}

	/**
	 * Performance begin event.
	 * 
	 * @param performanceEvent
	 *            the performance event
	 */
	private void performanceBeginEvent(PerformanceEvent performanceEvent) {
		int localSize = this.size;
		EventListener[] localArray = this.listenerArray;
		for (int i = 0; i < localSize; i++) {
			try {
				IPerformanceListener performanceListener = (IPerformanceListener) localArray[i];
				performanceListener.begin(performanceEvent);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Performance end event.
	 * 
	 * @param performanceEvent
	 *            the performance event
	 */
	private void performanceEndEvent(PerformanceEvent performanceEvent) {
		int localSize = this.size;
		EventListener[] localArray = this.listenerArray;
		for (int i = 0; i < localSize; i++) {
			try {
				IPerformanceListener performanceListener = (IPerformanceListener) localArray[i];
				performanceListener.end(performanceEvent);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}