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

import java.net.MalformedURLException;

import com.stabilit.sc.client.IClientConnection;
import com.stabilit.sc.ctx.ClientConnectionContext;
import com.stabilit.sc.ctx.IClientConnectionContext;
import com.stabilit.sc.exception.ConnectionException;
import com.stabilit.sc.io.SCMP;
import com.stabilit.sc.msg.impl.RoundTripMessage;

/**
 * @author JTraber
 * 
 */
public class RoundTripClient {

	public static void main(String[] args) throws ConnectionException {
		IClientConnectionContext conCtx = null;
		try {
			conCtx = new ClientConnectionContext("localhost", 7777, "netty.tcp");
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		}

		IClientConnection con = null;
		int index = 0;
		int numberofMsgToSend = 1000;
		long startTime = System.currentTimeMillis();
		con = conCtx.connect();
		while (index < numberofMsgToSend) {
			try {
				// Thread.sleep(2000);
				
				// TODO con.createSession();
				SCMP request = new SCMP();
				request.setHeader("serviceName", "service A");
				request.setMessageId("roundTrip");
				RoundTripMessage msg = new RoundTripMessage();
				msg.setAttribute("msg", "hello world " + ++index);				
				request.setBody(msg);
				SCMP response = con.sendAndReceive(request);
				RoundTripMessage roundTrip = (RoundTripMessage) response.getBody();
			//	System.out.println(roundTrip.getAttribute("msg") + " session = " + con.getSessionId());
				// TODO con.deleteSession();
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		con.disconnect();
		long neededTime = System.currentTimeMillis() - startTime;
		float numberOfMsg = numberofMsgToSend / (neededTime/1000f);
		System.out.println(numberOfMsg + " msg's per second!");
	}
}
