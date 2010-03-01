package com.stabilit.sc.pool;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.stabilit.sc.app.client.ClientConnectionFactory;
import com.stabilit.sc.app.client.IClientConnection;
import com.stabilit.sc.context.ClientApplicationContext;
import com.stabilit.sc.msg.ISCClientListener;

public class ConnectionPool {

	private static ConnectionPool instance;
	private static Map<String, List<IPoolConnection>> connectionMap = new ConcurrentHashMap<String, List<IPoolConnection>>();
	private int numOfConnections = 3;

	private ConnectionPool(int numOfConnections) {
		this.numOfConnections = numOfConnections;
	}

	public static synchronized ConnectionPool getInstance() {
		if (instance == null) {
			ConnectionPool.init(3);
		}
		return instance;
	}

	public static synchronized void init(int numOfConnections) {
		if (instance == null)
			instance = new ConnectionPool(numOfConnections);
	}

	public synchronized IPoolConnection borrowConnection(ClientApplicationContext ctx,
			Class<? extends ISCClientListener> scListener) {

		List<IPoolConnection> connectionList = connectionMap.get(ctx.getConnection());
		PoolConnection poolCon = null;
		IClientConnection con = null;
		URL url = null;

		if (connectionList == null) {
			con = ClientConnectionFactory.newInstance(ctx.getConnection());
			if (con == null) {
				return null;
			}
			url = ctx.getURL();
			con.setEndpoint(url);
			connectionList = new ArrayList<IPoolConnection>();
			connectionMap.put(ctx.getConnection(), connectionList);

			if (ctx.getAttribute("prot").equals("http")) {
				poolCon = new BlockingPoolConnection(con, scListener);
			} else {
				poolCon = new PoolConnection(con, scListener);
			}
			connectionList.add(poolCon);
			poolCon.setAvailable(false);

		} else {
			// get first available connection
			for (IPoolConnection conn : connectionList) {
				if (conn.isAvailable()) {
					((PoolConnection) conn).setAvailable(false);
					return conn;
				}
			}

			if (connectionList.size() < numOfConnections) {
				url = ctx.getURL();
				con = ClientConnectionFactory.newInstance(ctx.getConnection());
				con.setEndpoint(url);
				if (ctx.getAttribute("prot").equals("http")) {
					poolCon = new BlockingPoolConnection(con, scListener);
				} else {
					poolCon = new PoolConnection(con, scListener);
				}
				connectionList.add(poolCon);
			}
		}
		return poolCon;
	}
}
