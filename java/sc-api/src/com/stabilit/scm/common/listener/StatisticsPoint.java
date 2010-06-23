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
	READ, WRITE, CONNECT, DISCONNECT, EXCEPTION, LOGGER, RUNTIME
, CREATE_SESSION, DELETE_SESSION, DECODE_SCMP, ENCODE_SCMP, KEEP_ALIVE}

/**
 * The Class ConnectionPoint. Allows collecting statistic information - fire
 * read/write, connect/disconnect.
 */
public final class StatisticsPoint extends ListenerSupport<IStatisticsListener> {

	/** The statistics point. */
	private static StatisticsPoint statisticsPoint = new StatisticsPoint();

	private IConnectionListener connectionListener;
	private IExceptionListener exceptionListener;
	private ILoggerListener loggerListener;
	private IRuntimeListener runtimeListener;
	private ISessionListener sessionListener;
	private ISCMPListener scmpListener;
	private IKeepAliveListener keepAliveListener;

	/**
	 * Instantiates a new connection point.
	 */
	private StatisticsPoint() {
		this.connectionListener = new StatisticsConnectionListener();
		this.exceptionListener = new StatisticsExceptionListener();
		this.loggerListener = new StatisticsLoggerListener();
		this.runtimeListener = new StatisticsRuntimeListener();
		this.sessionListener = new StatisticsSessionListener();
		this.scmpListener = new StatisticsSCMPListener();
		this.keepAliveListener = new StatisticsKeepAliveListener();
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
			ConnectionPoint.getInstance().addListener(connectionListener);
			ExceptionPoint.getInstance().addListener(exceptionListener);
			LoggerPoint.getInstance().addListener(loggerListener);
			RuntimePoint.getInstance().addListener(runtimeListener);
			SessionPoint.getInstance().addListener(sessionListener);
			SCMPPoint.getInstance().addListener(scmpListener);
			KeepAlivePoint.getInstance().addListener(keepAliveListener);
		}
		super.addListener(listener);
	}

	public synchronized void removeListener(IStatisticsListener listener) {
		// register connection point
		super.removeListener(listener);
		if (this.isEmpty()) {
			ConnectionPoint.getInstance().removeListener(connectionListener);
			ExceptionPoint.getInstance().removeListener(exceptionListener);
			LoggerPoint.getInstance().removeListener(loggerListener);
			RuntimePoint.getInstance().removeListener(runtimeListener);
			SessionPoint.getInstance().removeListener(sessionListener);
			SCMPPoint.getInstance().removeListener(scmpListener);
			KeepAlivePoint.getInstance().removeListener(keepAliveListener);
		}
	}

	/**
	 * Fire statistics.
	 * 
	 * @param source
	 *            the source
	 * @param port
	 *            the port
	 */
	public void fireStatistics(StatisticsEvent statisticsEvent) {
		int localSize = this.size;
		EventListener[] localArray = this.listenerArray;
		for (int i = 0; i < localSize; i++) {
			try {
				IStatisticsListener statisticsListener = (IStatisticsListener) localArray[i];
				statisticsListener.statistics(statisticsEvent);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	// member class
	class StatisticsConnectionListener implements IConnectionListener {
		@Override
		public void connectEvent(ConnectionEvent connectionEvent)
				throws Exception {
			StatisticsEvent statisticsEvent = new StatisticsEvent(
					connectionEvent.getSource(), connectionEvent);
			statisticsEvent.setEventType(StatisticsEnum.CONNECT);
			fireStatistics(statisticsEvent);
		}

		@Override
		public void disconnectEvent(ConnectionEvent connectionEvent)
				throws Exception {
			StatisticsEvent statisticsEvent = new StatisticsEvent(
					connectionEvent.getSource(), connectionEvent);
			statisticsEvent.setEventType(StatisticsEnum.DISCONNECT);
			fireStatistics(statisticsEvent);
		}

		@Override
		public void readEvent(ConnectionEvent connectionEvent) throws Exception {
			StatisticsEvent statisticsEvent = new StatisticsEvent(
					connectionEvent.getSource(), connectionEvent);
			statisticsEvent.setEventType(StatisticsEnum.READ);
			fireStatistics(statisticsEvent);
		}

		@Override
		public void writeEvent(ConnectionEvent connectionEvent)
				throws Exception {
			StatisticsEvent statisticsEvent = new StatisticsEvent(
					connectionEvent.getSource(), connectionEvent);
			statisticsEvent.setEventType(StatisticsEnum.WRITE);
			fireStatistics(statisticsEvent);
		}
	}

	// member class
	class StatisticsExceptionListener implements IExceptionListener {
		@Override
		public void exceptionEvent(ExceptionEvent exceptionEvent)
				throws Exception {
			StatisticsEvent statisticsEvent = new StatisticsEvent(
					exceptionEvent.getSource(), exceptionEvent);
			statisticsEvent.setEventType(StatisticsEnum.EXCEPTION);
			fireStatistics(statisticsEvent);
		}
	}

	// member class
	class StatisticsLoggerListener implements ILoggerListener {

		@Override
		public void logEvent(LoggerEvent loggerEvent) throws Exception {
			StatisticsEvent statisticsEvent = new StatisticsEvent(
					loggerEvent.getSource(), loggerEvent);
			statisticsEvent.setEventType(StatisticsEnum.LOGGER);
			fireStatistics(statisticsEvent);
		}
	}
	
	// member class
	class StatisticsRuntimeListener implements IRuntimeListener {

		@Override
		public void runtimeEvent(RuntimeEvent runtimeEvent) throws Exception {
			StatisticsEvent statisticsEvent = new StatisticsEvent(
					runtimeEvent.getSource(), runtimeEvent);
			statisticsEvent.setEventType(StatisticsEnum.RUNTIME);
			fireStatistics(statisticsEvent);
		}
	}
	
	// member class
	class StatisticsSessionListener implements ISessionListener {

		@Override
		public void createSessionEvent(SessionEvent sessionEvent) {
			StatisticsEvent statisticsEvent = new StatisticsEvent(
					sessionEvent.getSource(), sessionEvent);
			statisticsEvent.setEventType(StatisticsEnum.CREATE_SESSION);
			fireStatistics(statisticsEvent);
		}

		@Override
		public void deleteSessionEvent(SessionEvent sessionEvent)
				throws Exception {
			StatisticsEvent statisticsEvent = new StatisticsEvent(
					sessionEvent.getSource(), sessionEvent);
			statisticsEvent.setEventType(StatisticsEnum.DELETE_SESSION);
			fireStatistics(statisticsEvent);			
		}
	}
	// member class
	class StatisticsSCMPListener implements ISCMPListener {

		@Override
		public void decodeEvent(SCMPEvent scmpEvent) {
			StatisticsEvent statisticsEvent = new StatisticsEvent(
					scmpEvent.getSource(), scmpEvent);
			statisticsEvent.setEventType(StatisticsEnum.DECODE_SCMP);
			fireStatistics(statisticsEvent);						
		}

		@Override
		public void encodeEvent(SCMPEvent scmpEvent) {
			StatisticsEvent statisticsEvent = new StatisticsEvent(
					scmpEvent.getSource(), scmpEvent);
			statisticsEvent.setEventType(StatisticsEnum.ENCODE_SCMP);
			fireStatistics(statisticsEvent);									
		}
	}
	// member class
	class StatisticsKeepAliveListener implements IKeepAliveListener {

		@Override
		public void keepAliveEvent(KeepAliveEvent keepAliveEvent)
				throws Exception {
			StatisticsEvent statisticsEvent = new StatisticsEvent(
					keepAliveEvent.getSource(), keepAliveEvent);
			statisticsEvent.setEventType(StatisticsEnum.KEEP_ALIVE);
			fireStatistics(statisticsEvent);					
		}
	}
}
