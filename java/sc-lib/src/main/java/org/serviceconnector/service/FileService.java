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

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(FileService.class);
	private FileServer server;
	private String path;

	private String scUploadScript;
	private String scGetFileListScript;

	/**
	 * Instantiates a new publish service.
	 * 
	 * @param name
	 *            the name
	 * @param type
	 *            the type
	 */
	public FileService(String name, String path, String scUploadScript, String scGetFileListScript) {
		super(name, ServiceType.FILE_SERVICE);
		this.path = path;
		this.scUploadScript = scUploadScript;
		this.scGetFileListScript = scGetFileListScript;
	}

	public void setServer(FileServer server) {
		this.server = server;
	}

	public String getPath() {
		return path;
	}

	public FileServer getServer() {
		return this.server;
	}

	public String getUploadFileScriptName() {
		return this.scUploadScript;
	}

	public String getGetFileListScriptName() {
		return this.scGetFileListScript;
	}

	public synchronized FileServer allocateFileServerAndCreateSession(FileSession session) throws Exception {
		if (this.server.hasFreeSession()) {
			this.server.addSession(session);
			return this.server;
		}
		// no free server available
		NoFreeServerException noFreeSereverExc = new NoFreeServerException(SCMPError.NO_FREE_SERVER, "service="
				+ this.getServiceName());
		noFreeSereverExc.setMessageType(SCMPMsgType.CLN_CREATE_SESSION);
		throw noFreeSereverExc;
	}
}
