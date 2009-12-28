package com.stabilit.sc.comm.ftp;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.stabilit.sc.app.client.ClientFactory;
import com.stabilit.sc.app.client.ftp.SCFtpClient;
import com.stabilit.sc.job.IJob;
import com.stabilit.sc.job.IJobFactory;
import com.stabilit.sc.job.IJobResult;
import com.stabilit.sc.job.JobFactory;

public class FtpCommTestCase {

	public static String ftpEndPoint = "ftp://localhost/";

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void ftpGet() throws IOException {
		SCFtpClient client = (SCFtpClient)ClientFactory.newInstance(ftpEndPoint);
		client.setUserid("test");
		client.setPassword("test");
		client.connect();
		IJobFactory jobFactory = new JobFactory();
		IJob job = jobFactory.newJob("ftp");
		job.setAttribute("path", "/com/stabilit/sc/comm/ftp/FtpCommTestCase.java");
		IJobResult jobResult = client.sendAndReceive(job);
		byte[] ret = (byte[])jobResult.getReturn();
		for (int i = 0; i < ret.length; i++) {
		   System.out.print((char)ret[i]);
		}
		client.disconnect();
	}
}
