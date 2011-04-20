package org.serviceconnector.util;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.security.InvalidParameterException;

import org.serviceconnector.SCVersion;

/**
 * The Class KitUtility.
 */
public class KitUtility {

	private static final String COPY_RENAME_KIT_TO_VERSION = "copyAndRenameKitToVersion";
	private static final String ZIP_EXTENSION = ".zip";

	/**
	 * The main method.
	 * 
	 * @param args
	 *            the arguments
	 * @throws Exception
	 *             the exception
	 */
	public static void main(String[] args) throws Exception {

		if (args.length != 3) {
			throw new InvalidParameterException("Wrong number of program arguments.");
		}

		String function = args[0];
		if (function.equals(COPY_RENAME_KIT_TO_VERSION)) {
			String kitPath = args[1];
			String copyPath = args[2];
			KitUtility.copyAndRenameKitToVersion(kitPath, copyPath);
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
	 * The Class ZipFileFilter.
	 */
	private class ZipFileFilter implements FilenameFilter {

		@Override
		public boolean accept(File dir, String name) {
			return name.endsWith(ZIP_EXTENSION);
		}
	}
}
