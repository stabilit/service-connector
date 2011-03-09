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

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.log4j.Appender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
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
import org.serviceconnector.service.FileService;
import org.serviceconnector.service.FileSession;
import org.serviceconnector.service.Session;
import org.serviceconnector.util.HttpClientUploadUtility;
import org.serviceconnector.util.URLUtility;
import org.serviceconnector.util.HttpClientUploadUtility.UploadRunnable;
import org.serviceconnector.web.WebUtil;

/**
 * The Class FileServer.
 */
public class FileServer extends Server {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = Logger.getLogger(FileServer.class);

	/** The Constant LOGS_FILE_SDF. */
	private static final SimpleDateFormat LOGS_FILE_SDF = new SimpleDateFormat(Constants.LOGS_FILE_NAME_FORMAT);

	/** The bas configuration. */
	private BasicConfiguration basConf = AppContext.getBasicConfiguration();
	/** The sessions, list of sessions allocated to the server. */
	private List<FileSession> sessions;
	/** The max sessions. */
	private int maxSessions;

	/**
	 * Instantiates a new file server.
	 * 
	 * @param remoteNodeConfiguration
	 *            the remote node configuration
	 * @param socketAddress
	 *            the socket address
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
	 * @param session
	 *            the session
	 * @param message
	 *            the message
	 * @param remoteFileName
	 *            the remote file name
	 * @param timeoutMillis
	 *            the timeout millis
	 * @return the sCMP message
	 * @throws Exception
	 *             the exception
	 */
	public SCMPMessage serverUploadFile(FileSession session, SCMPMessage message, String remoteFileName, int timeoutMillis)
			throws Exception {
		OutputStream out = null;
		HttpURLConnection httpCon = null;

		if (session.isStreaming()) {
			// streaming already started
			out = session.getOutputSteam();
		} else {
			// first stream package arrived - set up URL connection
			String path = session.getPath();
			URL url = new URL("http://" + this.remoteNodeConfiguration.getHost() + ":" + this.remoteNodeConfiguration.getPort()
					+ "/" + path + session.getUploadFileScriptName() + "?" + Constants.UPLOAD_FILE_PARAM_NAME + "="
					+ remoteFileName + "&" + Constants.UPLOAD_SERVICE_PARAM_NAME + "=" + message.getServiceName());
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
		out.write((byte[]) message.getBody());
		out.flush();

		SCMPMessage reply = null;
		if (message.isPart() == false) {
			// last package arrived
			out.close();
			httpCon = session.getHttpURLConnection();
			if (httpCon.getResponseCode() != HttpResponseStatus.OK.getCode()) {
				// error handling
				SCMPMessageFault fault = new SCMPMessageFault(SCMPError.UPLOAD_FILE_FAILED, httpCon.getResponseMessage());
				LOGGER.warn("Upload file failed " + httpCon.getResponseMessage());
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

	/**
	 * Server download file.
	 * 
	 * @param session
	 *            the session
	 * @param message
	 *            the message
	 * @param remoteFileName
	 *            the remote file name
	 * @param timeoutSeconds
	 *            the timeout seconds
	 * @return the sCMP message
	 * @throws Exception
	 *             the exception
	 */
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
	}

	/**
	 * Server get file list.
	 * 
	 * @param path
	 *            the path
	 * @param listScriptName
	 *            the list script name
	 * @param serviceName
	 *            the service name
	 * @param timeoutSeconds
	 *            the timeout seconds
	 * @return the sCMP message
	 * @throws Exception
	 *             the exception
	 */
	public SCMPMessage serverGetFileList(String path, String listScriptName, String serviceName, int timeoutSeconds)
			throws Exception {
		HttpURLConnection httpCon = null;
		String urlPath = URLUtility.makePath(path, listScriptName);
		urlPath += "?" + Constants.UPLOAD_SERVICE_PARAM_NAME + "=" + serviceName;
		URL url = new URL("http", this.remoteNodeConfiguration.getHost(), this.remoteNodeConfiguration.getPort(), urlPath);
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
			LOGGER.warn("List file request failed " + httpCon.getResponseMessage());
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
			LOGGER.warn("List file failed " + httpCon.getResponseMessage());
			return fault;
		}
	}

	/**
	 * Abort session.
	 * 
	 * @param session
	 *            the session
	 * @param reason
	 *            the reason {@inheritDoc}
	 */
	@Override
	public void abortSession(AbstractSession session, String reason) {
		FileSession fileSession = (FileSession) session;
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
	 * @param session
	 *            the session
	 */
	public void addSession(FileSession session) {
		this.sessions.add(session);
	}

	/**
	 * Removes the session.
	 * 
	 * @param session
	 *            the session
	 */
	public void removeSession(Session session) {
		if (this.sessions == null) {
			// might be the case if server got already destroyed
			return;
		}
		this.sessions.remove(session);
	}

	/**
	 * Upload current log files. This method gets all current log file names and store them all in zip compressed stream.
	 * This stream is loaded up to our file server (this). The uploading path is identified by the file service instance.
	 * The service name identifies the uploading service instance which is part of the zipped file name.
	 * Note: There is no file session required.
	 * 
	 * @param fileService
	 *            the file service
	 * @param serviceName
	 *            the service name
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	public String uploadCurrentLogFiles(FileService fileService, String serviceName) throws Exception {
		Calendar cal = Calendar.getInstance();
		Date now = cal.getTime();
		String logsFileName = null;
		synchronized (LOGS_FILE_SDF) {
			String dateTimeString = LOGS_FILE_SDF.format(now);
			logsFileName = Constants.LOGS_FILE_NAME + serviceName + "_" + dateTimeString + Constants.LOGS_FILE_EXTENSION;
		}
		String urlPath = URLUtility.makePath(fileService.getPath(), fileService.getUploadFileScriptName());
		URL url = new URL("http", this.getHost(), this.getPortNr(), urlPath);
		StringBuilder sb = new StringBuilder();
		sb.append(url.toString());
		sb.append("?");
		sb.append(Constants.UPLOAD_FILE_PARAM_NAME);
		sb.append("=");
		sb.append(logsFileName);
		sb.append("&");
		sb.append(Constants.UPLOAD_SERVICE_PARAM_NAME);
		sb.append("=");
		sb.append(serviceName);
		uploadCurrentLogFilesUsingStream(sb.toString());
		return logsFileName;
	}

	/**
	 * Upload current log files using stream. This methods does the main log file zip and upload job.
	 * All log files of today were written to a zipped stream which is loaded up to this file server instance.
	 * 
	 * @param uploadUri
	 *            the upload uri
	 * @throws Exception
	 *             the exception
	 */
	private void uploadCurrentLogFilesUsingStream(String uploadUri) throws Exception {
		// get all log file names
		List<String> logFiles = this.getCurrentLogFiles();
		if (logFiles.isEmpty()) {
			throw new FileServerException("upload log files failed, no logs files found");
		}
		HttpClientUploadUtility uploadUtility = new HttpClientUploadUtility(uploadUri);
		UploadRunnable uploadRunnable = uploadUtility.startUpload();
		OutputStream os = null;
		ZipOutputStream zos = null;
		try {
			os = uploadRunnable.getOutputStream();
			zos = new ZipOutputStream(os);
			for (String logFile : logFiles) {
				String path = logFile.replace(File.separatorChar, '/');
				InputStream is = WebUtil.loadResource(path);
				if (is == null) {
					continue;
				}
				ZipEntry entry = new ZipEntry(logFile);
				entry.setComment("log file " + logFile);
				zos.putNextEntry(entry);
				try {
					int readBytes = -1;
					byte[] buffer = new byte[1 << 16];
					while ((readBytes = is.read(buffer)) > 0) {
						zos.write(buffer, 0, readBytes);
					}
				} finally {
					if (is != null) {
						try {
							is.close();
						} catch (Exception e) {
							LOGGER.error(e.toString());
						}
					}
				}
				zos.closeEntry();
			}
			zos.close();
		} catch (Exception e) {
			uploadRunnable.close();
			throw e;
		} finally {
			if (zos != null) {
				zos.close();
			}
		}
		Integer ret = uploadRunnable.close();
		if (ret != HttpStatus.SC_OK) {
			throw new FileServerException("upload log files failed, http return code is " + ret);
		}
		return;
	}

	/**
	 * Gets the current log file names in a list. Any distinct filenames will be ignored.
	 * 
	 * @return the current log file in a list
	 */
	private List<String> getCurrentLogFiles() {
		Set<String> distinctLoggerSet = new HashSet<String>();
		List<String> logFileList = new ArrayList<String>();
		Logger rootLogger = LogManager.getRootLogger();
		addLogFiles(rootLogger, logFileList, distinctLoggerSet);
		Enumeration<?> currentLoggers = LogManager.getCurrentLoggers();
		while (currentLoggers.hasMoreElements()) {
			Logger currentLogger = (Logger) currentLoggers.nextElement();
			Enumeration<?> appenders = currentLogger.getAllAppenders();
			if (appenders.hasMoreElements()) {
				addLogFiles(currentLogger, logFileList, distinctLoggerSet);
			}
		}
		return logFileList;
	}

	/**
	 * Adds the log files for given LOGGER instance to the list. Any distinct file names will be ignored.
	 * 
	 * @param logger
	 *            the LOGGER
	 * @param logFileList
	 *            the log file list
	 * @param distinctLoggerSet
	 *            the distinct LOGGER set
	 */
	private void addLogFiles(Logger logger, List<String> logFileList, Set<String> distinctLoggerSet) {
		Enumeration<?> appenders = logger.getAllAppenders();
		while (appenders.hasMoreElements()) {
			Appender appender = (Appender) appenders.nextElement();
			String appenderName = appender.getName();
			if (distinctLoggerSet.contains(appenderName)) {
				continue;
			}
			distinctLoggerSet.add(appenderName);
			if (appender instanceof FileAppender) {
				FileAppender fileAppender = (FileAppender) appender;
				String sFile = fileAppender.getFile();
				File file = new File(sFile);
				if (file.exists() && file.isFile()) {
					logFileList.add(sFile);
				}
			}
		}
	}

	/**
	 * Download and replace.
	 * 
	 * @param fileService
	 *            the file service
	 * @param remoteFile
	 *            the remote file
	 * @param dstFile
	 *            the dst file
	 * @throws Exception
	 *             the exception
	 */
	public void downloadAndReplace(FileService fileService, String remoteFile, File dstFile) throws Exception {
		String path = fileService.getPath();
		String urlPath = URLUtility.makePath(path, remoteFile);
		URL downloadURL = new URL("http", this.getHost(), this.getPortNr(), urlPath);
		FileOutputStream fos = new FileOutputStream(dstFile);
		HttpURLConnection httpCon = (HttpURLConnection) downloadURL.openConnection();
		httpCon.connect();
		InputStream in = httpCon.getInputStream();
		byte[] fullBuffer = new byte[Constants.MAX_MESSAGE_SIZE];
		int readBytes = -1;
		while ((readBytes = in.read(fullBuffer)) > 0) {
			fos.write(fullBuffer, 0, readBytes);
		}
		in.close();
		fos.close();
		httpCon.disconnect();
		return;
	}

	/**
	 * Gets the max sessions.
	 * 
	 * @return the max sessions
	 */
	public int getMaxSessions() {
		return maxSessions;
	}
}
