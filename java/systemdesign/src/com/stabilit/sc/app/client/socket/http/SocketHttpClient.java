package com.stabilit.sc.app.client.socket.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;

import com.stabilit.sc.app.client.IClient;
import com.stabilit.sc.io.SCOP;
import com.stabilit.sc.job.IJob;
import com.stabilit.sc.job.IJobResult;
import com.stabilit.sc.job.ISubscribe;
import com.stabilit.sc.job.impl.AsyncCallJob;
import com.stabilit.sc.job.impl.SubscribeJob;
import com.stabilit.sc.util.ObjectStreamHttpUtil;

public class SocketHttpClient implements IClient {

	private Socket socket;
	private InputStream is;
	private OutputStream os;
	private URL url;
	private String sessionId;

	public SocketHttpClient() {
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
	public void openSession() throws IOException {

	}

	@Override
	public IJobResult sendAndReceive(IJob job) throws Exception {
		this.os = socket.getOutputStream();
		SCOP scop = new SCOP(job);
		scop.setSessionId(this.sessionId);
		InetAddress address = socket.getInetAddress();
		String host = address.getHostAddress() + ":" + url.getPort();
		ObjectStreamHttpUtil.writeRequestObject(this.os, host, scop);
		this.is = socket.getInputStream();
		Object obj = ObjectStreamHttpUtil.readObject(this.is);
		if (obj instanceof SCOP) {
			SCOP ret = (SCOP) obj;
			String retSessionID = ret.getSessionId();
			if (retSessionID != null) {
				this.sessionId = retSessionID;
			}
			return (IJobResult) ret.getBody();
		}
		throw new Exception("not found");
	}

	@Override
	public IJobResult receive(ISubscribe subscribeJob) throws Exception {
		this.os = socket.getOutputStream();
		IJob callJob = new AsyncCallJob(subscribeJob);
		SCOP scop = new SCOP(callJob);
		scop.setSessionId(this.sessionId);
		InetAddress address = socket.getInetAddress();
		String host = address.getHostAddress() + ":" + url.getPort();
		ObjectStreamHttpUtil.writeRequestObject(this.os, host, scop);
		this.is = socket.getInputStream();
		Object obj = ObjectStreamHttpUtil.readObject(this.is);
		if (obj instanceof SCOP) {
			SCOP ret = (SCOP) obj;
			String retSessionID = ret.getSessionId();
			if (retSessionID != null) {
				this.sessionId = retSessionID;
			}
			return (IJobResult) ret.getBody();
		}
		throw new Exception("not found");
	}

	@Override
	public void setEndpoint(URL url) {
		this.url = url;
	}

}
