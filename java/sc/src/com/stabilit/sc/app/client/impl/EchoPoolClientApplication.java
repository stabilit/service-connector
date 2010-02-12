package com.stabilit.sc.app.client.impl;

import java.net.URL;

import com.stabilit.sc.app.client.ClientApplication;
import com.stabilit.sc.app.client.ClientConnectionFactory;
import com.stabilit.sc.app.client.IClient;
import com.stabilit.sc.app.server.ServerException;
import com.stabilit.sc.context.ClientApplicationContext;
import com.stabilit.sc.message.IMessage;
import com.stabilit.sc.message.IMessageResult;
import com.stabilit.sc.message.impl.EchoMessage;

public class EchoPoolClientApplication extends ClientApplication {

	public EchoPoolClientApplication() {
	}

	@Override
	public void run() throws Exception {
		ClientApplicationContext applicationContext = (ClientApplicationContext) this.getContext();
		String con = applicationContext.getConnection();
		URL url = applicationContext.getURL();
		IClient client = ClientConnectionFactory.newInstance(con);
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
				IMessage job = new EchoMessage();
				job.setAttribute("msg", "hello " + ++index);
				IMessageResult result = client.sendAndReceive(job);
				System.out.println(result.getMessage() + " session = " + client.getSessionId());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
