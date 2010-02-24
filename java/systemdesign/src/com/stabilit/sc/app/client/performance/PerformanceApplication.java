package com.stabilit.sc.app.client.performance;

import com.stabilit.sc.app.client.ClientApplication;
import com.stabilit.sc.app.client.IConnection;
import com.stabilit.sc.app.server.ServerException;
import com.stabilit.sc.context.ClientApplicationContext;
import com.stabilit.sc.io.SCMP;
import com.stabilit.sc.msg.IMessage;
import com.stabilit.sc.msg.impl.EchoMessage;
import com.stabilit.sc.pool.ConnectionPoolFactory;

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
		IConnection con = ConnectionPoolFactory.newInstance(applicationContext);
		if (con == null) {
			throw new ServerException("no client available");
		}
		
		int numberOfMsg = Integer.valueOf(applicationContext.getArgs()[5]);
		
		int index = 0;
		startTime = System.currentTimeMillis();
		con.connect();
		SCMP request = new SCMP();
		for (int i = 0; i < numberOfMsg; i++) {
			try {						
				IMessage msg = new EchoMessage();
				request.setBody(msg);
				msg.setAttribute("msg", "hello " + ++index);
				SCMP result = con.sendAndReceive(request);
//				System.out.println(result.getJob());
				
			} catch (Exception e) {
				e.printStackTrace();
			}			
		}	
		con.disconnect();
		
		endTime = System.currentTimeMillis();
		long neededTime = endTime - startTime;
		System.out.println("Job Done in: " + neededTime + " Ms");
		double neededSeconds = neededTime / 1000;
		System.out.println((numberOfMsg / neededSeconds)
				+ " Messages in 1 second!");
		System.out.println("Anzahl Messages: " + numberOfMsg);
	}
}
