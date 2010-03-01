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

import com.stabilit.sc.app.IApplication;
import com.stabilit.sc.app.client.echo.DefaultEventListener;
import com.stabilit.sc.app.server.ServerApplicationFactory;
import com.stabilit.sc.context.ClientApplicationContext;
import com.stabilit.sc.exception.ScConnectionException;
import com.stabilit.sc.io.SCMP;
import com.stabilit.sc.msg.IData;
import com.stabilit.sc.msg.IMessage;
import com.stabilit.sc.msg.impl.EchoMessage;
import com.stabilit.sc.pool.ConnectionPool;
import com.stabilit.sc.pool.IPoolConnection;

/**
 * The Class ServerScConnection, represents a connection between a Server and a Sc.
 * 
 * @author JTraber
 */
public class ServerScConnection {

	private IApplication serviceServer;

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
		serviceServer = ServerApplicationFactory.newInstance("netty.http");
	}

	public void register(String serviceName, int readTimeout, int writeTimeout) throws ServiceServerException {
		try {
			serviceServer.run();
		} catch (Exception e1) {
			e1.printStackTrace();
		}	
		ClientApplicationContext applicationContext = null;
		//TODO applicationContext connnection type setzten .. ohne keep alive..  
		ConnectionPool pool = ConnectionPool.getInstance();
		IPoolConnection con = pool.borrowConnection(applicationContext, DefaultEventListener.class);
		if (con == null) {
			throw new ServiceServerException("no client available");
		}
		int index = 0;
		// TODO register handshake!!!
		while (true) {
			try {
				// Thread.sleep(2000);
				SCMP request = new SCMP();
				IMessage message = new EchoMessage();
				request.setBody(message);
				message.setAttribute("msg", "hello " + ++index);
				SCMP response = con.sendAndReceive(request);
				IMessage echoed = (IMessage) response.getBody();
				System.out.println(echoed + " session = " + con.getSessionId());
				con.releaseConnection();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
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
