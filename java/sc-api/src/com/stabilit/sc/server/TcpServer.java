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
/**
 * 
 */
package com.stabilit.sc.server;

import org.apache.log4j.Logger;

import com.stabilit.sc.app.client.ClientConnectionFactory;
import com.stabilit.sc.app.server.ITcpServerConnection;
import com.stabilit.sc.client.IClientConnection;
import com.stabilit.sc.context.ServerConnectionContext;
import com.stabilit.sc.exception.ConnectionException;
import com.stabilit.sc.io.SCMP;
import com.stabilit.sc.msg.IMessage;
import com.stabilit.sc.msg.impl.RegisterMessage;
import com.stabilit.sc.net.server.netty.tcp.NettyTcpServerConnectionThread;

/**
 * @author JTraber
 * 
 */
class TcpServer extends Server {

	private ITcpServerConnection con;

	/**
	 * @param serviceName
	 * @param connectionCtx
	 */
	protected TcpServer(String serviceName, ServerConnectionContext connectionCtx) {
		super(serviceName, connectionCtx);
		con = (ITcpServerConnection) connectionCtx.create();
	}

	Logger log = Logger.getLogger(TcpServer.class);

	@Override
	public void publish(SCMP scmp, int timeout, boolean compression) {
	}

	@Override
	public void connect() {
		Thread tcpServer = new Thread(new NettyTcpServerConnectionThread(con));
		tcpServer.start();
		IClientConnection clientCon = ClientConnectionFactory.newInstance("nettyRegister.tcp");
		try {
			clientCon.connect(connectionCtx.getSCHost(), connectionCtx.getSCPort());

			SCMP scmp = new SCMP();
			scmp.setMessageId("register");
			IMessage msg = new RegisterMessage();
			msg.setAttribute("test", "tester");
			scmp.setHeader("serviceName", "service A");
			scmp.setBody(msg);

			SCMP scmpRes = clientCon.sendAndReceive(scmp);
			
			if (scmpRes.getMessageId().equals("register"))
				System.out.println("server registered!");

		} catch (ConnectionException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() throws Exception {
		con.run();
	}
}
