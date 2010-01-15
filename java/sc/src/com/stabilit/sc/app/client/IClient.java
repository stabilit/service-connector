package com.stabilit.sc.app.client;

import java.io.IOException;
import java.net.URL;

import com.stabilit.sc.message.IMessage;
import com.stabilit.sc.message.IMessageResult;
import com.stabilit.sc.message.ISubscribe;

public interface IClient {

	public String getSessionId();
	
	public void setEndpoint(URL url);
	
	public void connect() throws Exception;

	public void openSession() throws IOException;

	public IMessageResult sendAndReceive(IMessage job) throws Exception;

	public IMessageResult receive(ISubscribe subscribeJob) throws Exception;

	public void closeSession() throws IOException;

	public void disconnect() throws Exception;
	
	public void destroy() throws Exception;

}
