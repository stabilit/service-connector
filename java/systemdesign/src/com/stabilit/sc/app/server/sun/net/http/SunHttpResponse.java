package com.stabilit.sc.app.server.sun.net.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import com.stabilit.sc.io.IResponse;
import com.stabilit.sc.io.ISession;
import com.stabilit.sc.io.SCMP;
import com.sun.net.httpserver.HttpExchange;

public class SunHttpResponse implements IResponse {

	private HttpExchange httpExchange;
	private ISession session;
	private SCMP scmp;
	
	public SunHttpResponse(HttpExchange httpExchange) {
		this.httpExchange = httpExchange;
		this.session = null;
		this.scmp = null;
	}

	@Override
	public void setSession(ISession session) {
        this.session = session;		
	}
	
	@Override
	public void setSCMP(SCMP scmp) {
		if (scmp == null) {
			return;
		}
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			this.scmp = scmp;
			if (this.session != null) {
			   this.scmp.setSessionId(this.session.getId());
			}
			oos.writeObject(this.scmp);
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
