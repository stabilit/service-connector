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
package org.serviceconnector.service;

import java.io.InputStream;
import java.net.HttpURLConnection;

import org.serviceconnector.server.FileServer;

public class FileSession extends Session {

	private boolean streaming;
	private String path;
	private HttpURLConnection httpURLConnection;
	private InputStream is;
	private String uploadScript;
	private String getFileListScript;

	public FileSession(String sessionInfo, String ipAddressList, String path, String uploadScript, String getFileListScript) {
		super(sessionInfo, ipAddressList);
		this.streaming = false;
		this.path = path;
		this.is = null;
		this.getFileListScript = getFileListScript;
		this.uploadScript = uploadScript;
	}

	/**
	 * Gets the file server.
	 * 
	 * @return the file server
	 */
	public FileServer getFileServer() {
		return (FileServer) this.server;
	}

	/**
	 * Checks if is streaming.
	 * 
	 * @return true, if is streaming
	 */
	public boolean isStreaming() {
		return this.streaming;
	}

	/**
	 * Start streaming.
	 */
	public void startStreaming() {
		this.streaming = true;
	}

	/**
	 * Stop streaming.
	 */
	public void stopStreaming() {
		this.streaming = false;
	}

	/**
	 * Gets the path.
	 * 
	 * @return the path
	 */
	public String getPath() {
		return path;
	}

	public void setHttpUrlConnection(HttpURLConnection httpCon) {
		this.httpURLConnection = httpCon;
	}

	public void setInputStream(InputStream is) {
		this.is = is;
	}

	public HttpURLConnection getHttpURLConnection() {
		return httpURLConnection;
	}

	public InputStream getInputStream() {
		return this.is;
	}
	
	public String getUploadFileScriptName() {
		return this.uploadScript;
	}

	public String getGetFileListScriptName() {
		return this.getFileListScript;
	}
}
