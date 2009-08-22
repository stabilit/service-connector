package com.stabilit.sc.client;

import java.io.IOException;

import com.stabilit.sc.job.IJob;
import com.stabilit.sc.job.IJobResult;

public interface IClient {

	public void connect() throws IOException;

	public void openSession() throws IOException;

	public IJobResult sendAndReceive(IJob job) throws IOException;

	public void closeSession() throws IOException;

	public void disconnect() throws IOException;

}
