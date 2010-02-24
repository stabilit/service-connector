package com.stabilit.sc.app.client.sun.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.stabilit.sc.app.client.IConnection;
import com.stabilit.sc.io.SCMP;

public class SunHttpConnection implements IConnection {

	private URL endPoint;
	private HttpURLConnection httpConnection;
	private String sessionId;

	public SunHttpConnection() {
		this(null);
	}
	
	public SunHttpConnection(URL endPoint) {
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
	public void destroy() throws Exception {
	}
	
	@Override
	public void openSession() {

	}

	@Override
	public SCMP sendAndReceive(SCMP scmp) throws IOException {
		OutputStream os = httpConnection.getOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(os);
		scmp.setSessionId(this.sessionId);
		oos.writeObject(scmp);
		oos.flush();
		InputStream is = httpConnection.getInputStream();
		ObjectInputStream ois = new ObjectInputStream(is);
		Object obj;
		try {
			obj = ois.readObject();
			if (obj instanceof SCMP) {
				SCMP ret = (SCMP) obj;
				String retSessionID = ret.getSessionId();
				if (retSessionID != null) {
					this.sessionId = retSessionID;
				}
				return ret;
			}
		} catch (ClassNotFoundException e) {
		}
		return null;
	}

	@Override
	public void send(SCMP scmp) throws IOException {
		OutputStream os = httpConnection.getOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(os);
		scmp.setSessionId(this.sessionId);
		oos.writeObject(scmp);
		oos.flush();
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

	@Override
	public boolean isAvailable() {
		return false;
	}

	@Override
	public void setAvailable(boolean available) {		
	}
}
