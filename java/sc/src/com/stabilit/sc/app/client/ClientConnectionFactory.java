package com.stabilit.sc.app.client;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import com.stabilit.sc.app.client.netty.http.HttpResponsePoolHandler;
import com.stabilit.sc.app.client.netty.http.NettyHttpClient;
import com.stabilit.sc.app.client.netty.http.NettyHttpPoolClient;
import com.stabilit.sc.pool.BlockingConnPool;
import com.stabilit.sc.pool.ClientKeepAliveHandler;
import com.stabilit.sc.pool.IConnectionPool;
import com.stabilit.sc.pool.NettyPoolImpl;

public class ClientConnectionFactory {

	private static Map<String, IClient> clientMap = new HashMap<String, IClient>();

	static {
		// jboss netty http client
		IConnectionPool pool = null;
		try {
			pool = new BlockingConnPool(1, new NettyPoolImpl(new URL("http://localhost:8080"),
					HttpResponsePoolHandler.class, ClientKeepAliveHandler.class));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		IClient nettyHttpPoolClient = new NettyHttpPoolClient(pool);
		clientMap.put(NettyHttpPoolClient.class.getName(), nettyHttpPoolClient);
		clientMap.put("netty.pool.http", nettyHttpPoolClient);

		IClient nettyHttpClient = new NettyHttpClient();
		clientMap.put(NettyHttpPoolClient.class.getName(), nettyHttpClient);
		clientMap.put("netty.http", nettyHttpClient);
	}

	public static IClient newInstance() {
		return newInstance("default");
	}

	public static IClient newInstance(String key) {
		IClient client = clientMap.get(key);
		return client;
	}

}
