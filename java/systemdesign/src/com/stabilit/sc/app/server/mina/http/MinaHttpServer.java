package com.stabilit.sc.app.server.mina.http;

import java.net.InetSocketAddress;

import org.apache.mina.common.IdleStatus;
import org.apache.mina.common.IoAcceptor;
import org.apache.mina.common.IoHandlerAdapter;
import org.apache.mina.common.IoSession;
import org.apache.mina.common.WriteFuture;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.SocketAcceptor;
import org.apache.mina.transport.socket.nio.SocketAcceptorConfig;
import org.apache.mina.util.SessionLog;

import com.stabilit.sc.app.client.EchoClient;
import com.stabilit.sc.app.server.IServer;
import com.stabilit.sc.cmd.CommandException;
import com.stabilit.sc.cmd.ICommand;
import com.stabilit.sc.cmd.factory.ICommandFactory;
import com.stabilit.sc.cmd.factory.SCCommandFactory;
import com.stabilit.sc.cmd.impl.EchoCommand;
import com.stabilit.sc.io.IRequest;
import com.stabilit.sc.io.IResponse;

public class MinaHttpServer implements IServer {
	private static final int PORT = 85;

	public static final String VERSION_STRING = "$Revision: 555855 $ $Date: 2007-07-13 12:19:00 +0900 (Fri, 13 Jul 2007) $";

	private IoAcceptor acceptor;
	private SocketAcceptorConfig config;

	public MinaHttpServer() {
		this.acceptor = null;
		this.config = null;
	}
	
	@Override
	public void create() throws Exception {
		this.acceptor = new SocketAcceptor();
		this.config = new SocketAcceptorConfig();

		this.config.setReuseAddress(true);
		this.config.getFilterChain().addLast("protocolFilter",
				new ProtocolCodecFilter(new HttpServerProtocolCodecFactory()));
		// config.getFilterChain().addLast("logger", new LoggingFilter());
	}

	@Override
	public void run() throws Exception {
		// Bind
		// acceptor.getFilterChain().addLast("executor", new ExecutorFilter(new
		// ThreadPoolExecutor(2,2,1000,TimeUnit.MILLISECONDS, new
		// LinkedBlockingQueue<Runnable>())));
		acceptor.bind(new InetSocketAddress("localhost", PORT),
				new SCHttpHandler(), config);
//		 System.out.println("Listening on port " + PORT);
	}

	@Override
	public void destroy() throws Exception {
//		acceptor.unbindAll();
	}

	public static class SCHttpHandler extends IoHandlerAdapter {
		private ICommandFactory commandFactory = new SCCommandFactory();

		public SCHttpHandler() {
		}

		@Override
		public void messageReceived(IoSession session, Object message) {
			// Check that we can service the request context
//			HttpRequestMessage requestMessage = (HttpRequestMessage) message;
			HttpResponseMessage responseMessage = new HttpResponseMessage();
			responseMessage.setContentType("text/plain");
			responseMessage
					.setResponseCode(HttpResponseMessage.HTTP_STATUS_SUCCESS);
//			IRequest request = new MinaHttpRequest(requestMessage);
//			IResponse response = new MinaHttpResponse(responseMessage);
			//ICommand command = commandFactory.newCommand(request);
//			try {
//				command.run(request, response);
//			} catch (CommandException e) {
//				e.printStackTrace();
//			}
			WriteFuture writeFuture;
			if (message != null) {
				writeFuture = session.write(responseMessage);
				writeFuture.join();
			}
			// session.close();
		}

		@Override
		public void sessionIdle(IoSession session, IdleStatus status) {
			 SessionLog.info(session, "Disconnecting the idle.");
			session.close();
		}

		@Override
		public void exceptionCaught(IoSession session, Throwable cause) {
			cause.printStackTrace();
			session.close();
		}

		@Override
		public void sessionClosed(IoSession session) throws Exception {
			super.sessionClosed(session);
		}
	}
}
