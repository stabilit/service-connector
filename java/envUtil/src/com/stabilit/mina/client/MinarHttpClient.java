package com.stabilit.mina.client;

import java.net.InetSocketAddress;

import org.apache.mina.common.ConnectFuture;
import org.apache.mina.common.IoHandlerAdapter;
import org.apache.mina.common.IoSession;
import org.apache.mina.transport.socket.nio.SocketConnector;

public class MinarHttpClient {

	public static void main(String[] args) {
		MinarHttpClient client = new MinarHttpClient();
//		for (int i = 0; i < 1; i++) {
//			client.run();
//		}
		client.run();
	}
	
	public void run() {
		SocketConnector socket = new  SocketConnector();
		IoHandlerAdapter handler = new MinarHttpHandler();
		ConnectFuture conn = socket.connect(new InetSocketAddress(8000), handler);
		IoSession session = conn.getSession();
		session.write("Hello World Mina!");
		session.close();	
	}
}
