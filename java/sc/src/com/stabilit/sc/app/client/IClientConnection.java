package com.stabilit.sc.app.client;

import java.io.IOException;
import java.net.URL;

import com.stabilit.sc.io.SCMP;

public interface IClientConnection extends IConnection {

	public boolean isAvailable();

	public void setAvailable(boolean available);

	public String getSessionId();

	public void setEndpoint(URL url);

	public void createSession() throws IOException;
	
	public void connect() throws IOException, Exception;
	
	public void disconnect() throws IOException, Exception;

	public SCMP sendAndReceive(SCMP scmp) throws Exception;

	public void deleteSession() throws IOException;

	public void destroy() throws Exception;

}
