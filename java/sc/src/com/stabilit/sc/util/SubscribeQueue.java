package com.stabilit.sc.util;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.stabilit.sc.cmd.CommandException;
import com.stabilit.sc.message.IMessage;
import com.stabilit.sc.message.ISubscribe;

public class SubscribeQueue {

	private static Map<String, IMessage> subscribeMap = new ConcurrentHashMap<String, IMessage>();
	
	public static String subscribe(IMessage subscribeJob) {
		UUID uuid = UUID.randomUUID();
		String sUuid = uuid.toString();
		subscribeMap.put(sUuid, subscribeJob);
		if (subscribeJob instanceof ISubscribe) {
		   ((ISubscribe)subscribeJob).setSubsribeID(sUuid);
		   subscribeJob.setAttribute(ISubscribe.INDEX, 0);
		}
		return sUuid;
	}

	public static void unsubscribe(IMessage unsubscribeJob) throws CommandException {
		if (unsubscribeJob instanceof ISubscribe == false) {
			throw new CommandException("job is not ISubscribe");
		}		
		String subscribeID = ((ISubscribe)unsubscribeJob).getSubscribeID();
		IMessage subscribeJob = subscribeMap.get(subscribeID);
		if (subscribeJob == null) {
			throw new CommandException("subscribe id = " + subscribeID + " is not known");
		}
		subscribeMap.remove(subscribeID);
		return;
	}

	public static IMessage get(IMessage unsubscribeJob) throws CommandException {
		if (unsubscribeJob instanceof ISubscribe == false) {
			throw new CommandException("job is not ISubscribe");
		}		
		String subscribeID = ((ISubscribe)unsubscribeJob).getSubscribeID();
		IMessage subscribeJob = subscribeMap.get(subscribeID);
		if (subscribeJob == null) {
			throw new CommandException("subscribe id = " + subscribeID + " is not known");
		}
		return subscribeMap.get(subscribeID);
	}

}
