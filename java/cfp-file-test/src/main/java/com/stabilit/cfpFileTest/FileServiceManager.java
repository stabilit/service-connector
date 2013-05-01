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
package com.stabilit.cfpFileTest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.UnexpectedException;
import java.security.InvalidParameterException;
import java.util.List;

import org.apache.log4j.Logger;
import org.serviceconnector.api.SCServiceException;
import org.serviceconnector.api.cln.SCClient;
import org.serviceconnector.api.cln.SCFileService;
import org.serviceconnector.net.ConnectionType;

/**
 * The Class FileServiceManager.
 */
public class FileServiceManager {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = Logger.getLogger(FileServiceManager.class);

	/**
	 * The main method.
	 * 
	 * @param arguments
	 *            argument 1: host to connect (127.1.1.1)<br />
	 *            argument 2: port to connect (e.g. 7000)<br />
	 *            argument 3: connection type (netty.tcp / netty.http)<br />
	 *            argument 4: service name (file-1)
	 *            argument 5: function to call (list, upload, download)
	 *            optional argument 6: file path of the uploading/downloading file
	 *            optional argument 7: name on server of the uploading/downloading file
	 *            optional argument 8: maximum allowed operation time in seconds
	 * @throws Exception
	 *             Several processing errors.
	 * @throws UnexpectedException
	 *             Wrong number of arguments.
	 */
	public static void main(String[] args) throws Exception {
		FileServiceManager fileUploader = new FileServiceManager();

		if (args.length < 5) {
			throw new InvalidParameterException("Wrong number of arguments: " + args.length);
		}

		String hostToConnect = args[0];
		int portToConnect = Integer.valueOf(args[1]);
		ConnectionType connectionType = ConnectionType.getType(args[2]);
		String serviceName = args[3];
		FILE_SERVICE_FUNCTION function = FILE_SERVICE_FUNCTION.getFileServiceFunction(args[4]);
		if (function == FILE_SERVICE_FUNCTION.NOT_VALID) {
			throw new InvalidParameterException("Wrong file service function in argmuents value=" + args[4]);
		}
		String filePath = null;
		String targetFileName = null;
		int otiInSecs = 0;

		if (function != FILE_SERVICE_FUNCTION.LIST) {
			filePath = args[5];
			targetFileName = args[6];
			otiInSecs = Integer.valueOf(args[7]);
		}

		fileUploader.process(hostToConnect, portToConnect, connectionType, serviceName, function, filePath, targetFileName,
				otiInSecs);
	}

	public void process(String hostToConnect, int portToConnect, ConnectionType conType, String serviceName,
			FILE_SERVICE_FUNCTION function, String filePath, String targetFileName, int otiInSecs) throws Exception {

		SCClient sc = new SCClient(hostToConnect, portToConnect, conType);

		try {
			sc.setMaxConnections(20);
			sc.setKeepAliveIntervalSeconds(30);
			sc.attach();

			LOGGER.info("FileUploader attached to: " + hostToConnect + ":" + portToConnect + " " + conType + " - " + serviceName);
			SCFileService service = sc.newFileService(serviceName);

			switch (function) {
			case LIST:
				list(service);
				break;
			case UPLOAD:
				upload(service, filePath, targetFileName, otiInSecs);
				break;
			case DOWNLOAD:
				download(service, filePath, targetFileName, otiInSecs);
				break;
			}

		} catch (Exception e) {
			LOGGER.error("run", e);
			throw e;
		} finally {
			try {
				sc.detach(); // detaches from SC, stops communication
			} catch (Exception e) {
				LOGGER.error("cleanup", e);
				throw e;
			}
		}
	}

	private void download(SCFileService service, String downFilePath, String downFileName, int otiInSecs)
			throws SCServiceException, IOException {
		File localFile = new File(downFilePath);
		FileOutputStream outStream = new FileOutputStream(localFile);
		LOGGER.info("... Downloading file to: " + downFilePath + " - name on server: " + downFileName);
		long startTime = System.nanoTime();
		service.downloadFile(otiInSecs, downFileName, outStream); // regular download
		long estimatedTime = System.nanoTime() - startTime;
		outStream.close();
		if (estimatedTime < 1000000000) {
			LOGGER.info("File Download COMPLETED within: " + estimatedTime + " nanos");
		} else if (estimatedTime < 60000000000l) {
			int secs = (int) (estimatedTime / 1000000000);
			int nanos = (int) ((estimatedTime % 1000000000));
			LOGGER.info("File Download COMPLETED within: " + secs + " secs " + nanos + " nanos");
		} else {
			int mins = (int) (estimatedTime / 60000000000l);
			int secs = (int) ((estimatedTime % 60000000000l) / 1000000000);
			int nanos = (int) ((estimatedTime % 60000000000l) % 1000000000);
			LOGGER.info("File Download COMPLETED within: " + mins + " mins " + secs + " secs " + nanos + " nanos");
		}
	}

	private void upload(SCFileService service, String upFilePath, String upFileTargetName, int otiInSecs)
			throws FileNotFoundException, SCServiceException {
		File localFile = new File(upFilePath);
		InputStream inpStream = new FileInputStream(localFile);
		long startTime = System.nanoTime();
		LOGGER.info("... Uploading file from: " + upFilePath + " - name on server: " + upFileTargetName);
		service.uploadFile(otiInSecs, upFileTargetName, inpStream); // regular upload
		long estimatedTime = System.nanoTime() - startTime;
		if (estimatedTime < 1000000000) {
			LOGGER.info("File Upload COMPLETED within: " + estimatedTime + " nanos");
		} else if (estimatedTime < 60000000000l) {
			int secs = (int) (estimatedTime / 1000000000);
			int nanos = (int) ((estimatedTime % 1000000000));
			LOGGER.info("File Upload COMPLETED within: " + secs + " secs " + nanos + " nanos");
		} else {
			int mins = (int) (estimatedTime / 60000000000l);
			int secs = (int) ((estimatedTime % 60000000000l) / 1000000000);
			int nanos = (int) ((estimatedTime % 60000000000l) % 1000000000);
			LOGGER.info("File Upload COMPLETED within: " + mins + " mins " + secs + " secs " + nanos + " nanos");
		}

	}

	private void list(SCFileService service) throws SCServiceException {
		long startTime = System.nanoTime();
		List<String> fileNameList = service.listFiles();
		long estimatedTime = System.nanoTime() - startTime;
		if (estimatedTime < 1000000000) {
			LOGGER.info("File Listing COMPLETED within: " + estimatedTime + " nanos");
		} else if (estimatedTime < 60000000000l) {
			int secs = (int) (estimatedTime / 1000000000);
			int nanos = (int) ((estimatedTime % 1000000000));
			LOGGER.info("File Listing COMPLETED within: " + secs + " secs " + nanos + " nanos");
		} else {
			int mins = (int) (estimatedTime / 60000000000l);
			int secs = (int) ((estimatedTime % 60000000000l) / 1000000000);
			int nanos = (int) ((estimatedTime % 60000000000l) % 1000000000);
			LOGGER.info("File Listing COMPLETED within: " + mins + " mins " + secs + " secs " + nanos + " nanos");
		}
		for (String fileName : fileNameList) {
			LOGGER.info("Found file on server: " + fileName);
		}
	}
}