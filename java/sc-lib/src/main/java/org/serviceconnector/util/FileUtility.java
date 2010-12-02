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

/**
 * The Class FileUtility.
 */
public class FileUtility {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(FileUtility.class);


	/**
	 *
	 * @param filename
	 * @return true if the given file exists
	 */
	public static boolean exists(String filename) {
		File file = new File(filename);
		if (file.exists()) {
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 *
	 * @param filename
	 * @return true if the given file does not exist
	 */
	public static boolean notExists(String filename){
		return !exists(filename);
	}
	

	/**
	 * 
	 * @param filename to look for
	 * @param nrSeconds to wait (check is done in 1 second interval)
	 * @throws Exception if the file does not exist after the given time
	 */
	public static void waitExists(String filename, int nrSeconds) throws Exception {
		for (int i = 0; i < (nrSeconds*10); i++) {
			if (exists(filename)) {
				return;
			}
			Thread.sleep(10);
		}
		throw new TimeoutException("File:" + filename + " does not exist after " + nrSeconds + " seconds timeout.");
	}


	/**
	 * 
	 * @param filename to look for
	 * @param nrSeconds to wait (check is done in 1 second interval)
	 * @throws Exception if the file still exists after the given time
	 */
	public static void waitNotExists(String filename, int nrSeconds) throws Exception {
		for (int i = 0; i < (nrSeconds*10); i++) {
			if (notExists(filename)) {
				return;
			}
			Thread.sleep(10);
		}
		throw new TimeoutException("File:" + filename + " does still exist after " + nrSeconds + " seconds timeout.");
	}
}
