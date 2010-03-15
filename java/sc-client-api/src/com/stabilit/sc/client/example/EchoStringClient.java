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
import com.stabilit.sc.io.SCMP;

/**
 * @author JTraber
 * 
 */
public class EchoStringClient {

	public static void main(String[] args) {
		IClientConnectionContext conCtx = null;
		try {
			conCtx = new ClientConnectionContext("localhost", 81, "netty.tcp");
			conCtx.setPoolSize(4); // optional
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		}

		IClientConnection con = null;
		int index = 0;
		while (true) {
			try {
				// Thread.sleep(2000);
				con = conCtx.connect();
				// TODO con.createSession();
				SCMP request = new SCMP();
				request.setMessageId("roundTrip");
				String msg = "hello " + ++index;
				request.setBody(msg);
				SCMP response = con.sendAndReceive(request);
				String roundTrip = (String) response.getBody();
				System.out.println(roundTrip + " session = " + con.getSessionId());
				// TODO con.deleteSession();
				con.disconnect();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
