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
package org.serviceconnector.service;

import org.apache.log4j.Logger;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.scmp.SCMPMsgType;
import org.serviceconnector.server.FileServer;

/**
 * The Class PublishService. PublishService is a remote interface in client API to a publish service and provides communication
 * functions.
 */
public class FileService extends Service {

	/** The Constant LOGGER. */
	@SuppressWarnings("unused")
	private static final Logger LOGGER = Logger.getLogger(FileService.class);
	
	/** The server. */
	private FileServer server;
	/** The path. */
	private String path;
	/** The sc upload script. */
	private String scUploadScript;
	/** The sc get file list script. */
	private String scGetFileListScript;

	/**
	 * Instantiates a new publish service.
	 * 
	 * @param name
	 *            the name
	 * @param server
	 *            the server
	 * @param path
	 *            the path
	 * @param scUploadScript
	 *            the sc upload script
	 * @param scGetFileListScript
	 *            the sc get file list script
	 */
	public FileService(String name, FileServer server, String path, String scUploadScript, String scGetFileListScript) {
		super(name, ServiceType.FILE_SERVICE);
		this.path = path;
		this.scUploadScript = scUploadScript;
		this.scGetFileListScript = scGetFileListScript;
		this.server = server;
	}

	/**
	 * Gets the path.
	 * 
	 * @return the path
	 */
	public String getPath() {
		return path;
	}

	/**
	 * Gets the server.
	 * 
	 * @return the server
	 */
	public FileServer getServer() {
		return this.server;
	}

	/**
	 * Gets the upload file script name.
	 * 
	 * @return the upload file script name
	 */
	public String getUploadFileScriptName() {
		return this.scUploadScript;
	}

	/**
	 * Gets the gets the file list script name.
	 * 
	 * @return the gets the file list script name
	 */
	public String getGetFileListScriptName() {
		return this.scGetFileListScript;
	}

	/**
	 * Allocate file server and create session.
	 * 
	 * @param session
	 *            the session
	 * @return the file server
	 * @throws Exception
	 *             the exception
	 */
	public synchronized FileServer allocateFileServerAndCreateSession(FileSession session) throws Exception {
		if (this.server.hasFreeSession()) {
			this.server.addSession(session);
			return this.server;
		}
		// no free server available
		NoFreeServerException noFreeSereverExc = new NoFreeServerException(SCMPError.NO_FREE_SERVER, "service=" + this.getName());
		noFreeSereverExc.setMessageType(SCMPMsgType.CLN_CREATE_SESSION);
		throw noFreeSereverExc;
	}
}
