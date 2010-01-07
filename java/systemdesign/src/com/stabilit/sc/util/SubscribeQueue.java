package com.stabilit.sc.util;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.stabilit.sc.cmd.CommandException;
import com.stabilit.sc.job.IJob;
import com.stabilit.sc.job.ISubscribe;

public class SubscribeQueue {

	private static Map<String, IJob> subscribeMap = new ConcurrentHashMap<String, IJob>();
	
	public static String subscribe(IJob subscribeJob) {
		UUID uuid = UUID.randomUUID();
		String sUuid = uuid.toString();
		subscribeMap.put(sUuid, subscribeJob);
		if (subscribeJob instanceof ISubscribe) {
		   ((ISubscribe)subscribeJob).setSubsribeID(sUuid);
		   subscribeJob.setAttribute(ISubscribe.INDEX, 0);
		}
		return sUuid;
	}

	public static void unsubscribe(IJob unsubscribeJob) throws CommandException {
		if (unsubscribeJob instanceof ISubscribe == false) {
			throw new CommandException("job is not ISubscribe");
		}		
		String subscribeID = ((ISubscribe)unsubscribeJob).getSubscribeID();
		IJob subscribeJob = subscribeMap.get(subscribeID);
		if (subscribeJob == null) {
			throw new CommandException("subscribe id = " + subscribeID + " is not known");
		}
		subscribeMap.remove(subscribeID);
		return;
	}

	public static IJob get(IJob unsubscribeJob) throws CommandException {
		if (unsubscribeJob instanceof ISubscribe == false) {
			throw new CommandException("job is not ISubscribe");
		}		
		String subscribeID = ((ISubscribe)unsubscribeJob).getSubscribeID();
		IJob subscribeJob = subscribeMap.get(subscribeID);
		if (subscribeJob == null) {
			throw new CommandException("subscribe id = " + subscribeID + " is not known");
		}
		return subscribeMap.get(subscribeID);
	}

}
