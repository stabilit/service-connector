package com.stabilit.sc.app.client.mina.http;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ProtocolException;
import java.net.URL;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.mina.core.future.CloseFuture;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.SocketConnector;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import com.stabilit.sc.app.client.IConnection;
import com.stabilit.sc.io.SCMP;
import com.stabilit.sc.util.ObjectStreamHttpUtil;

public class MinaHttpConnection implements IConnection {

	private URL url;
	private SocketConnector connector;
	private ConnectFuture future;
	private IoSession session;
	private String sessionId;
	private HttpClientHandler handler = null;
	private ProtocolCodecFilter filter = null;

	public MinaHttpConnection() {
		this.url = null;
		this.connector = new NioSocketConnector();
		this.sessionId = null;
		this.handler = this.new HttpClientHandler();
		connector.setHandler(handler);
	}

	@Override
	public String getSessionId() {
		return this.sessionId;
	}

	@Override
	public void closeSession() throws IOException {

	}

	@Override
	public void connect() throws Exception {
		if (this.filter == null) {
			this.filter = new ProtocolCodecFilter(new HttpProtocolCodecFactory(
					this.url));
			connector.getFilterChain().addLast(
					"protocolFilter",
					new ProtocolCodecFilter(new HttpProtocolCodecFactory(
							this.url)));
		}
		String host = url.getHost();
		int port = url.getPort();

		this.future = connector.connect(new InetSocketAddress(host, port));
		this.future.await();
		this.session = future.getSession();
	}

	@Override
	public void disconnect() throws Exception {
		CloseFuture future = this.session.close(true);
		future.await();
	}

	@Override
	public void destroy() throws Exception {
		connector.dispose();
	}

	@Override
	public void openSession() throws IOException {

	}

	@Override
	public SCMP sendAndReceive(SCMP scmp) throws Exception {
		scmp.setSessionId(this.sessionId);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectStreamHttpUtil.writeObjectOnly(baos, scmp);
		HttpRequestMessage requestMessage = new HttpRequestMessage("/");
		try {
			byte[] buffer = baos.toByteArray();
			requestMessage.setRequestMethod("POST");
			requestMessage.setContentLength(buffer.length);
			requestMessage.addContent(buffer);
		} catch (ProtocolException e) {
			e.printStackTrace();
		}
		WriteFuture future = session.write(requestMessage);
		future.awaitUninterruptibly();
		HttpClientHandler handler = (HttpClientHandler) session.getHandler();

		byte[] content = handler.getMessageSync().getContent();
		ByteArrayInputStream bais = new ByteArrayInputStream(content);
		Object obj = ObjectStreamHttpUtil.readObjectOnly(bais);
		if (obj instanceof SCMP) {
			SCMP ret = (SCMP) obj;
			String retSessionID = ret.getSessionId();
			if (retSessionID != null) {
				this.sessionId = retSessionID;
			}
			return ret;
		}
		throw new Exception("not found");
	}

	@Override
	public void send(SCMP scmp) throws Exception {
		scmp.setSessionId(this.sessionId);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectStreamHttpUtil.writeObjectOnly(baos, scmp);
		HttpRequestMessage requestMessage = new HttpRequestMessage("/");
		try {
			byte[] buffer = baos.toByteArray();
			requestMessage.setRequestMethod("POST");
			requestMessage.setContentLength(buffer.length);
			requestMessage.addContent(buffer);
		} catch (ProtocolException e) {
			e.printStackTrace();
		}
		WriteFuture future = session.write(requestMessage);
	}

	public class HttpClientHandler extends IoHandlerAdapter {

		private final BlockingQueue<HttpResponseMessage> answer = new LinkedBlockingQueue<HttpResponseMessage>();

		public HttpResponseMessage getMessageSync() {
			HttpResponseMessage responseMessage;
			boolean interrupted = false;
			for (;;) {
				try {
					// take() wartet bis Message in Queue kommt!
					responseMessage = answer.take();
					break;
				} catch (InterruptedException e) {
					interrupted = true;
				}
			}

			if (interrupted) {
				Thread.currentThread().interrupt();
			}
			return responseMessage;
		}

		@Override
		public void messageReceived(IoSession session, Object message) {
			answer.offer((HttpResponseMessage) message);
		}

		@Override
		public void sessionIdle(IoSession session, IdleStatus status) {
			session.close(true);
		}

		@Override
		public void exceptionCaught(IoSession session, Throwable cause) {
			cause.printStackTrace();
			session.close(true);
		}

		@Override
		public void sessionClosed(IoSession session) throws Exception {
			super.sessionClosed(session);
		}

	}

	@Override
	public void setEndpoint(URL url) {
		this.url = url;
	}

	@Override
	public boolean isAvailable() {
		return false;
	}

	@Override
	public void setAvailable(boolean available) {		
	}
}
