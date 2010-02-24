package com.stabilit.sc.msg;

import java.util.HashMap;
import java.util.Map;

import com.stabilit.sc.job.impl.EchoMessage;
import com.stabilit.sc.job.impl.FileSystemMessage;
import com.stabilit.sc.job.impl.FtpMessage;

public class MessageFactory implements IMessageFactory {
	private Map<String, IMessage> jobMap;
	
	public MessageFactory() {
		jobMap = new HashMap<String, IMessage>();
		IMessage echoJob = new EchoMessage();
		jobMap.put(echoJob.getKey(), echoJob);
		IMessage ftpJob = new FtpMessage();
		jobMap.put(ftpJob.getKey(), ftpJob);
		IMessage fsJob = new FileSystemMessage();
		jobMap.put(fsJob.getKey(), fsJob);
	}
	
	@Override
	public synchronized IMessage newJob(String key) {
		IMessage job = jobMap.get(key);
		if (job == null) {
			return job;
		}
		return job.newInstance();
	}

}
