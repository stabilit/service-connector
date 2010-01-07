package com.stabilit.sc.app.server.mina.http;

import java.io.ObjectOutputStream;
import java.io.OutputStream;

import com.stabilit.sc.io.IResponse;
import com.stabilit.sc.io.ISession;
import com.stabilit.sc.io.SCOP;
import com.stabilit.sc.job.IJobResult;

public class MinaHttpResponse implements IResponse {

	private HttpResponseMessage message;
	private ISession session;
	
	public MinaHttpResponse(HttpResponseMessage message) {
		this.message = message;
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
			OutputStream bodyOS = message.getBodyOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(bodyOS);
			SCOP scop = new SCOP(jobResult);
			if (this.session != null) {
			   scop.setSessionId(this.session.getId());
			}
			oos.writeObject(scop);
			oos.flush();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
	}

}
