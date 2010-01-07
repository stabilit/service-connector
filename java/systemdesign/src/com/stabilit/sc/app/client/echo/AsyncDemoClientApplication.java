package com.stabilit.sc.app.client.echo;

import java.net.URL;

import com.stabilit.sc.app.client.ClientApplication;
import com.stabilit.sc.app.client.ClientConnectionFactory;
import com.stabilit.sc.app.client.IClient;
import com.stabilit.sc.app.server.ServerException;
import com.stabilit.sc.context.ClientApplicationContext;
import com.stabilit.sc.job.IJob;
import com.stabilit.sc.job.IJobResult;
import com.stabilit.sc.job.impl.SubscribeJob;
import com.stabilit.sc.job.impl.UnSubscribeJob;

public class AsyncDemoClientApplication extends ClientApplication {

	private IClient client;

	public AsyncDemoClientApplication() {
		this.client = null;
	}

	@Override
	public void run() throws Exception {
		ClientApplicationContext applicationContext = (ClientApplicationContext) this.getContext();
		String key = applicationContext.getKey();
		URL url = applicationContext.getURL();
		client = ClientConnectionFactory.newInstance(key);
		if (client == null) {
			client = ClientConnectionFactory.newInstance();
		}
		if (client == null) {
			throw new ServerException("no client available");
		}
		client.setEndpoint(url);

		// subscribe
		SubscribeJob subscribeJob = subscribe();

		int index = 0;
		while (index++ < 100) {
			try {
				client.connect();
				IJobResult result = client.receive(subscribeJob);
				System.out.println(result.getJob() + " session = " + client.getSessionId());
				client.disconnect();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		// unsubscribe
		unsubscribe(subscribeJob);

	}
	
	private SubscribeJob subscribe() throws Exception {
		client.connect();
		IJob job = new SubscribeJob();
		IJobResult result = client.sendAndReceive(job);
		System.out.println(result.getJob() + " session = " + client.getSessionId());
		client.disconnect();
		return (SubscribeJob)result.getJob();
	}
	
	private void unsubscribe(SubscribeJob subscribeJob) throws Exception {
		client.connect();
		IJob job = new UnSubscribeJob(subscribeJob);
		IJobResult result = client.sendAndReceive(job);
		System.out.println(result.getJob() + " session = " + client.getSessionId());
		client.disconnect();		
		
	}
	
}
