package com.stabilit.sc.app.client.ftp;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import sun.net.ftp.FtpClient;

import com.stabilit.sc.app.client.IConnection;
import com.stabilit.sc.io.SCMP;
import com.stabilit.sc.msg.IMessage;

public class SCFtpConnection implements IConnection {

	private URL endPoint;
	private String userid;
	private String password;
	private FtpClient ftpClient;

	public SCFtpConnection(URL endPoint) {
		this.endPoint = endPoint;
		this.ftpClient = null;
		this.userid = null;
		this.password = null;
	}

	@Override
	public String getSessionId() {
		return null;
	}
	
	public SCFtpConnection(URL endPoint, String userid, String password) {
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
	public void destroy() throws Exception {
	}

	@Override
	public void openSession() {

	}

	@Override
	public SCMP sendAndReceive(SCMP scmp) throws IOException {
		if ("ftp".equals(scmp.getMessageId()) == false) {
			throw new IOException("not supported");
		}
		IMessage msg = (IMessage)scmp.getBody();
		String path = (String) msg.getAttribute("path");
		int lastSlash = path.lastIndexOf('/');
		String filename = path.substring(lastSlash + 1);
		String directory = path.substring(0, lastSlash);
		ftpClient.binary();
		ftpClient.cd(directory);

		InputStream is = ftpClient.get(path);
		BufferedInputStream bis = new BufferedInputStream(is);

		ByteArrayOutputStream os = new ByteArrayOutputStream();
		BufferedOutputStream bos = new BufferedOutputStream(os);

		byte[] buffer = new byte[1024 << 2];
		int readCount;
		while ((readCount = bis.read(buffer)) > 0) {
			bos.write(buffer, 0, readCount);
		}
		bos.close();

		msg.setAttribute("return", os.toByteArray());
		return scmp;
	}

	@Override
	public void send(SCMP scmp) throws Exception {
		throw new Exception("not supported");
	}

	@Override
	public void setEndpoint(URL url) {
		this.endPoint = url;
	}

	@Override
	public boolean isAvailable() {
		return false;
	}

	@Override
	public void setAvailable(boolean available) {		
	}
}
