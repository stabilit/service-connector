package com.stabilit.sc.pool;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import static com.stabilit.sc.SCKernelConstants.*;
import com.stabilit.sc.app.client.ClientConnectionFactory;
import com.stabilit.sc.app.client.IClientConnection;
import com.stabilit.sc.context.ClientApplicationContext;
import com.stabilit.sc.msg.IClientListener;

public class ConnectionPool {
	private static ConnectionPool instance;
	private static Map<String, List<IPoolConnection>> connectionMap = new ConcurrentHashMap<String, List<IPoolConnection>>();
	private int numOfConnections = 3;
	private Logger log = Logger.getLogger(ConnectionPool.class);

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

	public synchronized IPoolConnection lendConnection(ClientApplicationContext ctx,
			Class<? extends IClientListener> scListener) {

		// TODO scListener f�r connectionMap holen sinnvoll?
		List<IPoolConnection> connectionList = connectionMap.get(scListener.getName());
		PoolConnection poolCon = null;
		IClientConnection con = null;
		URL url = null;

		if (connectionList == null) {
			log.debug(ctx.getConnection() + ": borrows first connection from Pool");

			con = ClientConnectionFactory.newInstance(ctx.getConnection());
			if (con == null) {
				return null;
			}
			url = ctx.getURL();
			con.setEndpoint(url);
			connectionList = new ArrayList<IPoolConnection>();
			connectionMap.put(scListener.getName(), connectionList);

			// if (ctx.getAttribute(CLIENT_PROT).equals(HTTP)) {
			// poolCon = new BlockingPoolConnection(con, scListener);
			// } else {
			// poolCon = new PoolConnection(con, scListener);
			// }

			poolCon = new BlockingPoolConnection(con, scListener);
			connectionList.add(poolCon);
			poolCon.lend();

		} else {
			log.debug(ctx.getConnection() + ": borrows connection from Pool, connection in Pool: "
					+ connectionList.size());
			// get first available connection
			for (IPoolConnection conn : connectionList) {
				if (conn.isLendable() && conn.isWritable()) {
					((PoolConnection) conn).lend();
					return conn;
				}
			}

			if (connectionList.size() < numOfConnections) {
				url = ctx.getURL();
				con = ClientConnectionFactory.newInstance(ctx.getConnection());
				con.setEndpoint(url);
				// if (ctx.getAttribute(CLIENT_PROT).equals(HTTP)) {
				// poolCon = new BlockingPoolConnection(con, scListener);
				// } else {
				// poolCon = new PoolConnection(con, scListener);
				// }

				poolCon = new BlockingPoolConnection(con, scListener);
				connectionList.add(poolCon);
			}
		}
		return poolCon;
	}
}
