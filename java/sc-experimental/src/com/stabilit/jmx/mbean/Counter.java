package com.stabilit.jmx.mbean;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class Counter implements CounterMBean {
	private int count = 0;
	private Queue<Counter> counters = new LinkedBlockingQueue<Counter>();

	public void count() {
		while (true) {
			count++;
			counters.add(this);
			System.out.println("Counter incremented: " + count);
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public int getCount() {
		return count;
	}

	@Override
	public void setCount(int count) {
		this.count = count;
	}

	@Override
	public void doIt() {
		System.out.println("done");
	}

	@Override
	public void initCounter() {
		count = 0;
	}

	@Override
	public Queue<Counter> getCounters() {
		return counters;
	}

	@Override
	public void setCounters(Queue<Counter> counters) {
		this.counters = counters;
	}
}
