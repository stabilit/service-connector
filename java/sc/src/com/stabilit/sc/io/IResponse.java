package com.stabilit.sc.io;

import com.stabilit.sc.message.IMessageResult;

public interface IResponse {

	public void setJobResult(IMessageResult messageResult) throws Exception;

	public void setSession(ISession session);

}
