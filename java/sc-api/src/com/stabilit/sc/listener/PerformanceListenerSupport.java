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

public class PerformanceListenerSupport extends
		ListenerSupport<IPerformanceListener> {

	private static PerformanceListenerSupport performanceListenerSupport = new PerformanceListenerSupport();
	private boolean on = false;

	private PerformanceListenerSupport() {
	}

	public static PerformanceListenerSupport getInstance() {
		return performanceListenerSupport;
	}

	public boolean isOn() {
		return on;
	}
	
	public void setOn(boolean on) {
		this.on = on;
	}
	
	public void fireBegin(Object source, long time) {
		if (getInstance().isEmpty() == false) {
			PerformanceEvent performanceEvent = new PerformanceEvent(source, time);
			PerformanceListenerSupport.getInstance().performanceBeginEvent(performanceEvent);
		}
	}

	public void fireEnd(Object source, long time) {
		if (getInstance().isEmpty() == false) {
			PerformanceEvent performanceEvent = new PerformanceEvent(source, time);
			PerformanceListenerSupport.getInstance().performanceEndEvent(performanceEvent);
		}
	}

	public void performanceBeginEvent(PerformanceEvent performanceEvent) {
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
	
	public void performanceEndEvent(PerformanceEvent performanceEvent) {
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