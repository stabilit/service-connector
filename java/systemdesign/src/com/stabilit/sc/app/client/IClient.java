package com.stabilit.sc.app.client;

import java.io.IOException;
import java.net.URL;

import com.stabilit.sc.job.IJob;
import com.stabilit.sc.job.IJobResult;
import com.stabilit.sc.job.ISubscribe;

public interface IClient {

	public String getSessionId();
	
	public void setEndpoint(URL url);
	
	public void connect() throws Exception;

	public void openSession() throws IOException;

	public IJobResult sendAndReceive(IJob job) throws Exception;

	public IJobResult receive(ISubscribe subscribeJob) throws Exception;

	public void closeSession() throws IOException;

	public void disconnect() throws Exception;
	
	public void destroy() throws Exception;

}
