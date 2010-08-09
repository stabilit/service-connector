/*
 *-----------------------------------------------------------------------------*
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
 *-----------------------------------------------------------------------------*
/*
/**
 * 
 */
package com.stabilit.scm.sc;

import com.stabilit.scm.common.conf.CommunicatorConfigPool;
import com.stabilit.scm.common.listener.ConnectionPoint;
import com.stabilit.scm.common.listener.DefaultStatisticsListener;
import com.stabilit.scm.common.listener.ExceptionPoint;
import com.stabilit.scm.common.listener.IConnectionListener;
import com.stabilit.scm.common.listener.IExceptionListener;
import com.stabilit.scm.common.listener.ILoggerListener;
import com.stabilit.scm.common.listener.IPerformanceListener;
import com.stabilit.scm.common.listener.ISessionListener;
import com.stabilit.scm.common.listener.IStatisticsListener;
import com.stabilit.scm.common.listener.LoggerPoint;
import com.stabilit.scm.common.listener.PerformancePoint;
import com.stabilit.scm.common.listener.SessionPoint;
import com.stabilit.scm.common.listener.StatisticsPoint;
import com.stabilit.scm.common.log.Level;
import com.stabilit.scm.common.log.impl.ConnectionLogger;
import com.stabilit.scm.common.log.impl.ExceptionLogger;
import com.stabilit.scm.common.log.impl.LoggerFactory;
import com.stabilit.scm.common.log.impl.PerformanceLogger;
import com.stabilit.scm.common.log.impl.SessionLogger;
import com.stabilit.scm.common.log.impl.TopLogger;

/**
 * The Class LoggerConfigurator. Logger configurator for handling logging. Interface gives access for JXM console.
 * 
 * @author JTraber
 */
public class LoggerConfigurator implements ILoggerConfiguratorMXBean {

	/** The logger factory. */
	private LoggerFactory loggerFactory;
	/** The connection listener. */
	private IConnectionListener connectionListener;
	/** The exception listener. */
	private IExceptionListener exceptionListener;
	/** The logger listener. */
	private ILoggerListener loggerListener;
	/** The performance listener. */
	private IPerformanceListener performanceListener;
	/** The session listener. */
	private ISessionListener sessionListener;
	/** The statistics listener. */
	private IStatisticsListener statisticsListener;

	/**
	 * Instantiates a new logger configurator.
	 * 
	 * @param config
	 *            the configuration
	 */
	public LoggerConfigurator(CommunicatorConfigPool config) {
		this.loggerFactory = LoggerFactory.getCurrentLoggerFactory(config.getLoggerKey());
	}

	/** {@Inherited} */
	@Override
	public void addAllLoggers() {
		this.addStatisticsListener();
		this.addConnectionListener();
		this.addExceptionListener();
		this.addPerformanceListener();
		this.addSessionListener();
		this.addTopLoggerListener();
	}

	/** {@Inherited} */
	@Override
	public void addConnectionListener() {
		if (this.connectionListener != null) {
			// connectionListener is already turned on - no action necessary
			return;
		}
		this.connectionListener = (IConnectionListener) loggerFactory.newInstance(ConnectionLogger.class);
		ConnectionPoint.getInstance().addListener(this.connectionListener);
	}

	/** {@Inherited} */
	@Override
	public void removeConnectionListener() {
		if (this.connectionListener == null) {
			// connectionListener has not been turned on - no action necessary
			return;
		}
		ConnectionPoint.getInstance().removeListener(this.connectionListener);
		this.connectionListener = null;
	}

	/** {@Inherited} */
	@Override
	public void addExceptionListener() {
		if (this.exceptionListener != null) {
			// exceptionListener is already turned on - no action necessary
			return;
		}
		this.exceptionListener = (IExceptionListener) loggerFactory.newInstance(ExceptionLogger.class);
		ExceptionPoint.getInstance().addListener(this.exceptionListener);
	}

	/** {@Inherited} */
	@Override
	public void removeExceptionListener() {
		if (this.exceptionListener == null) {
			// exceptionListener has not been turned on - no action necessary
			return;
		}
		ExceptionPoint.getInstance().removeListener(this.exceptionListener);
		this.exceptionListener = null;
	}

	/** {@Inherited} */
	@Override
	public void addTopLoggerListener() {
		if (this.loggerListener != null) {
			// loggerListener is already turned on - no action necessary
			return;
		}
		this.loggerListener = (ILoggerListener) loggerFactory.newInstance(TopLogger.class);
		LoggerPoint.getInstance().addListener(this.loggerListener);
		this.setTopLoggerLevel(Level.DEBUG);
	}

	/** {@Inherited} */
	@Override
	public void removeTopLoggerListener() {
		if (this.loggerListener == null) {
			// loggerListener has not been turned on - no action necessary
			return;
		}
		LoggerPoint.getInstance().removeListener(this.loggerListener);
		this.loggerListener = null;
	}

	/**
	 * Sets the top logger level.
	 *
	 * @param level the new top logger level
	 */
	private void setTopLoggerLevel(Level level) {
		LoggerPoint.getInstance().setLevel(level);
	}

	/** {@Inherited} */
	@Override
	public void addPerformanceListener() {
		if (this.performanceListener != null) {
			// performanceListener is already turned on - no action necessary
			return;
		}
		this.performanceListener = (IPerformanceListener) loggerFactory.newInstance(PerformanceLogger.class);
		PerformancePoint.getInstance().addListener(this.performanceListener);
		PerformancePoint.getInstance().setOn(true);
	}

	/** {@Inherited} */
	@Override
	public void removePerformanceListener() {
		if (this.performanceListener == null) {
			// performanceListener has not been turned on - no action necessary
			return;
		}
		PerformancePoint.getInstance().removeListener(this.performanceListener);
		this.performanceListener = null;
		PerformancePoint.getInstance().setOn(false);
	}

	/** {@Inherited} */
	@Override
	public void addSessionListener() {
		if (this.sessionListener != null) {
			// sessionListener is already turned on - no action necessary
			return;
		}
		this.sessionListener = (ISessionListener) loggerFactory.newInstance(SessionLogger.class);
		SessionPoint.getInstance().addListener(this.sessionListener);
	}

	/** {@Inherited} */
	@Override
	public void removeSessionListener() {
		if (this.sessionListener == null) {
			// sessionListener has not been turned on - no action necessary
			return;
		}
		SessionPoint.getInstance().removeListener(this.sessionListener);
		this.sessionListener = null;
	}

	/** {@Inherited} */
	@Override
	public void addStatisticsListener() {
		if (this.statisticsListener != null) {
			// statisticsListener is already turned on - no action necessary
			return;
		}
		this.statisticsListener = new DefaultStatisticsListener();
		StatisticsPoint.getInstance().addListener(this.statisticsListener);
	}

	/** {@Inherited} */
	@Override
	public void removeStatisticsListener() {
		if (this.statisticsListener == null) {
			// statisticsListener has not been turned on - no action necessary
			return;
		}
		StatisticsPoint.getInstance().removeListener(this.statisticsListener);
		this.statisticsListener = null;
	}
}