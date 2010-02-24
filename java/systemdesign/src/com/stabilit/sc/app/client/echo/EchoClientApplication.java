package com.stabilit.sc.app.client.echo;

import com.stabilit.sc.app.client.ClientApplication;
import com.stabilit.sc.app.client.IConnection;
import com.stabilit.sc.app.server.ServerException;
import com.stabilit.sc.context.ClientApplicationContext;
import com.stabilit.sc.io.SCMP;
import com.stabilit.sc.msg.IMessage;
import com.stabilit.sc.msg.impl.EchoMessage;
import com.stabilit.sc.pool.ConnectionPoolFactory;

public class EchoClientApplication extends ClientApplication {

	public EchoClientApplication() {
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
				IMessage message = new EchoMessage();
				request.setBody(message);
				message.setAttribute("msg", "hello " + ++index);
				SCMP response = con.sendAndReceive(request);
				IMessage echoed = (IMessage) response.getBody();
				System.out.println(echoed + " session = " + con.getSessionId());
				con.disconnect();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
