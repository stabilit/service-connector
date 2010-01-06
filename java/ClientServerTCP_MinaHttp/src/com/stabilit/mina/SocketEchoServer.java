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

import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.transport.socket.SocketAcceptor;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

/**
 * @author JTraber
 * 
 */
public class SocketEchoServer {

	public static void main(String[] args) throws Exception {
		SocketAcceptor acceptor = new NioSocketAcceptor();
	
   

        acceptor.setHandler(  new EchoProtocolHandler() );

//        acceptor.getSessionConfig().setReadBufferSize( 2048 );
//        acceptor.getSessionConfig().setIdleTime( IdleStatus.BOTH_IDLE, 10 );

		
		acceptor.bind(new InetSocketAddress(5678));
		

		System.out.println("Listening on port " + 5678);		
	}

}
