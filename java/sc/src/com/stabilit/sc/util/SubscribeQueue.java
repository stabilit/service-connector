package com.stabilit.sc.util;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.stabilit.sc.cmd.CommandException;
import com.stabilit.sc.io.SCMP;
import com.stabilit.sc.msg.IMessage;

public class SubscribeQueue {

	private static Map<String, IMessage> subscribeMap = new ConcurrentHashMap<String, IMessage>();

	public static String subscribe(IMessage subscribeMessage) {
		UUID uuid = UUID.randomUUID();
		String sUuid = uuid.toString();
		subscribeMap.put(sUuid, subscribeMessage);
		subscribeMessage.setAttribute(SCMP.INDEX, 0);
		return sUuid;
	}

	public static void unsubscribe(String subscribeId) throws CommandException {
		IMessage subscribeMessage = subscribeMap.get(subscribeId);
		if (subscribeMessage == null) {
			throw new CommandException("subscribe id = " + subscribeId
					+ " is not known");
		}
		subscribeMap.remove(subscribeId);
		return;
	}

	public static IMessage get(String subscribeId) throws CommandException {
		IMessage subscribeJob = subscribeMap.get(subscribeId);
		if (subscribeJob == null) {
			throw new CommandException("subscribe id = " + subscribeId
					+ " is not known");
		}
		return subscribeMap.get(subscribeId);
	}

}
