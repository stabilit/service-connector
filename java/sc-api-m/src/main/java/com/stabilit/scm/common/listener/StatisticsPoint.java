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
 * The Enum StatisticsEnum. Possible events in system.
 */
enum StatisticsEnum {
	READ, WRITE, CONNECT, DISCONNECT, EXCEPTION, LOGGER, RUNTIME, CREATE_SESSION, DELETE_SESSION, ABORT_SESSION, DECODE_SCMP, ENCODE_SCMP, KEEP_ALIVE
}

/**
 * The Class ConnectionPoint. Allows collecting statistic information - fire read/write, connect/disconnect.
 * 
 * @author JTraber
 */
public final class StatisticsPoint extends ListenerSupport<IStatisticsListener> {

	/** The statistics point. */
	private static StatisticsPoint statisticsPoint = new StatisticsPoint();

	private IConnectionListener connectionListener;
	private IExceptionListener exceptionListener;
	private ILoggerListener loggerListener;
	private ISessionListener sessionListener;
	private ISCMPListener scmpListener;

	/**
	 * Instantiates a new connection point.
	 */
	private StatisticsPoint() {
		this.connectionListener = new StatisticsConnectionListener();
		this.exceptionListener = new StatisticsExceptionListener();
		this.loggerListener = new StatisticsLoggerListener();
		this.sessionListener = new StatisticsSessionListener();
		this.scmpListener = new StatisticsSCMPListener();
	}

	/**
	 * Gets the single instance of StatisticsPoint.
	 * 
	 * @return single instance of StatisticsPoint
	 */
	public static StatisticsPoint getInstance() {
		return statisticsPoint;
	}

	/**
	 * Adds the listener.
	 * 
	 * @param listener
	 *            the listener
	 */
	public synchronized void addListener(IStatisticsListener listener) {
		if (this.isEmpty()) {
			// register connection point
			ConnectionPoint.getInstance().addListener(connectionListener);
			ExceptionPoint.getInstance().addListener(exceptionListener);
			LoggerPoint.getInstance().addListener(loggerListener);
			SessionPoint.getInstance().addListener(sessionListener);
			SCMPPoint.getInstance().addListener(scmpListener);
		}
		super.addListener(listener);
	}

