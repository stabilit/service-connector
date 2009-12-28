package com.stabilit.sc.app.server.mina.http;

import java.io.ObjectOutputStream;
import java.io.OutputStream;

import com.stabilit.sc.io.IResponse;
import com.stabilit.sc.io.SCOP;
import com.stabilit.sc.job.IJobResult;

public class MinaHttpResponse implements IResponse {

	private HttpResponseMessage message;
	
	public MinaHttpResponse(HttpResponseMessage message) {
		this.message = message;
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
			oos.writeObject(scop);
			oos.flush();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
	}

}
