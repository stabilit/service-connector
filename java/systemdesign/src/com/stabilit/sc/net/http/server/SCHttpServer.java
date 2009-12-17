package com.stabilit.sc.net.http.server;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.stabilit.sc.cmd.CommandException;
import com.stabilit.sc.cmd.ICommand;
import com.stabilit.sc.cmd.factory.ICommandFactory;
import com.stabilit.sc.cmd.factory.SCCommandFactory;
import com.stabilit.sc.io.IRequest;
import com.stabilit.sc.io.IResponse;
import com.stabilit.sc.net.http.io.SCHttpRequest;
import com.stabilit.sc.net.http.io.SCHttpResponse;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class SCHttpServer {

	public static void main(String[] args) {
		int port = 80;
		HttpServer httpServer = null;
		System.setProperty("http.keepAlive", "true");
		try {
			httpServer = HttpServer.create(new InetSocketAddress(port), 0);
			httpServer.createContext("/", new SCHttpHandler());			
			httpServer.start();
			synchronized (httpServer) {
				httpServer.wait();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			httpServer.stop(0);
		}

	}

	static class SCHttpHandler implements HttpHandler {
		private ICommandFactory commandFactory = new SCCommandFactory();

		public SCHttpHandler() {
		}

		@Override
		public void handle(HttpExchange httpExchange) throws IOException {
			try {
//				System.out.println("SCHttpHandler.handle()");
				IRequest request = new SCHttpRequest(httpExchange);
				IResponse response = new SCHttpResponse(httpExchange);
				ICommand command = commandFactory.newCommand(request);
				command.run(request, response);		
			} catch (CommandException e) {
				e.printStackTrace();
				throw new IOException(e);
			}
		}
	}
}
