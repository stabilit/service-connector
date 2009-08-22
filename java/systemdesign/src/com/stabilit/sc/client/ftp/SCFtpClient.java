package com.stabilit.sc.client.ftp;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import sun.net.ftp.FtpClient;

import com.stabilit.sc.client.IClient;
import com.stabilit.sc.job.IJob;
import com.stabilit.sc.job.IJobResult;
import com.stabilit.sc.job.JobResult;

public class SCFtpClient implements IClient {

	private URL endPoint;
	private String userid;
	private String password;
	private FtpClient ftpClient;

	public SCFtpClient(URL endPoint) {
		this.endPoint = endPoint;
		this.ftpClient = null;
		this.userid = null;
		this.password = null;
	}
	
	public SCFtpClient(URL endPoint, String userid, String password) {
		this.endPoint = endPoint;
		this.userid = userid;
		this.password = password;
		this.ftpClient = null;
	}

	public String getUserid() {
		return userid;
	}
	
	public void setUserid(String userid) {
		this.userid = userid;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	@Override
	public void closeSession() {

	}

	@Override
	public void connect() throws IOException {
		ftpClient = new FtpClient(endPoint.getHost());
		ftpClient.login(userid, password);
	}

	@Override
	public void disconnect() throws IOException {
		if (ftpClient != null) {
			ftpClient.closeServer();
		}
	}

	@Override
	public void openSession() {

	}

	@Override
	public IJobResult sendAndReceive(IJob job) throws IOException {
		if ("ftp".equals(job.getKey()) == false) {
			throw new IOException("not supported");
		}
		String path = (String)job.getAttribute("path");
	    int lastSlash = path.lastIndexOf('/');
		String filename = path.substring(lastSlash+1);
		String directory = path.substring(0,lastSlash);
	    ftpClient.binary();
	    ftpClient.cd(directory);

		InputStream is = ftpClient.get(path);
	    BufferedInputStream bis = new BufferedInputStream(is);

	    ByteArrayOutputStream os = new ByteArrayOutputStream();
	    BufferedOutputStream bos = new BufferedOutputStream(os);

	    byte[] buffer = new byte[1024 << 2];
	    int readCount;	                    
	    while( (readCount = bis.read(buffer)) > 0) {
	        bos.write(buffer, 0, readCount);
	    }
	    bos.close();
	    
	    JobResult jobResult = new JobResult(job);
	    jobResult.setAttribute("return", os.toByteArray());	    	   

		return jobResult;
	}
}
