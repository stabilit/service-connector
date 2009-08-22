package com.stabilit.sc.net.http.io;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import com.stabilit.sc.io.IResponse;
import com.stabilit.sc.io.SCOP;
import com.stabilit.sc.job.IJobResult;
import com.sun.net.httpserver.HttpExchange;

public class SCHttpResponse implements IResponse {

	private HttpExchange httpExchange;
	
	public SCHttpResponse(HttpExchange httpExchange) {
		this.httpExchange = httpExchange;
	}

	@Override
	public void setJobResult(IJobResult jobResult) {
		if (jobResult == null) {
			return;
		}
		try {
			httpExchange.sendResponseHeaders(200, 0L);
			OutputStream os = httpExchange.getResponseBody();
			ObjectOutputStream oos = new ObjectOutputStream(os);
			SCOP scop = new SCOP(jobResult);
			oos.writeObject(scop);
			oos.flush();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		
	}

}
