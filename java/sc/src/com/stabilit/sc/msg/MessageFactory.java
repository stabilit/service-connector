package com.stabilit.sc.msg;

import java.util.HashMap;
import java.util.Map;

import com.stabilit.sc.msg.impl.EchoMessage;
import com.stabilit.sc.msg.impl.FileSystemMessage;
import com.stabilit.sc.msg.impl.FtpMessage;

public class MessageFactory implements IMessageFactory {
	private Map<String, IMessage> msgMap;
	
	public MessageFactory() {
		msgMap = new HashMap<String, IMessage>();
		IMessage echoMsg = new EchoMessage();
		msgMap.put(echoMsg.getKey(), echoMsg);
		IMessage ftpMsg = new FtpMessage();
		msgMap.put(ftpMsg.getKey(), ftpMsg);
		IMessage fsMsg = new FileSystemMessage();
		msgMap.put(fsMsg.getKey(), fsMsg);
	}
	
	@Override
	public synchronized IMessage newMsg(String key) {
		IMessage msg = msgMap.get(key);
		if (msg == null) {
			return msg;
		}
		return msg.newInstance();
	}

}
