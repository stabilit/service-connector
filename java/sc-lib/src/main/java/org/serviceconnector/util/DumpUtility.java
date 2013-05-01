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

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.Comparator;

import org.apache.log4j.Logger;
import org.serviceconnector.Constants;

/**
 * The Class DumpUtility. This class provides several dump utility functions.
 */
public final class DumpUtility {

	/**
	 * Instantiates a new dump utility.
	 */
	private DumpUtility() {
	}
	
	/** The Constant LOGGER. */
	@SuppressWarnings("unused")
	private static final Logger LOGGER = Logger.getLogger(DumpUtility.class);

	/**
	 * Gets all dump files matching given criterias.
	 * 
	 * @param dumpPath
	 *            the dump path
	 * @return the dump files
	 */
	public static File[] getDumpFiles(String dumpPath) {
		File dumpPathFile = new File(dumpPath);
		FileFilter fileFilter = new WildcardFileFilter(Constants.DUMP_FILE_NAME + "*." + Constants.DUMP_FILE_EXTENSION);
		File[] fileArray = dumpPathFile.listFiles(fileFilter);
		Arrays.sort(fileArray, new Comparator<File>() {
			public int compare(File f1, File f2) {
				return -Long.valueOf(f1.lastModified()).compareTo(f2.lastModified());
			}
		});
		return fileArray;
	}

	/**
	 * Delete all dump files for given dump path.
	 * 
	 * @param dumpPath
	 *            the dump path
	 */
	public static void deleteAllDumpFiles(String dumpPath) {
		File dumpPathFile = new File(dumpPath);
		FileFilter fileFilter = new WildcardFileFilter(Constants.DUMP_FILE_NAME + "*." + Constants.DUMP_FILE_EXTENSION);
		File[] fileArray = dumpPathFile.listFiles(fileFilter);
		for (File file : fileArray) {
			file.delete();
		}
		return;
	}

	/**
	 * Read dump file to writer.
	 * 
	 * @param dumpPath
	 *            the dump path
	 * @param dumpFile
	 *            the dump file
	 * @param writer
	 *            the writer
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static void readDumpFileToWriter(String dumpPath, String dumpFile, Writer writer) throws IOException {
		String filePath = dumpPath + File.separator + dumpFile;
		FileUtility.readFileToWriter(filePath, writer);
	}

}
