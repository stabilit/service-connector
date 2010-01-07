package com.stabilit.sc.comm.ftp;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.stabilit.sc.app.client.IClient;
import com.stabilit.sc.app.client.ClientConnectionFactory;
import com.stabilit.sc.job.IJobFactory;
import com.stabilit.sc.job.IJobResult;
import com.stabilit.sc.job.JobFactory;
import com.stabilit.sc.job.impl.FileSystemJob;
import com.stabilit.sc.job.impl.FileSystemJob.ACTION;

public class FileSystemTestCase {

	public static String httpEndPoint = "http://localhost/";

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void listFiles() throws IOException {
		IClient client = ClientConnectionFactory.newInstance(httpEndPoint);
		client.connect();
		IJobFactory jobFactory = new JobFactory();
		FileSystemJob job = (FileSystemJob)jobFactory.newJob("filesystem");
		job.setAction(ACTION.LIST);
		job.setPath("*.*");
		IJobResult jobResult;
		try {
			jobResult = client.sendAndReceive(job);
			File[] files = (File[])jobResult.getReturn();
			for (int i = 0; i < files.length; i++) {
				System.out.println(files[i].getName());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		client.disconnect();
	}
}
