package com.stabilit.sc.util;

import java.util.ArrayList;
import java.util.List;

import com.stabilit.sc.job.IJob;

public class EventQueue {
	
	private static EventQueue eventQueue = new EventQueue();

	private List<IJob> eventList = new ArrayList<IJob>();

	private EventQueue() {
	}

	public static EventQueue getInstance() {
		return eventQueue;
	}

	public synchronized void add(IJob job) {
		eventList.add(job);
		this.notifyAll();
	}

	public synchronized IJob get(int pos) {
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
