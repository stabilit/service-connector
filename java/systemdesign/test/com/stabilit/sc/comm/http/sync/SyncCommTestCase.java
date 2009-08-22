package com.stabilit.sc.comm.http.sync;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.stabilit.sc.client.IClient;
import com.stabilit.sc.client.SCClientFactory;
import com.stabilit.sc.job.IJob;
import com.stabilit.sc.job.IJobFactory;
import com.stabilit.sc.job.IJobResult;
import com.stabilit.sc.job.JobFactory;

public class SyncCommTestCase {

	public static String httpEndPoint = "http://localhost/";

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void syncEcho() throws IOException {
		IClient client = SCClientFactory.newInstance(httpEndPoint);
		client.connect();
		IJobFactory jobFactory = new JobFactory();
		IJob job = jobFactory.newJob("echo");
		IJobResult jobResult = client.sendAndReceive(job);
		IJob jobReturn = jobResult.getJob();
		client.disconnect();
	}
}
