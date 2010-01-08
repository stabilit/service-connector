package com.stabilit.mina.http.server;

import java.net.InetSocketAddress;

import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import com.stabilit.mina.http.client.MinaHttpClient.MSG_SIZE;

public class MinaHttpServer {
	private static final int PORT = 19555;

	public static final String VERSION_STRING = "$Revision: 555855 $ $Date: 2007-07-13 12:19:00 +0900 (Fri, 13 Jul 2007) $";

	private IoAcceptor acceptor;
	private static MSG_SIZE msgSize;

	public MinaHttpServer() {
		this.acceptor = null;
	}

	public void run() throws Exception {
		acceptor = new NioSocketAcceptor();
		acceptor.setHandler(new SCHttpServerHandler());
		acceptor.bind(new InetSocketAddress("localhost", PORT));

		acceptor.getFilterChain().addLast("protocol",
				new ProtocolCodecFilter(new HttpServerProtocolCodecFactory()));
	}

	public static void main(String[] args) throws Exception {
		MinaHttpServer server = new MinaHttpServer();
		switch (Integer.valueOf(args[0])) {
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
		server.run();
	}

	public static class SCHttpServerHandler extends IoHandlerAdapter {

		public SCHttpServerHandler() {
		}

		@Override
		public void messageReceived(IoSession session, Object message) {
			HttpResponseMessage response = new HttpResponseMessage();
			response.setResponseCode(HttpResponseMessage.HTTP_STATUS_SUCCESS);
			response.setContentType("text/html");

			String body = null;

			switch (msgSize) {
			case S512BYTE:
				body = "http://www.w3.org/pub/12345/123456789/123456789/1/TheProject/asdasdaasdasdasdasdasdasdasdasdasdasdasdasdasd/pub/WWW/123456789/6789/123456789/12345/123456789/TheProject/asdasdasdasdasdasdas/pub/Thect.html";
				break;
			case S2048BYTE:
				body = "http://www.w3.org/pub/WWW/123456789/123456789/123456789/123456789/123789/12345678/123456789/123456789/123456789/12345/123456789/WWW/123456789/123456789/123456789/123456789/123456789/12345678/123456789/123456789/123456789/12345/123456789/WWW/123456789/123456789/123456789/123456789/123456789/12345678/123456789/123456789/123456789/12345/123456789/WWW/123456789/123456789/123456789/123456789/123456789/12345678/123456789/123456789/123456789/12345/123456789/WWW/123456789/123456789/123456789/123456789/123456789/12345678/123456789/123456789/123456789/12345/123456789/WWW/123456789/123456789/123456789/123456789/123456789/12345678/123456789/123456789/123456789/12345/123456789/WWW/123456789/123456789/123456789/123456789/123456789/12345678/123456789/123456789/123456789/12345/123456789/123456789/123456789/123456789/123456789/123456789/123456789/123456789/123456789/123456789/123456789/123456789/123456789/123456789/123456789/1234567/WWW/123456789/123456789/123456789/123456789/123456789/12345678/123456789/123456789/123456789/12345/123456789/WWW/123456789/123456789/123456789/123456789/123456789/12345678/123456789/123456789/123456789/12345/123456789/WWW/123456789/123456789/123456789/123456789/123456789/12345678/123456789/123456789/123456789/12345/123456789/WWW/123456789/123456789/123456789/123456789/123456789/12345678/123456789/123456789/123456789/12345/123456789/WWW/123456789/123456789/123456789/123456789/123456789/12345678/123456789/123456789/123456789/12345/123456789/WWW/123456789/123456789/123456789/123456789/123456789/12345678/123456789/123456789/123456789/12345/123456789/WWW/123456789/123456789/123456789/123456789/123456789/12345678/123456789/123456789/123456789/12345/123456/12345678789/12345678/123456789/123456789/ct.html";
				break;
			default:
				body = "";
				break;
			}
			response.appendBody(body);
			session.write(response);
		}

		@Override
		public void sessionIdle(IoSession session, IdleStatus status) {
			session.close();
		}

		@Override
		public void exceptionCaught(IoSession session, Throwable cause) {
			session.close();
		}

		@Override
		public void sessionClosed(IoSession session) throws Exception {
			super.sessionClosed(session);
		}
	}
}
