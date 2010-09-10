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
package com.stabilit.scm.common.util;

import java.sql.Timestamp;
import java.util.Calendar;

import org.apache.log4j.Logger;

public class Statistics {

	/** The Constant logger. */
	private static final Logger logger = Logger.getLogger(Statistics.class);
	
	/** The instance. */
	private static Statistics instance = new Statistics();
	
	private double nrMessages = 0;
	private double nrBytes = 0;
	private Timestamp creationDateTime = new java.sql.Timestamp(Calendar.getInstance().getTime().getTime());
	
	/**
	 * Instantiates a new statistic logger. Private for singelton use.
	 */
	private Statistics() {
	}

	public static Statistics getInstance() {
		return instance;
	}
	
	public double getNrMessages() {
		return nrMessages;
	}

	public synchronized void incrementNrMessages() {
		nrMessages++;
	}
	
	public double getNrBytes() {
		return nrBytes;
	}

	public synchronized void addNrBytes(double bytes) {
		nrBytes = nrBytes + bytes;
	}

	
	public Timestamp getCreationDateTime() {
		return creationDateTime;
	}

	
	
	
}
