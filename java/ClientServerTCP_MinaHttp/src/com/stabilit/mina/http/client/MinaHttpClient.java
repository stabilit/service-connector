package com.stabilit.mina.http.client;

import java.net.InetSocketAddress;
import java.net.ProtocolException;
import java.net.URL;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.SocketConnector;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

public class MinaHttpClient {

	private static int count;
	private static int numberOfMsg;
	private static long startTime;
	private static MSG_SIZE msgSize;

	public static void main(String[] args) throws Exception {
		numberOfMsg = Integer.valueOf(args[0]);
		switch (Integer.valueOf(args[1])) {
		case 36:
			msgSize = MSG_SIZE.S36BYTE;
			break;
		case 128:
			msgSize = MSG_SIZE.S128BYTE;
			break;
		case 167:
			msgSize = MSG_SIZE.S167BYTE;
			break;
		case 512:
			msgSize = MSG_SIZE.S512BYTE;
			break;
		case 2048:
			msgSize = MSG_SIZE.S2048BYTE;
			break;
		default:
			msgSize = MSG_SIZE.S128BYTE;
			break;
		}
		SocketConnector connector = new NioSocketConnector();

		connector.setHandler(new SCHttpHandler());
		connector
				.getFilterChain()
				.addLast(
						"protocol",
						new ProtocolCodecFilter(
								new HttpProtocolCodecFactory(
										new URL(
												"http://www.w3.org/pub/WWW/123456789/123456789/123456789/123456789/123456789/12345/123456789/TheProject.html"))));
		ConnectFuture future = connector.connect(new InetSocketAddress(
				"localhost", 19555));
		future.await();
		IoSession session = future.getSession();
		MinaHttpClient.startTime = System.currentTimeMillis();
		new SCHttpHandler().sendMessage(session, msgSize);
	}

	public static class SCHttpHandler extends IoHandlerAdapter {

		public SCHttpHandler() {
		}

		@Override
		public void messageReceived(IoSession session, Object message) {
			if (count < numberOfMsg) {
				sendMessage(session, msgSize);
				count++;
				// System.out.println(count);
			} else {
				session.close(false);
				long neededTime = System.currentTimeMillis() - startTime;
				System.out.println("Job Done in: " + neededTime + " Ms");
				double neededSeconds = neededTime / 1000D;
				System.out.println((numberOfMsg / neededSeconds)
						+ " Messages in 1 second!");
			}
		}

		@Override
		public void sessionIdle(IoSession session, IdleStatus status) {
			System.out.println("session idl");
			session.close(true);
		}

		@Override
		public void exceptionCaught(IoSession session, Throwable cause) {
			System.out.println("session exception");
			cause.printStackTrace();
			session.close(true);
		}

		@Override
		public void sessionClosed(IoSession session) throws Exception {
			// System.out.println("session close");
			super.sessionClosed(session);
		}

		public void sendMessage(IoSession session, MSG_SIZE msgSize) {

			String path = null;

			switch (msgSize) {
			case S36BYTE:
				path = "/";
				break;
			case S128BYTE:
				path = "http://www.w3.org/pub/WWW/123456789/123456789/123456789/123456789/123456789/12345/ThePro.html";
				break;
			case S167BYTE:
				path = "http://www.w3.org/pub/WWW/123456789/123456789/123456789/123456789/123456789/12345678/123456789/123456789/123456789/12345/ThePrc.html";
				break;
			case S512BYTE:
				path = "http://www.w3.org/pub/12345/123456789/123456789/1/TheProject/asdasdasdasdasdasdasdasdasdasdasdasdasdasdasd/12345/pub/WWW/123456789/123456789/12345/123456789/TheProject/asdasdasdasdasdasdasdasdasdasdasdasdasdasdasd/pub/WWW/123456789/123456789/123456789/123456789/123456789/12345/123456789/TheProject/asdasdasdasdasdasdasdasdasdasdasdasdasdasdasd/pub/WWW/123456789/123456789/123456789/123456789/123456789/12345/123456789/TheProject/asdasdasdasdasdasdasdasdasdssad/TheProject.html";
				break;
			case S2048BYTE:
				path = "http://www.w3.org/pub/WWW/123456789/123456789/123456789/123456789/123456789/12345678/123456789/123456789/123456789/12345/123456789/WWW/123456789/123456789/123456789/123456789/123456789/12345678/123456789/123456789/123456789/12345/123456789/WWW/123456789/123456789/123456789/123456789/123456789/12345678/123456789/123456789/123456789/12345/123456789/WWW/123456789/123456789/123456789/123456789/123456789/12345678/123456789/123456789/123456789/12345/123456789/WWW/123456789/123456789/123456789/123456789/123456789/12345678/123456789/123456789/123456789/12345/123456789/WWW/123456789/123456789/123456789/123456789/123456789/12345678/123456789/123456789/123456789/12345/123456789/WWW/123456789/123456789/123456789/123456789/123456789/12345678/123456789/123456789/123456789/12345/123456789/123456789/123456789/123456789/123456789/123456789/123456789/123456789/123456789/123456789/123456789/123456789/123456789/123456789/123456789/1234567/WWW/123456789/123456789/123456789/123456789/123456789/12345678/123456789/123456789/123456789/12345/123456789/WWW/123456789/123456789/123456789/123456789/123456789/12345678/123456789/123456789/123456789/12345/123456789/WWW/123456789/123456789/123456789/123456789/123456789/12345678/123456789/123456789/123456789/12345/123456789/WWW/123456789/123456789/123456789/123456789/123456789/12345678/123456789/123456789/123456789/12345/123456789/WWW/123456789/123456789/123456789/123456789/123456789/12345678/123456789/123456789/123456789/12345/123456789/WWW/123456789/123456789/123456789/123456789/123456789/12345678/123456789/123456789/123456789/12345/123456789/WWW/123456789/123456789/123456789/123456789/123456789/12345678/123456789/123456789/123456789/12345/123456789/WWW/123456789/123456789/123456789/123456789/123456789/12345678/123456789/123456789/123456789/12345/123456789/WWW/123456789/123456789/123456789/123456789/123456789/12345678/123456789/123456789/123456789/12345/123456789/WWW/123456789/123456789/123456789/123456789/123456789/12345678/123456789/123456789/1TheProject.html";
				break;
			default:
				path = "http://www.w3.org/pub/WWW/123456789/123456789/123456789/123456789/123456789/12345/ThePro.html";
				break;
			}
			HttpRequestMessage requestMessage = new HttpRequestMessage(path);
			try {
				requestMessage.setRequestMethod("GET");
			} catch (ProtocolException e) {
				e.printStackTrace();
			}
			session.write(requestMessage);
		}
	}

	public enum MSG_SIZE {
		S36BYTE, S128BYTE, S167BYTE, S512BYTE, S2048BYTE;
	}
}
