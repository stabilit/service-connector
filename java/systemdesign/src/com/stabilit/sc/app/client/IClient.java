package com.stabilit.sc.app.client;

import java.io.IOException;
import java.net.URL;

import com.stabilit.sc.job.IJob;
import com.stabilit.sc.job.IJobResult;

public interface IClient {

	public void setEndpoint(URL url);
	
	public void connect() throws IOException;

	public void openSession() throws IOException;

	public IJobResult sendAndReceive(IJob job) throws Exception;

	public void closeSession() throws IOException;

	public void disconnect() throws IOException;

}
