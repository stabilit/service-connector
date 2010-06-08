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

enum StatisticsEnum {
	READ, WRITE, CONNECT, DISCONNECT;
}

/**
 * The Class ConnectionPoint. Allows logging on connection level - fire read/write, connect/disconnect.
 */
public final class StatisticsPoint extends ListenerSupport<IStatisticsListener> {

	/** The statistics point. */
	private static StatisticsPoint statisticsPoint = new StatisticsPoint();

	/**
	 * Instantiates a new connection point.
	 */
	private StatisticsPoint() {
	}

	/**
	 * Gets the single instance of StatisticsPoint.
	 * 
	 * @return single instance of StatisticsPoint
	 */
	public static StatisticsPoint getInstance() {
		return statisticsPoint;
	}

	public synchronized void addListener(IStatisticsListener listener) {
		if (this.isEmpty()) {
			// register connection point
			ConnectionPoint.getInstance().addListener(new StatisticsConnectionListener());
		}
		super.addListener(listener);		
	}
	
	/**
	 * Fire statistics.
	 * 
	 * @param source
	 *            the source
	 * @param port
	 *            the port
	 */
	public void fireConnectionStatistics(StatisticsEvent statisticsEvent) {
		int localSize = this.size;
		EventListener[] localArray = this.listenerArray;
		for (int i = 0; i < localSize; i++) {
			try {
				IStatisticsListener statisticsListener = (IStatisticsListener) localArray[i];
				statisticsListener.connectionStatistics(statisticsEvent);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	// member class
	class StatisticsConnectionListener implements IConnectionListener
	{
		@Override
		public void connectEvent(ConnectionEvent connectionEvent)
				throws Exception {		
			StatisticsEvent statisticsEvent = new StatisticsEvent(connectionEvent.getSource(), connectionEvent);
			statisticsEvent.setEventType(StatisticsEnum.READ);
		    fireConnectionStatistics(statisticsEvent);	
		}

		@Override
		public void disconnectEvent(ConnectionEvent connectionEvent)
				throws Exception {			
			StatisticsEvent statisticsEvent = new StatisticsEvent(connectionEvent.getSource(), connectionEvent);
		    fireConnectionStatistics(statisticsEvent);	
		}
		@Override
		public void readEvent(ConnectionEvent connectionEvent) throws Exception {
			StatisticsEvent statisticsEvent = new StatisticsEvent(connectionEvent.getSource(), connectionEvent);
		    fireConnectionStatistics(statisticsEvent);	
		}
		@Override
		public void writeEvent(ConnectionEvent connectionEvent)
				throws Exception {
			StatisticsEvent statisticsEvent = new StatisticsEvent(connectionEvent.getSource(), connectionEvent);
		    fireConnectionStatistics(statisticsEvent);	
		}		
	}
}
