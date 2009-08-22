package com.stabilit.sc.io;

import com.stabilit.sc.job.IJob;

public interface IRequest {

	public String getKey();

	public IJob getJob();

}
