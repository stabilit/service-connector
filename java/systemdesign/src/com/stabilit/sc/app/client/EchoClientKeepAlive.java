package com.stabilit.sc.app.client;

import java.net.URL;

import com.stabilit.sc.app.server.ServerException;
import com.stabilit.sc.job.IJob;
import com.stabilit.sc.job.IJobResult;
import com.stabilit.sc.job.impl.EchoJob;

public class EchoClientKeepAlive {

	public static void main(String[] args) throws Exception {
		String sURL = "http://localhost:80/";
        URL url = new URL(sURL);
		String key = "default";
		if (args.length > 0) {
			try {
				url = new URL(args[0]);
				sURL = args[0];
				url = new URL(sURL);
			} catch (Exception e) {
               key = args[0];
			}
		}
		IClient client = ClientFactory.newInstance(key);
		if (client == null) {
			client = ClientFactory.newInstance();
		}
		if (client == null) {
			throw new ServerException("no client available");
		}
		client.setEndpoint(url);

		int index = 0;
		client.connect();
		while (true) {
			IJob job = new EchoJob();
			job.setAttribute("msg", "hello " + ++index);
			IJobResult result = client.sendAndReceive(job);
			System.out.println(result.getJob());
		}
		//client.disconnect();
	}
}
