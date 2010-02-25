package com.stabilit.sc.msg;

import java.util.HashMap;
import java.util.Map;

import com.stabilit.sc.msg.impl.EchoMessage;
import com.stabilit.sc.msg.impl.FileSystemMessage;
import com.stabilit.sc.msg.impl.FtpMessage;

public class MessageFactory implements IMessageFactory {
	private Map<String, IMessage> messageMap;
	
	private static IMessageFactory singleMessageFactory = new MessageFactory();
	
	public static IMessageFactory getMessageFactory() {
		return singleMessageFactory;
	}
	
	public MessageFactory() {
		messageMap = new HashMap<String, IMessage>();
		IMessage echoMessage = new EchoMessage();
		messageMap.put(echoMessage.getKey(), echoMessage);
		IMessage ftpMessage = new FtpMessage();
		messageMap.put(ftpMessage.getKey(), ftpMessage);
		IMessage fsMessage = new FileSystemMessage();
		messageMap.put(fsMessage.getKey(), fsMessage);
	}
	
	@Override
	public synchronized IMessage newMessage(String key) {
		IMessage job = messageMap.get(key);
		if (job == null) {
			return job;
		}
		return job.newInstance();
	}

}
