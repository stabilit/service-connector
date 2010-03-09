package com.stabilit.sc.util;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.stabilit.sc.cmd.CommandException;

public class SubscribePublishQueue implements Runnable {

	private static Map<String, Integer> subscribeMap = new ConcurrentHashMap<String, Integer>();
	private List<String> messages = Collections.synchronizedList(new LinkedList<String>());
	Logger log = Logger.getLogger(SubscribePublishQueue.class);

	public synchronized String subscribe() {
		UUID uuid = UUID.randomUUID();
		String sUuid = uuid.toString();
		subscribeMap.put(sUuid, 0);
		log.debug("subscription for client with uuid: " + uuid);
		return sUuid;
	}

	public synchronized String getNextMsg(String uuid) {
		int index = 0;
		if (subscribeMap.containsKey(uuid)) {
			index = subscribeMap.get(uuid);
		}

		if (messages.size() <= index) {
			return null;
		}
		
		String value = messages.get(index);
		index++;
		subscribeMap.put(uuid, index);
		notifyAll();
		return value;
	}

	public synchronized void putNewMsg(String msg) {
		log.debug("Message put in SubPubList: " + msg);
		messages.add(msg);
	}

	public void unsubscribe(String subscribeId) throws CommandException {
		if (!subscribeMap.containsKey(subscribeId)) {
			throw new CommandException("sUuid: " + subscribeId + "not in Map, unable to unsubscribe.");
		}
		log.debug("subscription for client with sUuid deleted: " + subscribeId);
		subscribeMap.remove(subscribeId);
		return;
	}

	public Integer getNextIndex(String sUuid) throws CommandException {
		if (!subscribeMap.containsKey(sUuid)) {
			throw new CommandException("subscribe id = " + sUuid + " is not known");
		}
		log.debug("nextIndex for client with sUuid taken: " + sUuid);
		return subscribeMap.get(sUuid);
	}

	@Override
	public synchronized void run() {
		TreeMap<String, Integer> sortedMap;
		while (true) {
			sortedMap = new TreeMap<String, Integer>(new ComparatorMap(subscribeMap));
			sortedMap.putAll(subscribeMap);

			int index = 0;
			if (sortedMap.size() > 0) {
				index = sortedMap.firstEntry().getValue();
			}

			for (int i = 0; i < index; i++) {
				System.out.println("remove msg : " + messages.get(0));
				log.debug("message in queue has been sent and deleted: " + messages.get(0));
				messages.remove(0);
			}

			if (index != 0) {
				for (Iterator<String> iterator = subscribeMap.keySet().iterator(); iterator.hasNext();) {
					String uuid = iterator.next();
					int count = subscribeMap.get(uuid);
					count -= index;
					subscribeMap.put(uuid, count);
				}
			}

			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public class ComparatorMap implements Comparator<Object> {

		private Map<String, Integer> unsortedMap;

		public ComparatorMap(Map<String, Integer> subscribeMap) {
			this.unsortedMap = subscribeMap;
		}

		@Override
		public int compare(Object o1, Object o2) {
			return unsortedMap.get(o1) > unsortedMap.get(o2) ? 1 : -1;
		}
	}
}
