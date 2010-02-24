package com.stabilit.sc.app.server.mina.http;

import java.io.ObjectOutputStream;
import java.io.OutputStream;

import com.stabilit.sc.io.IResponse;
import com.stabilit.sc.io.ISession;
import com.stabilit.sc.io.SCMP;

public class MinaHttpResponse implements IResponse {

	private HttpResponseMessage message;
	private SCMP scmp;
	private ISession session;
	
	public MinaHttpResponse(HttpResponseMessage message) {
		this.scmp = scmp;
		this.message = message;
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
			OutputStream bodyOS = message.getBodyOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(bodyOS);
			this.scmp = scmp;
			if (this.session != null) {
			   scmp.setSessionId(this.session.getId());
			}
			oos.writeObject(this.scmp);
			oos.flush();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
	}

}
