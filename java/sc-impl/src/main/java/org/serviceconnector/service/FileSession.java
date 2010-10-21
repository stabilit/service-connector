package org.serviceconnector.service;

import org.serviceconnector.server.FileServer;

public class FileSession extends Session {

	public FileSession(String sessionInfo, String ipAddressList) {
		super(sessionInfo, ipAddressList);
	}

	/**
	 * Gets the file server.
	 * 
	 * @return the file server
	 */
	public FileServer getFileServer() {
		return (FileServer) this.server;
	}
	
}
