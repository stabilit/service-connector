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
package com.stabilit.sc.client.example;

import com.stabilit.sc.client.IClientConnection;
import com.stabilit.sc.client.factory.ClientConnectionFactory;
import com.stabilit.sc.exception.ConnectionException;
import com.stabilit.sc.io.SCMP;
import com.stabilit.sc.io.SCMPMsgType;

/**
 * @author JTraber
 * 
 */
public class EchoStringClient {

	public static void main(String[] args) throws ConnectionException {
		IClientConnection clientConnection = null;
		ClientConnectionFactory clientConnectionFactory = new ClientConnectionFactory();
		clientConnection = clientConnectionFactory.newInstance("netty.tcp");
		clientConnection.setHost("localhost");
		clientConnection.setPort(7777);
		
		int index = 0;
		int numberofMsgToSend = 10000;
		// Thread.sleep(2000);
		clientConnection.connect();
		long startTime = System.currentTimeMillis();
		while (index < numberofMsgToSend) {
			try {

				// TODO con.createSession();
				SCMP request = new SCMP();
				request.setMessageType(SCMPMsgType.REQ_ECHO.getRequestName());
				// request.setHeader("serviceName", "service A");
				String msg = "hello world " + ++index;

				request.setBody(msg);
				SCMP response = clientConnection.sendAndReceive(request);
				String roundTrip = (String) response.getBody();
				// System.out.println(roundTrip + " session = " + con.getSessionId());

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		long neededTime = System.currentTimeMillis() - startTime;
		float numberOfMsg = numberofMsgToSend / (neededTime / 1000f);
		System.out.println(numberOfMsg + " msg's per second!");
		// TODO con.deleteSession();
		clientConnection.disconnect();
	}
}
