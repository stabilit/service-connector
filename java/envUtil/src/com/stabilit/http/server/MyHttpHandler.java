package com.stabilit.http.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class MyHttpHandler implements HttpHandler {

	private byte[] buffer = new byte[128];
	private int messCount;

	@Override
	public void handle(HttpExchange t) throws IOException {
		InputStream is = t.getRequestBody();
		// read(is); // .. read the request body
		Headers responseHeaders = t.getResponseHeaders();
		responseHeaders.set("Content-Type", "text/plain");
		t.sendResponseHeaders(200, 0L);
		OutputStream responseBody = t.getResponseBody();
		responseBody.write(buffer);
		responseBody.flush();
		responseBody.close();
		is.close();
		// t.sendResponseHeaders(200, buffer.length);
		// OutputStream os = t.getResponseBody();
		// os.write(buffer);
		// os.close();
		// is.close();
		// t.close();
		// System.out.println("Message number " + messCount + " recieved!");
		messCount++;
	}

	public void read(InputStream is) {
		System.out.println(is.toString());
	}
}
