package com.stabilit.sc.app.client;

import java.io.IOException;
import java.net.URL;

import com.stabilit.sc.io.SCMP;

public interface IClientConnection extends IConnection {

	public boolean isAvailable();

	public void setAvailable(boolean available);

	public String getSessionId();

	public void setEndpoint(URL url);

	public void connect() throws Exception;

	public void openSession() throws IOException;

	public SCMP sendAndReceive(SCMP scmp) throws Exception;

	public void closeSession() throws IOException;

	public void disconnect() throws Exception;

	public void destroy() throws Exception;

}
