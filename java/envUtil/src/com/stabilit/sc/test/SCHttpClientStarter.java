package com.stabilit.sc.test;

import java.io.IOException;

import com.stabilit.sc.client.IClient;
import com.stabilit.sc.client.SCClientFactory;
import com.stabilit.sc.job.IJob;
import com.stabilit.sc.job.IJobFactory;
import com.stabilit.sc.job.IJobResult;
import com.stabilit.sc.job.JobFactory;

public class SCHttpClientStarter {

	private static final String HTTP_END_POINT = "http://localhost/";
	private IClient client;

	public static void main(String[] args) {
		SCHttpClientStarter starter = new SCHttpClientStarter();
		starter.run();
	}

	public void run() {
		client = SCClientFactory.newInstance(HTTP_END_POINT);
		try {
			for (int i = 0; i < 100000; i++) {
				send();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void send() throws IOException {
		client.connect();
		IJobFactory jobFactory = new JobFactory();
		IJob job = jobFactory.newJob("echo");
		IJobResult jobResult = client.sendAndReceive(job);
		IJob jobReturn = jobResult.getJob();
		//System.out.println(jobReturn);
		client.disconnect();
	}
}
