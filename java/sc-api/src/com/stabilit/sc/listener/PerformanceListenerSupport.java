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
 * The Class PerformanceListenerSupport. Allows logging performance - fire begin/end.
 */
public class PerformanceListenerSupport extends
		ListenerSupport<IPerformanceListener> {

	/** The performance listener support. */
	private static PerformanceListenerSupport performanceListenerSupport = new PerformanceListenerSupport();
	
	/** The on. */
	private boolean on = false;

	/**
	 * Instantiates a new performance listener support.
	 */
	private PerformanceListenerSupport() {
	}

	/**
	 * Gets the single instance of PerformanceListenerSupport.
	 * 
	 * @return single instance of PerformanceListenerSupport
	 */
	public static PerformanceListenerSupport getInstance() {
		return performanceListenerSupport;
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
	 * @param on the new on
	 */
	public void setOn(boolean on) {
		this.on = on;
	}
	
	/**
	 * Fire begin.
	 * 
	 * @param source the source
	 * @param time the time
	 */
	public void fireBegin(Object source, long time) {
		if (getInstance().isEmpty() == false) {
			PerformanceEvent performanceEvent = new PerformanceEvent(source, time);
			PerformanceListenerSupport.getInstance().performanceBeginEvent(performanceEvent);
		}
	}

	/**
	 * Fire end.
	 * 
	 * @param source the source
	 * @param time the time
	 */
	public void fireEnd(Object source, long time) {
		if (getInstance().isEmpty() == false) {
			PerformanceEvent performanceEvent = new PerformanceEvent(source, time);
			PerformanceListenerSupport.getInstance().performanceEndEvent(performanceEvent);
		}
	}

	/**
	 * Performance begin event.
	 * 
	 * @param performanceEvent the performance event
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
	 * @param performanceEvent the performance event
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