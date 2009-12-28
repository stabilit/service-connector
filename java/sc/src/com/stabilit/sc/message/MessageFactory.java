package com.stabilit.sc.message;

import java.util.HashMap;
import java.util.Map;

import com.stabilit.sc.message.impl.EchoMessage;

public class MessageFactory implements IMessageFactory {
	private Map<String, IMessage> messageMap;
	
	public MessageFactory() {
		messageMap = new HashMap<String, IMessage>();
		IMessage echoMessage = new EchoMessage();
		messageMap.put(echoMessage.getKey(), echoMessage);
	}
	
	@Override
	public synchronized IMessage newMessage(String key) {
		IMessage message = messageMap.get(key);
		if (message == null) {
			return message;
		}
		return message.newInstance();
	}

}
