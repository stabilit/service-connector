/*
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
 */
package org.serviceconnector.util;

import java.io.File;
import java.util.concurrent.TimeoutException;

import org.apache.log4j.Logger;

public class FileUtility {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(FileUtility.class);

	/**
	 * Checks if the file containing the PID exists. Is used for testing purpose to verify that SC is running properly.
	 */
	public static boolean exists(String filename, int nrSeconds) throws Exception {
		File file = new File(filename);
		for (int i = 0; i < nrSeconds; i++) {
			if (file.exists()) {
				return true;
			}
			Thread.sleep(1000);
		}
		throw new TimeoutException("File:" + filename + " does not exist within " + nrSeconds + " seconds.");
	}

	/**
	 * Checks if the file containing the PID does not exist. Is used for testing purpose to verify that SC is running properly.
	 */
	public static boolean notExists(String filename, int nrSeconds) throws Exception {
		File file = new File(filename);
		for (int i = 0; i < nrSeconds; i++) {
			if (!file.exists()) {
				return true;
			}
			Thread.sleep(1000);
		}
		throw new TimeoutException("File:" + filename + " does still exist within " + nrSeconds + " seconds.");
	}
}
