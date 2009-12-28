package com.stabilit.sc.app.server.mina.http;

import com.stabilit.sc.io.IRequest;
import com.stabilit.sc.io.SCOP;
import com.stabilit.sc.job.IJob;

public class MinaHttpRequest implements IRequest {

	private HttpRequestMessage message;
	private SCOP scop;

	public MinaHttpRequest(HttpRequestMessage message) {
		this.message = message;
		this.scop = (SCOP)message.getObj();
	}

	@Override
	public IJob getJob() {
		if (scop == null) {
			load();
		}
		return (IJob) scop.getBody();
	}

	@Override
	public String getKey() {
		IJob job = this.getJob();
		if (job != null) {
			return job.getKey();
		}
		return null;
	}

	private void load() {
		try {
//			InputStream is = httpExchange.getRequestBody();
//			ObjectInputStream ois = new ObjectInputStream(is);
//			Object obj = ois.readObject();
//			if (obj instanceof SCOP) {
//				this.scop = (SCOP) obj;
//			}
		} catch (Exception e) {
		}
	}

}
