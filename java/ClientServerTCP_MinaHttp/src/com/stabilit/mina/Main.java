/*
 *-----------------------------------------------------------------------------*
 *                            Copyright © 20by                              *
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
package com.stabilit.mina;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

/**
 * @author JTraber
 * 
 */
public class Main {

	public static long startTime;
	public static int count = 0;
	public static int numberOfMsg = 10000;

	public static void main(String[] args) throws Exception {
		// Create TCP/IP connector.
		NioSocketConnector connector = new NioSocketConnector();

		connector.setHandler(new NetCatProtocolHandler());
		ConnectFuture cf = connector.connect(new InetSocketAddress("localhost",
				Integer.parseInt("5678")));

		cf.awaitUninterruptibly();
		IoSession session = cf.getSession();

		CharsetEncoder encoder = Charset.forName("UTF-8").newEncoder();
		
		IoBuffer buf = IoBuffer.allocate(128);
		buf.putString("Hello World! ", encoder);
		Main.startTime = System.currentTimeMillis();

		session.write(buf);
	}
}
