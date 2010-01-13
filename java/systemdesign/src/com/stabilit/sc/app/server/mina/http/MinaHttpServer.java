package com.stabilit.sc.app.server.mina.http;

import java.net.InetSocketAddress;

import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import com.stabilit.sc.app.server.ServerApplication;
import com.stabilit.sc.cmd.CommandException;
import com.stabilit.sc.cmd.ICommand;
import com.stabilit.sc.cmd.factory.CommandFactory;
import com.stabilit.sc.cmd.factory.ICommandFactory;
import com.stabilit.sc.io.IRequest;
import com.stabilit.sc.io.IResponse;

public class MinaHttpServer extends ServerApplication {
	private static final int PORT = 8066;

	public static final String VERSION_STRING = "$Revision: 555855 $ $Date: 2007-07-13 12:19:00 +0900 (Fri, 13 Jul 2007) $";

	private IoAcceptor acceptor;
		
	public MinaHttpServer() {
		this.acceptor = null;
	}
	
	@Override
	public void create() throws Exception {
		this.acceptor = new NioSocketAcceptor();
		acceptor.setHandler(new HttpServerHandler());

		acceptor.getFilterChain().addLast("protocolFilter",
				new ProtocolCodecFilter(new HttpServerProtocolCodecFactory()));
		// config.getFilterChain().addLast("logger", new LoggingFilter());
	}

	@Override
	public void run() throws Exception {
		acceptor.bind(new InetSocketAddress("localhost", PORT));
		synchronized (this) {
			wait();
		}
	}

	@Override
	public void destroy() throws Exception {
		acceptor.unbind();
	}

	public static class HttpServerHandler extends IoHandlerAdapter {
		private ICommandFactory commandFactory = CommandFactory.getInstance();

		public HttpServerHandler() {
		}

		@Override
		public void messageReceived(IoSession session, Object message) {
			// Check that we can service the request context
			HttpRequestMessage requestMessage = (HttpRequestMessage) message;
			HttpResponseMessage responseMessage = new HttpResponseMessage();
			responseMessage.setContentType("text/plain");
			responseMessage
					.setResponseCode(HttpResponseMessage.HTTP_STATUS_SUCCESS);
			IRequest request = new MinaHttpRequest(requestMessage);
			IResponse response = new MinaHttpResponse(responseMessage);
			ICommand command = commandFactory.newCommand(request);
			try {
				command.run(request, response);
			} catch (CommandException e) {
				e.printStackTrace();
			}
			session.write(responseMessage);
//			session.close();
		}

		@Override
		public void sessionIdle(IoSession session, IdleStatus status) {
			// SessionLog.info(session, "Disconnecting the idle.");
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
