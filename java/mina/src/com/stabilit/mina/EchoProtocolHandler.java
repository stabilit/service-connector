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

package com.stabilit.mina;

import java.io.InputStream;
import java.nio.CharBuffer;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.ssl.SslFilter;

/**
 * @author JTraber
 * 
 */
public class EchoProtocolHandler extends IoHandlerAdapter {

	@Override
	public void sessionCreated(IoSession session) {
	//	session.getConfig().setIdleTime(IdleStatus.BOTH_IDLE, 10);

		// We're going to use SSL negotiation notification.
//		session.setAttribute(SslFilter.USE_NOTIFICATION);
	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {
		System.out.println("CLOSED");
	}

	@Override
	public void sessionOpened(IoSession session) throws Exception {
		System.out.println("OPENED");
	}

	@Override
	public void sessionIdle(IoSession session, IdleStatus status) {
		System.out.println("*** IDLE #" + session.getIdleCount(IdleStatus.BOTH_IDLE)
				+ " ***");
	}

	@Override
	public void exceptionCaught(IoSession session, Throwable cause) {
		System.out.println(cause);
		session.close(true);
	}

	@Override
	public void messageReceived(IoSession session, Object message)
			throws Exception {
	//	System.out.println("Received : ");
		session.write(message);
	}
}
