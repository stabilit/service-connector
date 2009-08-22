package com.stabilit.sc.net.http.client;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.stabilit.sc.job.IJob;
import com.stabilit.sc.job.Job;

public class SCHttpClient {

	public static void main(String[] args) {
		String sURL = "http://localhost/";
		URL url = null;
		if (args.length > 0) {
			sURL = args[0];
		}
		int index = 0;
		while (true) {
			try {
				index++;
				url = new URL(sURL);
				HttpURLConnection httpConnection = (HttpURLConnection) url
						.openConnection();
				httpConnection.setDoOutput(true);
				httpConnection.setDoInput(true);
				OutputStream os = httpConnection.getOutputStream();
				IJob job = new Job("echo");
				job.setAttribute("index", index);
				ObjectOutputStream oos = new ObjectOutputStream(os);
				oos.writeObject(job);
				oos.flush();
				oos.close();
				InputStream is = httpConnection.getInputStream();
				ObjectInputStream ois = new ObjectInputStream(is);
				Object obj = ois.readObject();
				if (obj instanceof IJob) {
					IJob retJob = (IJob) obj;
					int retIndex = (Integer)retJob.getAttribute("index");
					if (retIndex != index) {
						throw new Exception("invalid return index");
					}
					System.out.println(obj);
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
