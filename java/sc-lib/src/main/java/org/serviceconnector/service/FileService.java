/*-----------------------------------------------------------------------------*
 *                                                                             *
 *       Copyright � 2010 STABILIT Informatik AG, Switzerland                  *
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
import org.serviceconnector.server.FileServer;

/**
 * The Class PublishService. PublishService is a remote interface in client API to a publish service and provides
 * communication functions.
 */
public class FileService extends Service {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(FileService.class);
	private FileServer server;
	private String path;

	/**
	 * Instantiates a new publish service.
	 * 
	 * @param name
	 *            the name
	 * @param type
	 *            the type
	 */
	public FileService(String name, String path) {
		super(name, ServiceType.FILE_SERVICE);
		this.path = path;
	}

	public FileServer getServer() {
		return server;
	}

	public void setServer(FileServer server) {
		this.server = server;
	}
	
	public String getPath() {
		return path;
	}
}