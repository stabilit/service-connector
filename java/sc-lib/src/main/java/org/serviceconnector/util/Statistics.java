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
package org.serviceconnector.util;

import java.sql.Timestamp;
import java.util.Calendar;

import org.apache.log4j.Logger;
import org.serviceconnector.Constants;

/**
 * The Class Statistics.
 */
public final class Statistics {

	/** The Constant LOGGER. */
	@SuppressWarnings("unused")
	private static final Logger LOGGER = Logger.getLogger(Statistics.class);

	/** The instance. */
	private static Statistics instance = new Statistics();
	/** The total messages. */
	private double totalMessages = 0;
	/** The total bytes. */
	private double totalBytes = 0;
	/** The startup date time. */
	private Timestamp startupDateTime = new java.sql.Timestamp(Calendar.getInstance().getTime().getTime());
	/** The messages in cache. */
	private int messagesInCache = 0;

	/**
	 * Instantiates a new statistic LOGGER. Private for singleton use.
	 */
	private Statistics() {
	}

	/**
	 * Gets the single instance of Statistics.
	 * 
	 * @return single instance of Statistics
	 */
	public static Statistics getInstance() {
		return instance;
	}

	/**
	 * Increment messages in cache.
	 */
	public void incrementMessagesInCache() {
		this.messagesInCache++;
	}

	/**
	 * Decrement messages in cache.
	 */
	public void decrementMessagesInCache() {
		this.messagesInCache--;
	}

	/**
	 * Increment total messages.
	 * 
	 * @param msgLength
	 *            the msg length
	 */
	public synchronized void incrementTotalMessages(long msgLength) {
		totalMessages++;
		totalBytes = totalBytes + msgLength;
	}

	/**
	 * Gets the total messages.
	 * 
	 * @return the total messages
	 */
	public double getTotalMessages() {
		return totalMessages;
	}

	/**
	 * Gets the total bytes.
	 * 
	 * @return the total bytes
	 */
	public double getTotalBytes() {
		return totalBytes;
	}

	/**
	 * Gets the startup date time.
	 * 
	 * @return the startup date time
	 */
	public Timestamp getStartupDateTime() {
		return startupDateTime;
	}

	/**
	 * Gets the runtime since startup millis.
	 * 
	 * @return the runtime since startup millis
	 */
	public long getRuntimeSinceStartupSeconds() {
		long current = System.currentTimeMillis();
		long startup = this.startupDateTime.getTime();
		return (current - startup) / Constants.SEC_TO_MILLISEC_FACTOR;
	}

	/**
	 * Sets the startup date time.
	 * 
	 * @param startupDateTime
	 *            the new startup date time
	 */
	public void setStartupDateTime(Timestamp startupDateTime) {
		this.startupDateTime = startupDateTime;
	}

	/**
	 * Gets the messages in cache.
	 * 
	 * @return the messages in cache
	 */
	public int getMessagesInCache() {
		return this.messagesInCache;
	}
}
