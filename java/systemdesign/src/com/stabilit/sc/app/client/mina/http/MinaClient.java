package com.stabilit.sc.app.client.mina.http;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.URL;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.ConnectFuture;
import org.apache.mina.common.IoSession;
import org.apache.mina.transport.socket.nio.SocketConnector;

import com.stabilit.sc.app.client.IClient;
import com.stabilit.sc.io.SCOP;
import com.stabilit.sc.job.IJob;
import com.stabilit.sc.job.IJobResult;

public class MinaClient implements IClient {

	private HttpClientHandler handler;
	private SocketConnector connector;
	private ConnectFuture future1;
	private IoSession session;
	private URL endPoint;
	private static final byte[] CRLF = new byte[] { 0x0D, 0x0A };

	public MinaClient() {
	}
	
	public MinaClient(URL endPoint) {
		this.endPoint = endPoint;
	}

	@Override
	public void closeSession() throws IOException {
		session.close();
	}

	@Override
	public void connect() throws IOException {
		connector = new SocketConnector();		
		SocketAddress address = new InetSocketAddress(endPoint.getHost(), endPoint.getPort());

		handler = new HttpClientHandler();
		if (session != null && session.isConnected()) {
			throw new IllegalStateException(
					"Already connected. Disconnect first.");
		}

		future1 = connector.connect(address, handler);
		connector.setWorkerTimeout(0);
		future1.join();
		session = future1.getSession();
	}

	@Override
	public void disconnect() throws IOException {
		session.close();
	}

	@Override
	public void openSession() throws IOException {
		session = future1.getSession();
	}

	@Override
	public IJobResult sendAndReceive(IJob job) throws Exception {
		CharsetEncoder encoder = Charset.defaultCharset().newEncoder();
		ByteBuffer buf = ByteBuffer.allocate(256);
		try {			
			SCOP scop = new SCOP(job);
			ByteArrayOutputStream bais = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(bais);
			oos.writeObject(scop);
			//String httpRequest = "GET / HTTP/1.1 \nContent-Length: " + bais.size() + "\n\n";
			String httpRequest = "GET / HTTP/1.1 \nContent-Length: " + 1 + "\n\n";
			buf.setAutoExpand(true);
			buf.putString(httpRequest, encoder);			
			//buf.put(bais.toByteArray());
			buf.put(new Byte("s"));
			buf.put(CRLF);
			buf.put(CRLF);
			buf.put(CRLF);

		} catch (CharacterCodingException e) {
			e.printStackTrace();
		}
		System.out.println(buf.capacity()-buf.remaining());
		buf.flip();

		session.write(buf);
		session.getCloseFuture().join();
		return null;
	}

	@Override
	public void setEndpoint(URL url) {
		endPoint = url;
	}
}
