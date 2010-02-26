package com.stabilit.sc.pool;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.stabilit.sc.app.client.ClientConnectionFactory;
import com.stabilit.sc.app.client.IClientConnection;
import com.stabilit.sc.context.ClientApplicationContext;

public class ConnectionPoolFactory {

	private static Map<String, List<IClientConnection>> connectionMap = new HashMap<String, List<IClientConnection>>();
	
	public static synchronized IClientConnection borrowConnection(ClientApplicationContext ctx) {
		List<IClientConnection> connectionList = connectionMap.get(ctx.getConnection());
		if (connectionList == null) {
			IClientConnection con = ClientConnectionFactory.newInstance(ctx.getConnection());
			if (con == null) {
				return null;
			}
			URL url = ctx.getURL();
			connectionList = new ArrayList<IClientConnection>();
			connectionMap.put(ctx.getConnection(), connectionList);
			PoolConnection poolCon = new PoolConnection(con); 
			connectionList.add(poolCon);
			// init
			for (int i = 0; i < 4; i++) {
				con = ClientConnectionFactory.newInstance(ctx.getConnection());
				con.setEndpoint(url);
				poolCon = new PoolConnection(con); 
				connectionList.add(poolCon);
			}
		}
		// get first available connection
		for (IClientConnection con : connectionList) {
			if (con.isAvailable()) {
				con.setAvailable(false);
				return con;
			}
		}
		return null;
	}
}
