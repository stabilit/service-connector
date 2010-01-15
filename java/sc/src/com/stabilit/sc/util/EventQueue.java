package com.stabilit.sc.util;

import java.util.ArrayList;
import java.util.List;

import com.stabilit.sc.message.IMessage;

public class EventQueue {
	
	private static EventQueue eventQueue = new EventQueue();

	private List<IMessage> eventList = new ArrayList<IMessage>();

	private EventQueue() {
	}

	public static EventQueue getInstance() {
		return eventQueue;
	}

	public synchronized void add(IMessage job) {
		eventList.add(job);
		this.notifyAll();
	}

	public synchronized IMessage get(int pos) {
		while (true) {
			if (eventList.size() > pos) {
				return eventList.get(pos);
			}
			try {
				wait();
			} catch (InterruptedException e) {
				return null;
			}
		}
	}

}
