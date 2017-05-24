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
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class FileCtx.
 */
public class FileCtx {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(FileCtx.class);

	/** The lock. */
	private FileLock lock;
	/** The file channel. */
	private FileChannel fileChannel;
	/** The file. */
	private File file;

	/**
	 * Instantiates a new file ctx.
	 *
	 * @param lock the lock
	 * @param fileChannel the file channel
	 */
	public FileCtx(FileLock lock, FileChannel fileChannel, File file) {
		this.lock = lock;
		this.fileChannel = fileChannel;
		this.file = file;
	}

	public void releaseFileLockAndCloseChannel() {
		try {
			this.lock.release();
			Thread.sleep(300);
		} catch (Exception e) {
			LOGGER.debug("Releasing file lock failed", e);
		}
		try {
			this.fileChannel.force(true);
		} catch (IOException e) {
			LOGGER.debug("Forcing file channel failed", e);
		}
		try {
			this.fileChannel.close();
			Thread.sleep(300);
		} catch (Exception e) {
			LOGGER.debug("Closing file channel failed", e);
		}
	}

	/**
	 * Gets the file.
	 *
	 * @return the file
	 */
	public File getFile() {
		return file;
	}
}
