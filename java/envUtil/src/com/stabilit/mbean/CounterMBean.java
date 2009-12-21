package com.stabilit.mbean;

import java.util.Queue;

public interface CounterMBean {
	public int getCount();
	public void setCount(int count);
	public Queue<Counter> getCounters();
	public void setCounters(Queue<Counter> counters);
	public void doIt();
	public void initCounter();
}