	/**
	 * Removes the listener.
	 * 
	 * @param listener
	 *            the listener
	 */
	public synchronized void removeListener(IStatisticsListener listener) {
		// register connection point
		super.removeListener(listener);
		if (this.isEmpty()) {
			ConnectionPoint.getInstance().removeListener(connectionListener);
			ExceptionPoint.getInstance().removeListener(exceptionListener);
			LoggerPoint.getInstance().removeListener(loggerListener);
			SessionPoint.getInstance().removeListener(sessionListener);
			SCMPPoint.getInstance().removeListener(scmpListener);
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
				ExceptionPoint.getInstance().fireException(this, e);
			}
		}
	}

	/**
	 * The listener interface for receiving statisticsConnection events. The class that is interested in processing a
	 * statisticsConnection event implements this interface, and the object created with that class is registered with a
	 * component using the component's <code>addStatisticsConnectionListener<code> method. When
	 * the statisticsConnection event occurs, that object's appropriate
	 * method is invoked.
	 * 
	 * @see StatisticsConnectionEvent
	 */
	private class StatisticsConnectionListener implements IConnectionListener {
		/** {@inheritDoc} */
		@Override
		public void connectEvent(ConnectionEvent connectionEvent) throws Exception {
			StatisticsEvent statisticsEvent = new StatisticsEvent(connectionEvent.getSource(), connectionEvent);
			statisticsEvent.setEventType(StatisticsEnum.CONNECT);
			fireStatistics(statisticsEvent);
		}

		/** {@inheritDoc} */
		@Override
		public void disconnectEvent(ConnectionEvent connectionEvent) throws Exception {
			StatisticsEvent statisticsEvent = new StatisticsEvent(connectionEvent.getSource(), connectionEvent);
			statisticsEvent.setEventType(StatisticsEnum.DISCONNECT);
			fireStatistics(statisticsEvent);
		}

		/** {@inheritDoc} */
		@Override
		public void readEvent(ConnectionEvent connectionEvent) throws Exception {
			StatisticsEvent statisticsEvent = new StatisticsEvent(connectionEvent.getSource(), connectionEvent);
			statisticsEvent.setEventType(StatisticsEnum.READ);
			fireStatistics(statisticsEvent);
		}

		/** {@inheritDoc} */
		@Override
		public void writeEvent(ConnectionEvent connectionEvent) throws Exception {
			StatisticsEvent statisticsEvent = new StatisticsEvent(connectionEvent.getSource(), connectionEvent);
			statisticsEvent.setEventType(StatisticsEnum.WRITE);
			fireStatistics(statisticsEvent);
		}

		/** {@inheritDoc} */
		@Override
		public void keepAliveEvent(ConnectionEvent connectionEvent) throws Exception {
			StatisticsEvent statisticsEvent = new StatisticsEvent(connectionEvent.getSource(), connectionEvent);
			statisticsEvent.setEventType(StatisticsEnum.KEEP_ALIVE);
			fireStatistics(statisticsEvent);
		}
	}

	/**
	 * The listener interface for receiving statisticsException events. The class that is interested in processing a
	 * statisticsException event implements this interface, and the object created with that class is registered with a
	 * component using the component's <code>addStatisticsExceptionListener<code> method. When
	 * the statisticsException event occurs, that object's appropriate
	 * method is invoked.
	 * 
	 * @see StatisticsExceptionEvent
	 */
	private class StatisticsExceptionListener implements IExceptionListener {

		/** {@inheritDoc} */
		@Override
		public void exceptionEvent(ExceptionEvent exceptionEvent) throws Exception {
			StatisticsEvent statisticsEvent = new StatisticsEvent(exceptionEvent.getSource(), exceptionEvent);
			statisticsEvent.setEventType(StatisticsEnum.EXCEPTION);
			fireStatistics(statisticsEvent);
		}
	}

	/**
	 * The listener interface for receiving statisticsLogger events. The class that is interested in processing a
	 * statisticsLogger event implements this interface, and the object created with that class is registered with a
	 * component using the component's <code>addStatisticsLoggerListener<code> method. When
	 * the statisticsLogger event occurs, that object's appropriate
	 * method is invoked.
	 * 
	 * @see StatisticsLoggerEvent
	 */
	private class StatisticsLoggerListener implements ILoggerListener {

		/** {@inheritDoc} */
		@Override
		public void logEvent(LoggerEvent loggerEvent) throws Exception {
			StatisticsEvent statisticsEvent = new StatisticsEvent(loggerEvent.getSource(), loggerEvent);
			statisticsEvent.setEventType(StatisticsEnum.LOGGER);
			fireStatistics(statisticsEvent);
		}
	}

	/**
	 * The listener interface for receiving statisticsSession events. The class that is interested in processing a
	 * statisticsSession event implements this interface, and the object created with that class is registered with a
	 * component using the component's <code>addStatisticsSessionListener<code> method. When
	 * the statisticsSession event occurs, that object's appropriate
	 * method is invoked.
	 * 
	 * @see StatisticsSessionEvent
	 */
	private class StatisticsSessionListener implements ISessionListener {

		/** {@inheritDoc} */
		@Override
		public void createSessionEvent(SessionEvent sessionEvent) {
			StatisticsEvent statisticsEvent = new StatisticsEvent(sessionEvent.getSource(), sessionEvent);
			statisticsEvent.setEventType(StatisticsEnum.CREATE_SESSION);
			fireStatistics(statisticsEvent);
		}

		/** {@inheritDoc} */
		@Override
		public void deleteSessionEvent(SessionEvent sessionEvent) throws Exception {
			StatisticsEvent statisticsEvent = new StatisticsEvent(sessionEvent.getSource(), sessionEvent);
			statisticsEvent.setEventType(StatisticsEnum.DELETE_SESSION);
			fireStatistics(statisticsEvent);
		}

		/** {@inheritDoc} */
		@Override
		public void abortSessionEvent(SessionEvent sessionEvent) {
			StatisticsEvent statisticsEvent = new StatisticsEvent(sessionEvent.getSource(), sessionEvent);
			statisticsEvent.setEventType(StatisticsEnum.ABORT_SESSION);
			fireStatistics(statisticsEvent);
		}
	}

	/**
	 * The listener interface for receiving statisticsSCMP events. The class that is interested in processing a
	 * statisticsSCMP event implements this interface, and the object created with that class is registered with a
	 * component using the component's <code>addStatisticsSCMPListener<code> method. When
	 * the statisticsSCMP event occurs, that object's appropriate
	 * method is invoked.
	 * 
	 * @see StatisticsSCMPEvent
	 */
	private class StatisticsSCMPListener implements ISCMPListener {

		/** {@inheritDoc} */
		@Override
		public void decodeEvent(SCMPEvent scmpEvent) {
			StatisticsEvent statisticsEvent = new StatisticsEvent(scmpEvent.getSource(), scmpEvent);
			statisticsEvent.setEventType(StatisticsEnum.DECODE_SCMP);
			fireStatistics(statisticsEvent);
		}

		/** {@inheritDoc} */
		@Override
		public void encodeEvent(SCMPEvent scmpEvent) {
			StatisticsEvent statisticsEvent = new StatisticsEvent(scmpEvent.getSource(), scmpEvent);
			statisticsEvent.setEventType(StatisticsEnum.ENCODE_SCMP);
			fireStatistics(statisticsEvent);
		}
	}
}
