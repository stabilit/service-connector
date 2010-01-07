package com.stabilit.sc.app.client.echo;

import java.net.URL;

import com.stabilit.sc.app.client.ClientApplication;
import com.stabilit.sc.app.client.ClientConnectionFactory;
import com.stabilit.sc.app.client.IClient;
import com.stabilit.sc.app.server.ServerException;
import com.stabilit.sc.context.ClientApplicationContext;
import com.stabilit.sc.job.IJob;
import com.stabilit.sc.job.IJobResult;
import com.stabilit.sc.job.impl.EchoJob;

public class EchoClientApplication extends ClientApplication {

	public EchoClientApplication() {
	}

	@Override
	public void run() throws Exception {
		ClientApplicationContext applicationContext = (ClientApplicationContext) this.getContext();
		String key = applicationContext.getKey();
		URL url = applicationContext.getURL();
		IClient client = ClientConnectionFactory.newInstance(key);
		if (client == null) {
			client = ClientConnectionFactory.newInstance();
		}
		if (client == null) {
			throw new ServerException("no client available");
		}
		client.setEndpoint(url);

		int index = 0;
		while (true) {
			try {
				// Thread.sleep(2000);
				client.connect();
				IJob job = new EchoJob();
				job.setAttribute("msg", "hello " + ++index);
				IJobResult result = client.sendAndReceive(job);
				System.out.println(result.getJob() + " session = " + client.getSessionId());
				client.disconnect();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
