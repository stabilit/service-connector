package com.stabilit.sc.app.client.echo;

import com.stabilit.sc.app.client.ClientApplication;
import com.stabilit.sc.app.client.IConnection;
import com.stabilit.sc.app.server.ServerException;
import com.stabilit.sc.context.ClientApplicationContext;
import com.stabilit.sc.io.SCMP;
import com.stabilit.sc.job.impl.EchoMessage;
import com.stabilit.sc.msg.IMessage;
import com.stabilit.sc.pool.ConnectionPoolFactory;

public class EchoClientApplicationKeepAlive extends ClientApplication {

	public EchoClientApplicationKeepAlive() {
	}
	
	@Override
	public void run() throws Exception {
		ClientApplicationContext applicationContext = (ClientApplicationContext) this.getContext();
		IConnection con = ConnectionPoolFactory.newInstance(applicationContext);
		if (con == null) {
			throw new ServerException("no client available");
		}

		int index = 0;
		con.connect();
		long start = System.currentTimeMillis();
		SCMP request = new SCMP();
		while (index++ < 10000) {
			IMessage msg = new EchoMessage();
			request.setBody(msg);
			msg.setAttribute("msg", "hello " + index);
			SCMP response = con.sendAndReceive(request);
			IMessage echoed = (IMessage) response.getBody();
		    System.out.println(echoed + " session = " + con.getSessionId());
		}
		long end = System.currentTimeMillis();
		System.out.println(" 10000 calls in time (ms) = " + (end - start));
		con.disconnect();
		con.destroy();
	}
}
