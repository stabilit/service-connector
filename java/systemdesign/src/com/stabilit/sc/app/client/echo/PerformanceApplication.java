package com.stabilit.sc.app.client.echo;

import java.net.URL;

import com.stabilit.sc.app.client.ClientApplication;
import com.stabilit.sc.app.client.ClientConnectionFactory;
import com.stabilit.sc.app.client.IClient;
import com.stabilit.sc.context.ClientApplicationContext;
import com.stabilit.sc.job.IJob;
import com.stabilit.sc.job.IJobResult;
import com.stabilit.sc.job.impl.EchoJob;

/**
 * The Class PerformanceOneClient.
 */
public class PerformanceApplication extends ClientApplication {

	/** The end time. */
	public long endTime;

	/** The start time. */
	public long startTime;

	@Override
	public void run() throws Exception {
		
		ClientApplicationContext applicationContext = (ClientApplicationContext) this.getContext();
		String key = applicationContext.getKey();
		URL url = applicationContext.getURL();
		IClient client = ClientConnectionFactory.newInstance(key);
		
		int numberOfMsg = Integer.valueOf(applicationContext.getArgs()[1]);
		
		if (client == null) {
			System.out.println("no client available");
		}
		client.setEndpoint(url);

		int index = 0;
		startTime = System.currentTimeMillis();
		for (int i = 0; i < numberOfMsg; i++) {
			try {
				client.connect();
				IJob job = new EchoJob();
				job.setAttribute("msg", "hello " + ++index);
				IJobResult result = client.sendAndReceive(job);
				// System.out.println(result.getJob());
				client.disconnect();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		endTime = System.currentTimeMillis();
		long neededTime = endTime - startTime;
		System.out.println("Job Done in: " + neededTime + " Ms");
		double neededSeconds = neededTime / 1000;
		System.out.println((numberOfMsg / neededSeconds)
				+ " Messages in 1 second!");
		System.out.println("Anzahl Messages: " + numberOfMsg);
	}
}
