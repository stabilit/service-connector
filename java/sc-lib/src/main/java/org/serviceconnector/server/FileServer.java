/*
 * Copyright © 2010 STABILIT Informatik AG, Switzerland *
 * *
 * Licensed under the Apache License, Version 2.0 (the "License"); *
 * you may not use this file except in compliance with the License. *
 * You may obtain a copy of the License at *
 * *
 * http://www.apache.org/licenses/LICENSE-2.0 *
 * *
 * Unless required by applicable law or agreed to in writing, software *
 * distributed under the License is distributed on an "AS IS" BASIS, *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. *
 * See the License for the specific language governing permissions and *
 * limitations under the License. *
 */
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
import org.serviceconnector.conf.BasicConfiguration;
import org.serviceconnector.conf.RemoteNodeConfiguration;
import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.scmp.SCMPMessageFault;
import org.serviceconnector.scmp.SCMPPart;
import org.serviceconnector.service.AbstractSession;
import org.serviceconnector.service.FileSession;
import org.serviceconnector.service.Session;

public class FileServer extends Server {

	/** The bas configuration. */
	BasicConfiguration basConf = AppContext.getBasicConfiguration();
	/** The sessions, list of sessions allocated to the server. */
	private List<FileSession> sessions;
	/** The max sessions. */
	private int maxSessions;

	public FileServer(RemoteNodeConfiguration remoteNodeConfiguration, InetSocketAddress socketAddress) {
		super(remoteNodeConfiguration, socketAddress);
		this.sessions = Collections.synchronizedList(new ArrayList<FileSession>());
		this.maxSessions = remoteNodeConfiguration.getMaxSessions();
		this.serverKey = remoteNodeConfiguration.getName();
	}

	public SCMPMessage serverUploadFile(FileSession session, SCMPMessage message, String remoteFileName, int timeoutMillis)
			throws Exception {
		OutputStream out = null;
		HttpURLConnection httpCon = null;

		if (session.isStreaming()) {
			// streaming already started
			httpCon = session.getHttpURLConnection();
			out = httpCon.getOutputStream();
		} else {
			// first stream package arrived - set up URL connection
			String path = session.getPath();
			URL url = new URL("http://" + this.remoteNodeConfiguration.getHost() + ":" + this.remoteNodeConfiguration.getPort()
					+ "/" + path + session.getUploadFileScriptName() + "?name=" + remoteFileName);
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
		}
		try {
			// write the data to the server
			out.write((byte[]) message.getBody());
			out.flush();
		} catch (Exception e) {
			SCMPMessageFault fault = new SCMPMessageFault(e);
			return fault;
		}

		SCMPMessage reply = null;
		if (message.isPart() == false) {
			// last package arrived
			out.close();
			if (httpCon.getResponseCode() != HttpResponseStatus.OK.getCode()) {
				// error handling
				SCMPMessageFault fault = new SCMPMessageFault(SCMPError.UPLOAD_FILE_FAILED, httpCon.getResponseMessage());
				return fault;
			}
			httpCon.disconnect();
			session.stopStreaming();
			reply = new SCMPMessage();
		} else {
			// set up poll request
			reply = new SCMPPart(true);
		}
		return reply;
	}

	public SCMPMessage serverDownloadFile(FileSession session, SCMPMessage message, String remoteFileName, int timeoutSeconds)
			throws Exception {
		InputStream in = null;
		HttpURLConnection httpCon = null;

		if (session.isStreaming()) {
			// streaming already started
			in = session.getInputStream();
		} else {
			// download request arrived - set up URL connection
			String path = session.getPath();
			try {
				URL url = new URL("http://" + this.remoteNodeConfiguration.getHost() + ":" + this.remoteNodeConfiguration.getPort()
						+ "/" + path + remoteFileName);
				httpCon = (HttpURLConnection) url.openConnection();
				httpCon.connect();
				in = httpCon.getInputStream();
			} catch (Exception e) {
				SCMPMessageFault fault = new SCMPMessageFault(SCMPError.SERVER_ERROR, httpCon.getResponseMessage() + " "
						+ e.getMessage());
				return fault;
			}
			// set session to streaming mode
			session.startStreaming();
			session.setHttpUrlConnection(httpCon);
			session.setInputStream(in);
		}
		try {
			// write the data to the client
			SCMPMessage reply = null;
			byte[] fullBuffer = new byte[Constants.MAX_MESSAGE_SIZE];
			int readBytes = in.read(fullBuffer);
			if (readBytes < 0) {
				// this is the end
				reply = new SCMPMessage();
				reply.setBody(new byte[0]);
				in.close();
				session.getHttpURLConnection().disconnect();
				session.stopStreaming();
				return reply;
			}
			// set up part request, no poll request
			reply = new SCMPPart(false);
			reply.setBody(fullBuffer, 0, readBytes);
			return reply;
		} catch (Exception e) {
			SCMPMessageFault fault = new SCMPMessageFault(e);
			return fault;
		}
	}

	public SCMPMessage serverGetFileList(String path, String listScriptName, String serviceName, int timeoutSeconds)
			throws Exception {
		HttpURLConnection httpCon = null;

		URL url = new URL("http://" + this.remoteNodeConfiguration.getHost() + ":" + this.remoteNodeConfiguration.getPort() + "/"
				+ path + listScriptName + "?service=" + serviceName);
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
			SCMPMessageFault fault = new SCMPMessageFault(SCMPError.SERVER_ERROR, httpCon.getResponseMessage() + " "
					+ e.getMessage());
			return fault;
		}
		try {
			// write the data to the client
			SCMPMessage reply = null;
			byte[] fullBuffer = new byte[Constants.MAX_MESSAGE_SIZE];
			int readBytes = in.read(fullBuffer);
			if (readBytes < 0) {
				// this is the end
				reply = new SCMPMessage();
				reply.setBody(new byte[0]);
				in.close();
				httpCon.disconnect();
				return reply;
			}
			// set up part request, no poll request
			reply = new SCMPMessage();
			reply.setBody(fullBuffer, 0, readBytes);
			return reply;
		} catch (Exception e) {
			SCMPMessageFault fault = new SCMPMessageFault(e);
			return fault;
		}
	}

	/** {@inheritDoc} */
	@Override
	public void abortSession(AbstractSession session, String reason) {
		FileSession fileSession = (FileSession) session;
		HttpURLConnection httpURLConnection = fileSession.getHttpURLConnection();
		if (httpURLConnection != null) {
			httpURLConnection.disconnect();
		}
		this.sessions.remove(session);
	}

	public boolean hasFreeSession() {
		return this.sessions.size() < this.maxSessions;
	}

	public void addSession(FileSession session) {
		this.sessions.add(session);
	}

	public void removeSession(Session session) {
		if (this.sessions == null) {
			// might be the case if server got already destroyed
			return;
		}
		this.sessions.remove(session);
	}
}
