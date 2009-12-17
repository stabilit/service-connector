package com.stabilit.sc.net.http.client;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.stabilit.sc.io.SCOP;
import com.stabilit.sc.job.IJob;
import com.stabilit.sc.job.IJobResult;
import com.stabilit.sc.job.Job;

public class SCHttpClient {

	public static void main(String[] args) throws Exception {
		String sURL = "http://localhost/";
		URL url = null;
		if (args.length > 0) {
			sURL = args[0];
		}
		int index = 0;
		long startTime = 0;
		url = new URL(sURL);
		HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
		httpConnection.setDoOutput(true);
		httpConnection.setDoInput(true);
		httpConnection.addRequestProperty("keep-alive", "true");
		httpConnection.setConnectTimeout(10000);
		System.out.println(httpConnection.getConnectTimeout());
		OutputStream os = httpConnection.getOutputStream();
		InputStream is = null;
		while (true) {
			try {
				index++;
				IJob job = new Job("echo");
				job.setAttribute("index", index);
				SCOP scop = new SCOP(job);
				ObjectOutputStream oos = new ObjectOutputStream(os);
				oos.writeObject(scop);
				oos.flush();
				//oos.close();
				is = httpConnection.getInputStream();
				ObjectInputStream ois = new ObjectInputStream(is);
				Object obj = ois.readObject();
				if (obj instanceof SCOP) {
					obj = ((SCOP) obj).getBody();
				}
				if (obj instanceof IJobResult) {
					obj = ((IJobResult) obj).getJob();
				}
				if (obj instanceof IJob) {
					IJob retJob = (IJob) obj;
					int retIndex = (Integer) retJob.getAttribute("index");
					if (retIndex != index) {
						throw new Exception("invalid return index");
					}
					if (retIndex % 1000 == 0) {
						System.out.println(obj + " " + (System.currentTimeMillis()-startTime));
						startTime = System.currentTimeMillis();
					}
				} else {
					throw new Exception("no job result returned");
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
			}
		}
	}
}
