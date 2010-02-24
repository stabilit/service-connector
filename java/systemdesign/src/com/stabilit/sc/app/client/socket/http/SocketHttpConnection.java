package com.stabilit.sc.app.client.socket.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;

import com.stabilit.sc.app.client.IConnection;
import com.stabilit.sc.io.SCMP;
import com.stabilit.sc.util.ObjectStreamHttpUtil;

public class SocketHttpConnection implements IConnection {

	private Socket socket;
	private InputStream is;
	private OutputStream os;
	private URL url;
	private String sessionId;

	public SocketHttpConnection() {
		this.socket = null;
		this.url = null;
	}

	@Override
	public String getSessionId() {
		return this.sessionId;
	}
	
	@Override
	public void closeSession() throws IOException {

	}

	@Override
	public void connect() throws IOException {
		String host = url.getHost();
		int port = url.getPort();
		socket = new Socket(host, port);
	}

	@Override
	public void disconnect() throws IOException {
		socket.close();
		is.close();
		os.close();
	}

	@Override
	public void destroy() throws Exception {
	}

	@Override
	public void openSession() throws IOException {

	}

	@Override
	public SCMP sendAndReceive(SCMP scmp) throws Exception {
		this.os = socket.getOutputStream();
		scmp.setSessionId(this.sessionId);
		InetAddress address = socket.getInetAddress();
		String host = address.getHostAddress() + ":" + url.getPort();
		ObjectStreamHttpUtil.writeRequestObject(this.os, host, scmp);
		this.is = socket.getInputStream();
		Object obj = ObjectStreamHttpUtil.readObject(this.is);
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
		this.os = socket.getOutputStream();
		scmp.setSessionId(this.sessionId);
		InetAddress address = socket.getInetAddress();
		String host = address.getHostAddress() + ":" + url.getPort();
		ObjectStreamHttpUtil.writeRequestObject(this.os, host, scmp);
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
