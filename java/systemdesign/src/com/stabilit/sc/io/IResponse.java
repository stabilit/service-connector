package com.stabilit.sc.io;

import com.stabilit.sc.job.IJobResult;

public interface IResponse {

	public void setJobResult(IJobResult jobResult) throws Exception;

	public void setSession(ISession session);

}
