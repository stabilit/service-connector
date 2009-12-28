package com.stabilit.sc.job;

import java.util.HashMap;
import java.util.Map;

import com.stabilit.sc.job.impl.EchoJob;
import com.stabilit.sc.job.impl.FileSystemJob;
import com.stabilit.sc.job.impl.FtpJob;

public class JobFactory implements IJobFactory {
	private Map<String, IJob> jobMap;
	
	public JobFactory() {
		jobMap = new HashMap<String, IJob>();
		IJob echoJob = new EchoJob();
		jobMap.put(echoJob.getKey(), echoJob);
		IJob ftpJob = new FtpJob();
		jobMap.put(ftpJob.getKey(), ftpJob);
		IJob fsJob = new FileSystemJob();
		jobMap.put(fsJob.getKey(), fsJob);
	}
	
	@Override
	public synchronized IJob newJob(String key) {
		IJob job = jobMap.get(key);
		if (job == null) {
			return job;
		}
		return job.newInstance();
	}

}
