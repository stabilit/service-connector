package com.stabilit.sc.app.client.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.stabilit.sc.app.client.IClient;
import com.stabilit.sc.io.SCOP;
import com.stabilit.sc.job.IJob;
import com.stabilit.sc.job.IJobResult;
import com.stabilit.sc.job.ISubscribe;
import com.stabilit.sc.job.impl.AsyncCallJob;

public class SunHttpClient implements IClient {

	private URL endPoint;
	private HttpURLConnection httpConnection;
	private String sessionId;

	public SunHttpClient() {
		this(null);
	}
	
	public SunHttpClient(URL endPoint) {
		this.endPoint = endPoint;
		this.httpConnection = null;
		this.sessionId = null;
	}

	@Override
	public String getSessionId() {
		return this.sessionId;
	}

	@Override
	public void closeSession() {

	}

	@Override
	public void connect() throws IOException {
		httpConnection = (HttpURLConnection) endPoint.openConnection();
		httpConnection.setDoOutput(true);
		httpConnection.setDoInput(true);
	}

	@Override
	public void disconnect() {
		if (httpConnection != null) {
			httpConnection.disconnect();
		}
	}

	@Override
	public void openSession() {

	}

	@Override
	public IJobResult sendAndReceive(IJob job) throws IOException {
		OutputStream os = httpConnection.getOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(os);
		SCOP scop = new SCOP(job);
		scop.setSessionId(this.sessionId);
		oos.writeObject(scop);
		oos.flush();
		InputStream is = httpConnection.getInputStream();
		ObjectInputStream ois = new ObjectInputStream(is);
		Object obj;
		try {
			obj = ois.readObject();
			if (obj instanceof SCOP) {
				SCOP ret = (SCOP) obj;
				String retSessionID = ret.getSessionId();
				if (retSessionID != null) {
					this.sessionId = retSessionID;
				}
				return (IJobResult) ret.getBody();
			}
		} catch (ClassNotFoundException e) {
		}
		return null;
	}

	@Override
	public IJobResult receive(ISubscribe subscribeJob) throws IOException {
		IJob callJob = new AsyncCallJob(subscribeJob);
		OutputStream os = httpConnection.getOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(os);
		SCOP scop = new SCOP(callJob);
		scop.setSessionId(this.sessionId);
		oos.writeObject(scop);
		oos.flush();
		InputStream is = httpConnection.getInputStream();
		ObjectInputStream ois = new ObjectInputStream(is);
		Object obj;
		try {
			obj = ois.readObject();
			if (obj instanceof SCOP) {
				SCOP ret = (SCOP) obj;
				String retSessionID = ret.getSessionId();
				if (retSessionID != null) {
					this.sessionId = retSessionID;
				}
				return (IJobResult) ret.getBody();
			}
		} catch (ClassNotFoundException e) {
		}
		return null;
	}

	public URL getEndPoint() {
		return endPoint;
	}
	
	public void setEndPoint(URL endPoint) {
		this.endPoint = endPoint;
	}

	@Override
	public void setEndpoint(URL url) {
       this.endPoint = url;		
	}
}
