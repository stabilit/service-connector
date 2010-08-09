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


/**
 * The Interface ILoggerConfiguratorMXBean. JMX access to configure logging.
 * 
 * @author JTraber
 */
public interface ILoggerConfiguratorMXBean {

	/**
	 * Adds all loggers.
	 */
	public abstract void addAllLoggers();

	/**
	 * Adds the connection listener.
	 */
	public abstract void addConnectionListener();

	/**
	 * Removes the connection listener.
	 */
	public abstract void removeConnectionListener();

	/**
	 * Adds the exception listener.
	 */
	public abstract void addExceptionListener();

	/**
	 * Removes the exception listener.
	 */
	public abstract void removeExceptionListener();

	/**
	 * Adds the top logger listener.
	 */
	public abstract void addTopLoggerListener();

	/**
	 * Removes the top logger listener.
	 */
	public abstract void removeTopLoggerListener();

	/**
	 * Adds the performance listener.
	 */
	public abstract void addPerformanceListener();

	/**
	 * Removes the performance listener.
	 */
	public abstract void removePerformanceListener();

	/**
	 * Removes the session listener.
	 */
	public abstract void removeSessionListener();

	/**
	 * Adds the session listener.
	 */
	public abstract void addSessionListener();

	/**
	 * Adds the statistics listener.
	 */
	public abstract void addStatisticsListener();

	/**
	 * Removes the statistics listener.
	 */
	public abstract void removeStatisticsListener();

	/**
	 * Removes the subscription listener.
	 */
	public abstract void removeSubscriptionListener();

	/**
	 * Adds the subscription listener.
	 */
	public abstract void addSubscriptionListener();
}
