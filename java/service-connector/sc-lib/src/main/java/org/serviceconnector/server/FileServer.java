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
package org.serviceconnector.server;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.serviceconnector.Constants;
import org.serviceconnector.conf.RemoteNodeConfiguration;
import org.serviceconnector.log.SessionLogger;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.scmp.SCMPMessageFault;
import org.serviceconnector.scmp.SCMPPart;
import org.serviceconnector.service.AbstractSession;
import org.serviceconnector.service.FileSession;
import org.serviceconnector.service.Session;
import org.serviceconnector.util.URLUtility;
import org.serviceconnector.util.XMLDumpWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class FileServer.
 */
public class FileServer extends Server {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(FileServer.class);
	/** The sessions, list of sessions allocated to the server. */
	private List<FileSession> sessions;
	/** The max sessions. */
	private int maxSessions;

	/**
	 * Instantiates a new file server.
	 *
	 * @param remoteNodeConfiguration the remote node configuration
	 * @param socketAddress the socket address
	 */
	public FileServer(RemoteNodeConfiguration remoteNodeConfiguration, InetSocketAddress socketAddress) {
		super(remoteNodeConfiguration, socketAddress);
		this.sessions = Collections.synchronizedList(new ArrayList<FileSession>());
		this.maxSessions = remoteNodeConfiguration.getMaxSessions();
		this.serverKey = remoteNodeConfiguration.getName();
	}

	/**
	 * Server upload file.
	 *
	 * @param session the session
	 * @param message the message
	 * @param remoteFileName the remote file name
	 * @param timeoutMillis the timeout milliseconds
	 * @return the sCMP message
	 * @throws Exception the exception
	 */
	public SCMPMessage serverUploadFile(FileSession session, SCMPMessage message, String remoteFileName, int timeoutMillis) throws Exception {
		OutputStream out = null;
		HttpURLConnection httpCon = null;

		if (session.isStreaming()) {
			// streaming already started
			out = session.getOutputSteam();
		} else {
			// first stream package arrived - set up URL connection
			String path = session.getPath();
			URL url = new URL(
					"http://" + this.remoteNodeConfiguration.getHost() + ":" + this.remoteNodeConfiguration.getPort() + Constants.SLASH + path + session.getUploadFileScriptName()
							+ "?" + Constants.UPLOAD_FILE_PARAM_NAME + "=" + remoteFileName + "&" + Constants.UPLOAD_SERVICE_PARAM_NAME + "=" + message.getServiceName());
			LOGGER.debug("file upload url = " + url.toString());
			httpCon = (HttpURLConnection) url.openConnection();
			httpCon.setRequestMethod("PUT");
			httpCon.setDoOutput(true);
			httpCon.setDoInput(true);
			// enable streaming of HTTP
			httpCon.setChunkedStreamingMode(2048);
			httpCon.connect();
			out = httpCon.getOutputStream();
			// set session to streaming mode
			session.startStreaming();
			session.setHttpUrlConnection(httpCon);
			session.setOutputStream(out);
		}
		// write the data to the server
		Object body = message.getBody();
		if (body != null) {
			out.write((byte[]) body);
		}
		out.flush();

		SCMPMessage reply = null;
		if (message.isPart() == false) {
			// last package arrived
			out.close();
			httpCon = session.getHttpURLConnection();
			if (httpCon.getResponseCode() != HttpResponseStatus.OK.getCode()) {
				// error handling - SCMP Version request
				SCMPMessageFault fault = new SCMPMessageFault(message.getSCMPVersion(), SCMPError.FILE_UPLOAD_FAILED, httpCon.getResponseMessage());
				LOGGER.warn("Upload file failed =" + httpCon.getResponseMessage());
				return fault;
			}
			httpCon.disconnect();
			session.stopStreaming();
			// SCMP Version request
			reply = new SCMPMessage(message.getSCMPVersion());
		} else {
			// set up poll request - SCMP Version request
			reply = new SCMPPart(message.getSCMPVersion(), true);
		}
		return reply;
	}

	/**
	 * Server download file.
	 *
	 * @param session the session
	 * @param message the message
	 * @param remoteFileName the remote file name
	 * @param timeoutSeconds the timeout seconds
	 * @return the sCMP message
	 * @throws Exception the exception
	 */
	public SCMPMessage serverDownloadFile(FileSession session, SCMPMessage message, String remoteFileName, int timeoutSeconds) throws Exception {
		InputStream in = null;
		HttpURLConnection httpCon = null;

		if (session.isStreaming()) {
			// streaming already started
			in = session.getInputStream();
		} else {
			// download request arrived - set up URL connection
			String path = session.getPath();
			try {
				URL url = new URL("http://" + this.remoteNodeConfiguration.getHost() + ":" + this.remoteNodeConfiguration.getPort() + Constants.SLASH + path + remoteFileName);
				httpCon = (HttpURLConnection) url.openConnection();
				httpCon.connect();
				in = httpCon.getInputStream();
			} catch (Exception e) {
				// SCMP Version request
				SCMPMessageFault fault = new SCMPMessageFault(message.getSCMPVersion(), SCMPError.SERVER_ERROR, httpCon.getResponseMessage() + " " + e.getMessage());
				LOGGER.warn("Download file request failed " + httpCon.getResponseMessage());
				return fault;
			}
			// set session to streaming mode
			session.startStreaming();
			session.setHttpUrlConnection(httpCon);
			session.setInputStream(in);
		}
		// write the data to the client
		SCMPMessage reply = null;
		byte[] fullBuffer = new byte[Constants.DEFAULT_MESSAGE_PART_SIZE];
		int readBytes = in.read(fullBuffer);
		if (readBytes < 0) {
			// this is the end - SCMP Version request
			reply = new SCMPMessage(message.getSCMPVersion());
			reply.setBody(new byte[0]);
			in.close();
			session.getHttpURLConnection().disconnect();
			session.stopStreaming();
			return reply;
		}
		// set up part request, no poll request - SCMP Version request
		reply = new SCMPPart(message.getSCMPVersion(), false);
		reply.setBody(fullBuffer, 0, readBytes);
		return reply;
	}

