package org.serviceconnector.service;

import java.io.InputStream;
import java.net.HttpURLConnection;

import org.serviceconnector.server.FileServer;

public class FileSession extends Session {

	private boolean streaming;
	private String path;
	private HttpURLConnection httpURLConnection;
	private InputStream is;
	private int outstandingDownloadLength;

	public FileSession(String sessionInfo, String ipAddressList, String path) {
		super(sessionInfo, ipAddressList);
		this.streaming = false;
		this.path = path;
		this.outstandingDownloadLength = 0;
		this.is = null;
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

	public void setOutstandingDownloadContentLength(int contentLength) {
		this.outstandingDownloadLength = contentLength;
	}

	public int getOutstandingDownloadLength() {
		return outstandingDownloadLength;
	}
}
