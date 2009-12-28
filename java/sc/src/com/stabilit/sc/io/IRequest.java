package com.stabilit.sc.io;

import com.stabilit.sc.message.IMessage;

public interface IRequest {

	public String getKey();

	public IMessage getJob();

}