	/**
	 * Server get file list.
	 *
	 * @param path the path
	 * @param listScriptName the list script name
	 * @param serviceName the service name
	 * @param timeoutSeconds the timeout seconds
	 * @return the SCMP message
	 * @throws Exception the exception
	 */
	public SCMPMessage serverGetFileList(String path, String listScriptName, SCMPMessage reqMessage, int timeoutSeconds) throws Exception {
		HttpURLConnection httpCon = null;
		String serviceName = reqMessage.getServiceName();
		String urlPath = URLUtility.makePath(path, listScriptName);
		urlPath += Constants.QUESTION_MARK + Constants.UPLOAD_SERVICE_PARAM_NAME + Constants.EQUAL_SIGN + serviceName;
		URL url = new URL(Constants.HTTP, this.remoteNodeConfiguration.getHost(), this.remoteNodeConfiguration.getPort(), urlPath);
		httpCon = (HttpURLConnection) url.openConnection();
		httpCon.setRequestMethod("GET");
		httpCon.setDoOutput(true);
		httpCon.setDoInput(true);

		InputStream in = null;
		try {
			httpCon = (HttpURLConnection) url.openConnection();
			httpCon.connect();
			in = httpCon.getInputStream();
		} catch (Exception e) {
			// SCMP Version request
			SCMPMessageFault fault = new SCMPMessageFault(reqMessage.getSCMPVersion(), SCMPError.SERVER_ERROR, httpCon.getResponseMessage() + " " + e.getMessage());
			LOGGER.warn("List file request failed " + httpCon.getResponseMessage());
			return fault;
		}
		try {
			// write the data to the client
			SCMPMessage reply = null;
			byte[] fullBuffer = new byte[Constants.DEFAULT_MESSAGE_PART_SIZE];
			int readBytes = in.read(fullBuffer);
			if (readBytes < 0) {
				// this is the end - SCMP Version request
				reply = new SCMPMessage(reqMessage.getSCMPVersion());
				reply.setBody(new byte[0]);
				in.close();
				httpCon.disconnect();
				return reply;
			}
			// set up part request, no poll request - SCMP Version request
			reply = new SCMPMessage(reqMessage.getSCMPVersion());
			reply.setBody(fullBuffer, 0, readBytes);
			return reply;
		} catch (Exception e) {
			// SCMP Version request
			SCMPMessageFault fault = new SCMPMessageFault(reqMessage.getSCMPVersion(), e, SCMPError.SERVER_ERROR);
			LOGGER.warn("List file failed " + httpCon.getResponseMessage());
			return fault;
		}
	}

	/**
	 * Abort session.
	 *
	 * @param session the session
	 * @param reason the reason
	 */
	@Override
	public void abortSession(AbstractSession session, String reason) {
		FileSession fileSession = (FileSession) session;
		SessionLogger.logAbortSession(fileSession, reason);
		HttpURLConnection httpURLConnection = fileSession.getHttpURLConnection();
		if (httpURLConnection != null) {
			httpURLConnection.disconnect();
		}
		this.sessions.remove(session);
	}

	/**
	 * Checks for free session.
	 *
	 * @return true, if successful
	 */
	public boolean hasFreeSession() {
		return this.sessions.size() < this.maxSessions;
	}

	/**
	 * Adds the session.
	 *
	 * @param session the session
	 */
	public void addSession(FileSession session) {
		this.sessions.add(session);
	}

	/**
	 * Removes the session.
	 *
	 * @param session the session
	 */
	public void removeSession(Session session) {
		if (this.sessions == null) {
			// might be the case if server got already destroyed
			return;
		}
		this.sessions.remove(session);
	}

	/**
	 * Gets the max sessions.
	 *
	 * @return the max sessions
	 */
	public int getMaxSessions() {
		return maxSessions;
	}

	/**
	 * Dump the server into the xml writer.
	 *
	 * @param writer the writer
	 * @throws Exception the exception
	 */
	@Override
	public void dump(XMLDumpWriter writer) throws Exception {
		writer.writeStartElement("file-server");
		writer.writeAttribute("key", this.serverKey);
		writer.writeAttribute("socketAddress", this.socketAddress.getHostName() + Constants.SLASH + this.socketAddress.getPort());
		writer.writeAttribute("operationTimeoutMultiplier", this.operationTimeoutMultiplier);
		writer.writeAttribute("maxSessions", this.maxSessions);
		this.requester.dump(writer);
		writer.writeStartElement("sessions");
		List<FileSession> sessionList = this.sessions;
		for (FileSession session : sessionList) {
			session.dump(writer);
		}
		writer.writeEndElement(); // end of sessions
		writer.writeEndElement(); // end of file-server
	}
}
