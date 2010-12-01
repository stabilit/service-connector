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

// TODO: Auto-generated Javadoc
/**
 * The Class Statistics.
 */
public class Statistics {

	/** The Constant logger. */
	private static final Logger logger = Logger.getLogger(Statistics.class);
	
	/** The instance. */
	private static Statistics instance = new Statistics();
	
	/** The total messages. */
	private double totalMessages = 0;
	
	/** The total bytes. */
	private double totalBytes = 0;
	
	/** The startup date time. */
	private Timestamp startupDateTime = new java.sql.Timestamp(Calendar.getInstance().getTime().getTime());
	
	/** The cached messages. */
	private int cachedMessages = 0;
	
	/** The cached bytes. */
	private long cachedBytes = 0;
	
	/** The cached files. */
	private int cachedFiles = 0;
	
	/**
	 * Instantiates a new statistic logger. Private for singelton use.
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
	 * Increment total messages.
	 */
	public synchronized void incrementTotalMessages(long msgLength) {
		totalMessages++;
		totalBytes = totalBytes + msgLength;
	}

	/**
	 * Increment cached messages.
	 */
	public synchronized void incrementCachedMessages(long msgLength) {
		cachedMessages++;
		cachedBytes = cachedBytes + msgLength;
	}

	
	/**
	 * Increment cached messages.
	 */
	public synchronized void decrementCachedMessages(long msgLength) {
		cachedMessages--;
		cachedBytes = cachedBytes - msgLength;
	}

	/**
	 * Increment cached messages.
	 */
	public synchronized void incrementCachedMessagesSize(long msgLength) {
		cachedBytes = cachedBytes + msgLength;
	}

	
	/**
	 * Increment cached messages.
	 */
	public synchronized void decrementCachedMessagesSize(long msgLength) {
		cachedBytes = cachedBytes + msgLength;
	}

	/**
	 * Increment cached files.
	 *
	 * @param nrFiles the nr files
	 */
	public synchronized void incrementCachedFiles(int nrFiles) {
		cachedFiles = cachedFiles + nrFiles;
	}
	
	/**
	 * Decrement cached files.
	 *
	 * @param nrFiles the nr files
	 */
	public synchronized void decrementCachedFiles(int nrFiles) {
		cachedFiles = cachedFiles - nrFiles;
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
		return (current - startup) / 1000;
	}

	/**
	 * Sets the startup date time.
	 *
	 * @param startupDateTime the new startup date time
	 */
	public void setStartupDateTime(Timestamp startupDateTime) {
		this.startupDateTime = startupDateTime;
	}

	/**
	 * Gets the cached messages.
	 *
	 * @return the cached messages
	 */
	public int getCachedMessages() {
		return cachedMessages;
	}

	/**
	 * Gets the cached bytes.
	 *
	 * @return the cached bytes
	 */
	public long getCachedBytes() {
		return cachedBytes;
	}

	/**
	 * Gets the cached files.
	 *
	 * @return the cached files
	 */
	public int getCachedFiles() {
		return cachedFiles;
	}
	
	
	
}
