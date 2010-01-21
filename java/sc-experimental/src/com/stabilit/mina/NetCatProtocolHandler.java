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

import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

/**
 * @author JTraber
 * 
 */
public class NetCatProtocolHandler extends IoHandlerAdapter {

	@Override
	public void sessionOpened(IoSession session) {
		// Set reader idle time to seconds.
		// sessionIdle(...) method will be invoked when no data is read
		// for seconds.
		// session.getConfig().setIdleTime(IdleStatus.READER_IDLE, 10);
	}

	@Override
	public void sessionClosed(IoSession session) {
		// Print out total number of bytes read from the remote peer.
		System.err.println("Total " + session.getReadBytes() + " byte(s)");
	}

	@Override
	public void sessionIdle(IoSession session, IdleStatus status) {
		// Close the connection if reader is idle.
		// if (status == IdleStatus.READER_IDLE) {
		// session.close(true);
		// }
	}

	@Override
	public void messageReceived(IoSession session, Object message) {
//		System.out.println("yes");
		CharsetEncoder encoder = Charset.forName("UTF-8").newEncoder();
		if(Main.count != (Main.numberOfMsg-1)) {
			IoBuffer buf = IoBuffer.allocate(128);
			try {
				buf.putString("Hello World! ", encoder);
			} catch (CharacterCodingException e) {
				e.printStackTrace();
			}
			session.write(buf);
			Main.count++;
		} else {
			long endTime = System.currentTimeMillis();
			long neededTime = endTime - Main.startTime;
			System.out.println("Job Done in: " + neededTime + " Ms");
			
			double neededSeconds = neededTime / 1000d;
			System.out.println((Main.numberOfMsg * 1 / neededSeconds)
					+ " Messages in 1 second!");
		}	
	}
}
