package com.stabilit.sc.app.server.sun.net.http;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.stabilit.sc.app.server.IServer;
import com.stabilit.sc.cmd.CommandException;
import com.stabilit.sc.cmd.ICommand;
import com.stabilit.sc.cmd.factory.ICommandFactory;
import com.stabilit.sc.cmd.factory.SCCommandFactory;
import com.stabilit.sc.io.IRequest;
import com.stabilit.sc.io.IResponse;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class SunHttpServer implements IServer {

	private HttpServer httpServer;

	@Override
	public void create() throws Exception {
		int port = 80;
		httpServer = HttpServer.create(new InetSocketAddress(port), 0);
		httpServer.createContext("/", new SCHttpHandler());
	}

	@Override
	public void run() throws Exception {
		httpServer.start();
		synchronized (httpServer) {
			httpServer.wait();
		}		
	}
	
	@Override
	public void destroy() throws Exception {
		httpServer.stop(0);		
	}
	
	static class SCHttpHandler implements HttpHandler {

		ICommandFactory commandFactory = new SCCommandFactory();

		public SCHttpHandler() {
		}

		@Override
		public void handle(HttpExchange httpExchange) throws IOException {
			try {
				IRequest request = new SunHttpRequest(httpExchange);
				IResponse response = new SunHttpResponse(httpExchange);
				ICommand command = commandFactory.newCommand(request);
				if (command == null) {
					throw new CommandException("invalid command");
				}
				command.run(request, response);
			} catch (CommandException e) {
				e.printStackTrace();
				throw new IOException(e);
			}
		}
	}
}
