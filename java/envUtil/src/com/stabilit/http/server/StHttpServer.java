package com.stabilit.http.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpServer;

public class StHttpServer {

	private static String ip = "127.0.0.1";
	private static int port = 8000;

	public static void main(String[] args) {
		if (args.length != 0) {
			ip = args[0];
			port = Integer.valueOf(args[1]);
		}
		System.setProperty("http.keepAlive", "false");
		System.setProperty("http.maxConnections", "10");
		StHttpServer server = new StHttpServer();
		server.run();
	}

	public void run() {
		HttpServer server = null;
		try {
			server = HttpServer.create(new InetSocketAddress(InetAddress
					.getByName(ip), port), 1);
			server.createContext("/", new MyHttpHandler());
			server.setExecutor(null); // creates a default executor
			server.start();
			synchronized (server) {
				server.wait();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			server.stop(0);
		}
	}
}
