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

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.concurrent.TimeUnit;

import org.serviceconnector.server.FileServer;
import org.serviceconnector.util.XMLDumpWriter;

/**
 * The Class FileSession.
 */
public class FileSession extends Session {

	/** The streaming. */
	private boolean streaming;
	
	/** The path. */
	private String path;
	
	/** The http url connection. */
	private HttpURLConnection httpURLConnection;
	
	/** The is. */
	private InputStream is;
	
	/** The out. */
	private OutputStream out;
	
	/** The upload script. */
	private String uploadScript;
	
	/** The get file list script. */
	private String getFileListScript;

	/**
	 * Instantiates a new file session.
	 * 
	 * @param sessionInfo
	 *            the session info
	 * @param ipAddressList
	 *            the ip address list
	 * @param path
	 *            the path
	 * @param uploadScript
	 *            the upload script
	 * @param getFileListScript
	 *            the get file list script
	 */
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
	 * Dump the session into the xml writer.
	 * 
	 * @param writer
	 *            the writer
	 * @throws Exception
	 *             the exception
	 */
	public void dump(XMLDumpWriter writer) throws Exception {
		writer.writeStartElement("file-session");
		writer.writeAttribute("id", this.getId());
		writer.writeAttribute("sessionInfo", this.getSessionInfo());
		writer.writeAttribute("isCascaded", this.isCascaded());
		writer.writeAttribute("sessionTimeoutSeconds", this.getSessionTimeoutSeconds());
		writer.writeAttribute("hasPendingRequest", this.hasPendingRequest());
		writer.writeAttribute("timeout", this.getTimeout().getDelay(TimeUnit.SECONDS));;
		writer.writeAttribute("isStreaming", this.isStreaming());
		writer.writeAttribute("path", this.getPath());
		writer.writeAttribute("uploadScript", this.uploadScript);
		writer.writeAttribute("getFileListScript", this.getFileListScript);
		writer.writeElement("ipAddressList", this.getIpAddressList());
		this.getService().dump(writer);
		writer.writeEndElement(); // file-session
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
	 * Sets the http url connection.
	 * 
	 * @param httpCon
	 *            the new http url connection
	 */
	public void setHttpUrlConnection(HttpURLConnection httpCon) {
		this.httpURLConnection = httpCon;
	}

	/**
	 * Sets the input stream.
	 * 
	 * @param is
	 *            the new input stream
	 */
	public void setInputStream(InputStream is) {
		this.is = is;
	}

	/**
	 * Gets the http url connection.
	 * 
	 * @return the http url connection
	 */
	public HttpURLConnection getHttpURLConnection() {
		return httpURLConnection;
	}

	/**
	 * Gets the input stream.
	 * 
	 * @return the input stream
	 */
	public InputStream getInputStream() {
		return this.is;
	}

	/**
	 * Gets the upload file script name.
	 * 
	 * @return the upload file script name
	 */
	public String getUploadFileScriptName() {
		return this.uploadScript;
	}

	/**
	 * Gets the gets the file list script name.
	 * 
	 * @return the gets the file list script name
	 */
	public String getGetFileListScriptName() {
		return this.getFileListScript;
	}

	/**
	 * Sets the output stream.
	 * 
	 * @param out
	 *            the new output stream
	 */
	public void setOutputStream(OutputStream out) {
		this.out = out;
	}

	/**
	 * Gets the output steam.
	 * 
	 * @return the output steam
	 */
	public OutputStream getOutputSteam() {
		return this.out;
	}
}
