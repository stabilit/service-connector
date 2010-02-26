/*
 *-----------------------------------------------------------------------------*
 *                            Copyright © 2010 by                              *
 *                    STABILIT Informatik AG, Switzerland                      *
 *                            ALL RIGHTS RESERVED                              *
 *                                                                             *
 * Valid license from STABILIT is required for possession, use or copying.     *
 * This software or any other copies thereof may not be provided or otherwise  *
 * made available to any other person. No title to and ownership of the        *
 * software is hereby transferred. The information in this software is subject *
 * to change without notice and should not be construed as a commitment by     *
 * STABILIT Informatik AG.                                                     *
 *                                                                             *
 * All referenced products are trademarks of their respective owners.          *
 *-----------------------------------------------------------------------------*
 */
package com.stabilit.sc;

import java.io.InputStream;

import com.stabilit.sc.app.server.IServiceServerConnection;
import com.stabilit.sc.exception.ScConnectionException;
import com.stabilit.sc.msg.IData;
import com.stabilit.sc.serviceserver.IServiceServer;
import com.stabilit.sc.serviceserver.ServerServiceFactory;
import com.stabilit.sc.serviceserver.ServiceServerException;
import com.stabilit.sc.serviceserver.handler.IResponseHandler;
import com.stabilit.sc.serviceserver.handler.ITimeoutHandler;

/**
 * The Class ServerScConnection, represents a connection between a Server and a Sc.
 * 
 * @author JTraber
 */
public class ServerScConnection {

	private IServiceServer serviceServer;

	/**
	 * The Constructor.
	 * 
	 * @param scHost
	 *            the sc host
	 * @param scPort
	 *            the sc port
	 * @param scProtocol
	 *            used protocol
	 * @param numOfConnections
	 *            the number of connections used by Sc
	 */
	public ServerScConnection(String scHost, int scPort, String connectionType) {
		serviceServer = ServerServiceFactory.newInstance(connectionType);
	}

	public void register(String serviceName, Class<? extends IResponseHandler<IServiceServerConnection>> responseHandlerClass,
			Class<? extends ITimeoutHandler> timeoutHandlerClass, int keepAliveTimeout, int readTimeout,
			int writeTimeout) throws ServiceServerException {
		serviceServer.start(serviceName, responseHandlerClass, timeoutHandlerClass, keepAliveTimeout, readTimeout, writeTimeout);
	}

	/**
	 * Publish message.
	 * 
	 * @param responseMessage
	 *            the response message
	 * @param compression
	 *            the compression
	 * 
	 * @throws ScConnectionException
	 *             connection exception in publish process
	 */
	public void publish(IData responseMessage, boolean compression) throws ScConnectionException {
	}

	/**
	 * Download.
	 * 
	 * @param fileName
	 *            the file name
	 * @param timeout
	 *            the timeout
	 * 
	 * @return the input stream
	 */
	public InputStream downloadFile(String fileName, int timeout) {
		return null;
	}

	/**
	 * Upload.
	 * 
	 * @param uploadFile
	 *            the upload file
	 */
	public void uploadFile(InputStream uploadFile) {
	}
}
