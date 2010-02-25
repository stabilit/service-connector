package com.stabilit.sc.app.client.echo;

import com.stabilit.sc.app.client.ClientApplication;
import com.stabilit.sc.app.client.IConnection;
import com.stabilit.sc.app.server.ServerException;
import com.stabilit.sc.context.ClientApplicationContext;
import com.stabilit.sc.io.SCMP;
import com.stabilit.sc.pool.ConnectionPoolFactory;

public class EchoStringClientApplication extends ClientApplication {

	public EchoStringClientApplication() {
	}

	@Override
	public void run() throws Exception {
		ClientApplicationContext applicationContext = (ClientApplicationContext) this.getContext();
		IConnection con = ConnectionPoolFactory.newInstance(applicationContext);
		if (con == null) {
			throw new ServerException("no client available");
		}
		int index = 0;
		while (true) {
			try {
				// Thread.sleep(2000);
				con.connect();
				SCMP request = new SCMP();
				request.setMessageId("echo");
				String msg = "hello " + ++index;
				request.setBody(msg);
				SCMP response = con.sendAndReceive(request);
				String echoed = (String) response.getBody();
				System.out.println(echoed + " session = " + con.getSessionId());
				con.disconnect();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
