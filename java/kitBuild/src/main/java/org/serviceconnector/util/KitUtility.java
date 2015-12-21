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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.security.InvalidParameterException;

import org.serviceconnector.SCVersion;

/**
 * The Class KitUtility.
 */
public class KitUtility {

	private static final String COPY_RENAME_KIT_TO_VERSION = "copyAndRenameKitToVersion";
	private static final String GENERATE_HASH_FILE = "generateHashFile";
	private static final String ZIP_EXTENSION = ".zip";

	/**
	 * The main method. Programs arguments explanation below.<br>
	 * <br>
	 * args[0] => function to run (copyAndRenameKitToVersion|generateHashFile)
	 * args[1] => kitPath (path of the kit) <br>
	 * args[2] => copyPath (path to copy renamed kit)<br>
	 * <br>
	 * java org.serviceconnector.util.KitUtility copyAndRenameKitToVersion ..\..\..\kit\ ..\..\..\
	 * java org.serviceconnector.util.KitUtility generateHashFile ..\..\..\kit\ <br>
	 * 
	 * @param args
	 *            the arguments
	 * @throws Exception
	 *             the exception
	 */
	public static void main(String[] args) throws Exception {

		if (args.length < 2) {
			throw new InvalidParameterException("Wrong number of program arguments " + args.length + ".");
		}

		String function = args[0];
		if (function.equals(COPY_RENAME_KIT_TO_VERSION)) {
			String kitPath = args[1];
			String copyPath = args[2];
			KitUtility.copyAndRenameKitToVersion(kitPath, copyPath);
		} else if (function.equals(GENERATE_HASH_FILE)) {
			String kitPath = args[1];
			KitUtility.generateHashFile(kitPath);
		} else {
			throw new InvalidParameterException("Invalid function called, function not implemented - function=" + function);
		}
	}

	/**
	 * Copy and rename kit to version.
	 * 
	 * @param kitPath
	 *            the kit path
	 * @param copyPath
	 *            the copy path
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static void copyAndRenameKitToVersion(String kitPath, String copyPath) throws IOException {
		File kitFolder = new File(kitPath);

		if (kitFolder.isDirectory() == false) {
			throw new InvalidParameterException("Invalid kitPath argument(1), no directory - kitpath=" + kitPath);
		}

		File[] zipFiles = kitFolder.listFiles(new KitUtility().new ZipFileFilter());
		for (File zipFile : zipFiles) {
			String fileName = zipFile.getName();

			String renamedFileName = fileName.replace(ZIP_EXTENSION, "_V" + SCVersion.CURRENT.toString() + ZIP_EXTENSION);
			File renamedFile = new File(copyPath + renamedFileName);
			FileUtility.copyFile(zipFile, renamedFile);
			System.out.println(zipFile.getAbsolutePath() + " copied and renamed to " + renamedFile.getAbsolutePath());
		}
	}

	/**
	 * Generate hash file.
	 * 
	 * @param kitPath
	 *            the kit path
	 * @throws Exception
	 *             the exception
	 */
	public static void generateHashFile(String kitPath) throws Exception {
		File kitFolder = new File(kitPath);
		if (kitFolder.isDirectory() == false) {
			throw new InvalidParameterException("Invalid kitPath argument(1), no directory - kitpath=" + kitPath);
		}

		File[] zipFiles = kitFolder.listFiles(new KitUtility().new ZipFileFilter());
		for (File zipFile : zipFiles) {
			String fileName = zipFile.getName().substring(0, zipFile.getName().lastIndexOf('.'));
			if (fileName.endsWith(SCVersion.CURRENT.toString()) == false) {
				// ZIP file not of current release - ignore it!
				continue;
			}
			String hashCode = FileUtility.generateSHA1OfFile(zipFile);
			String filePath = zipFile.getParent();
			String hashfilePath = filePath + File.separatorChar + fileName + "_SHA-1.txt";
			BufferedWriter out = new BufferedWriter(new FileWriter(hashfilePath));
			out.write(hashCode);
			out.close();
			System.out.println("Hash code written to " + hashfilePath);
		}
	}

	/**
	 * The Class ZipFileFilter.
	 */
	private class ZipFileFilter implements FilenameFilter {

		@Override
		public boolean accept(File dir, String name) {
			return name.endsWith(ZIP_EXTENSION);
		}
	}
}
