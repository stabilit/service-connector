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

public class Statistics {

	/** The Constant logger. */
	private static final Logger logger = Logger.getLogger(Statistics.class);
	
	/** The instance. */
	private static Statistics instance = new Statistics();
	
	private double totalMessages = 0;
	private double totalBytes = 0;
	private Timestamp creationDateTime = new java.sql.Timestamp(Calendar.getInstance().getTime().getTime());
	private double cachedMessages = 0;
	private double cachedBytes = 0;
	private int cachedFiles = 0;
	
	/**
	 * Instantiates a new statistic logger. Private for singelton use.
	 */
	private Statistics() {
	}

	public static Statistics getInstance() {
		return instance;
	}
	
	public synchronized void incrementTotalMessages() {
		totalMessages++;
	}

	public synchronized void incrementTotalBytes(double nrBytes) {
		totalBytes = totalBytes + nrBytes;
	}

	public synchronized void incrementCachedMessages() {
		cachedMessages++;
	}

	public synchronized void incrementCachedBytes(double nrBytes) {
		cachedBytes = cachedBytes + nrBytes;
	}

	public synchronized void decrementCachedBytes(double nrBytes) {
		cachedBytes = cachedBytes - nrBytes;
	}

	public synchronized void incrementCachedFiles(int nrFiles) {
		cachedFiles = cachedFiles + nrFiles;
	}
	
	public synchronized void decrementCachedFiles(int nrFiles) {
		cachedFiles = cachedFiles + nrFiles;
	}

	public double getTotalMessages() {
		return totalMessages;
	}

	public double getTotalBytes() {
		return totalBytes;
	}

	public Timestamp getCreationDateTime() {
		return creationDateTime;
	}

	public double getCachedMessages() {
		return cachedMessages;
	}

	public double getCachedBytes() {
		return cachedBytes;
	}

	public int getCachedFiles() {
		return cachedFiles;
	}
	
	
	
}
