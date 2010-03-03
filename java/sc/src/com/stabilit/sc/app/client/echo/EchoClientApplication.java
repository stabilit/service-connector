package com.stabilit.sc.app.client.echo;

import com.stabilit.sc.app.client.ClientApplication;
import com.stabilit.sc.context.ClientApplicationContext;
import com.stabilit.sc.exception.ServerException;
import com.stabilit.sc.io.SCMP;
import com.stabilit.sc.msg.IMessage;
import com.stabilit.sc.msg.impl.EchoMessage;
import com.stabilit.sc.pool.ConnectionPool;
import com.stabilit.sc.pool.IPoolConnection;

public class EchoClientApplication extends ClientApplication {

	public EchoClientApplication() {
	}

	@Override
	public void run() throws Exception {
		ClientApplicationContext applicationContext = (ClientApplicationContext) this.getContext();
		ConnectionPool pool = ConnectionPool.getInstance();		
		IPoolConnection con = pool.borrowConnection(applicationContext, DefaultEventListener.class);
		if (con == null) {
			throw new ServerException("no client available");
		}
		int index = 0;
		while (true) {
			try {
				// Thread.sleep(2000);
				SCMP request = new SCMP();				
				IMessage message = new EchoMessage();
				request.setBody(message);
				message.setAttribute("msg", "hello " + ++index);
				SCMP response = con.sendAndReceive(request);
				IMessage echoed = (IMessage) response.getBody();
				System.out.println(echoed + " session = " + con.getSessionId());
				con.releaseConnection();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
