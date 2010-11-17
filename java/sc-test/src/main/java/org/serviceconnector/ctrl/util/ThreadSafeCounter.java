package org.serviceconnector.ctrl.util;

import org.apache.log4j.Logger;

public class ThreadSafeCounter {
	private volatile int counter = 0;

	public synchronized void increment() {
		counter++;
	}

	public synchronized void decrement() {
		counter--;
	}

	public int value() {
		return counter;
	}

	@Override
	public String toString() {
		return String.valueOf(value());
	}
}
