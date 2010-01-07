package com.stabilit.sc.app.server.sun.net.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import com.stabilit.sc.io.IResponse;
import com.stabilit.sc.io.ISession;
import com.stabilit.sc.io.SCOP;
import com.stabilit.sc.job.IJobResult;
import com.sun.net.httpserver.HttpExchange;

public class SunHttpResponse implements IResponse {

	private HttpExchange httpExchange;
	private ISession session;
	
	public SunHttpResponse(HttpExchange httpExchange) {
		this.httpExchange = httpExchange;
		this.session = null;
	}

	@Override
	public void setSession(ISession session) {
        this.session = session;		
	}
	
	@Override
	public void setJobResult(IJobResult jobResult) {
		if (jobResult == null) {
			return;
		}
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			SCOP scop = new SCOP(jobResult);
			if (this.session != null) {
			   scop.setSessionId(this.session.getId());
			}
			oos.writeObject(scop);
			oos.flush();
            byte[] stream = baos.toByteArray();
			httpExchange.sendResponseHeaders(200, stream.length);
			OutputStream os = httpExchange.getResponseBody();
			os.write(stream);
			os.close();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		
	}

}
