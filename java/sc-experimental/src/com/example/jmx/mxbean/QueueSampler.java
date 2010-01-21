/*
 * QueueSampler.java - MXBean implementation for the QueueSampler MXBean.
 * This class must implement all the Java methods declared in the
 * QueueSamplerMXBean interface, with the appropriate behavior for each one.
 */

package com.example.jmx.mxbean;

import java.util.Date;
import java.util.Queue;

public class QueueSampler implements QueueSamplerMXBean {

    private Queue<String> queue;

    public QueueSampler(Queue<String> queue) {
        this.queue = queue;
    }

    public QueueSample getQueueSample() {
        synchronized (queue) {
            return new QueueSample(new Date(), queue.size(), queue.peek());
        }
    }

    public void clearQueue() {
        synchronized (queue) {
            queue.clear();
        }
    }

	@Override
	public String[] getQueue() {
		return (String[]) queue.toArray();
	}

	@Override
	public QueueSample[] getQueueSamples() {
		QueueSample[] samples = new QueueSample[2];
		
		samples[0] = new QueueSample(new Date(), queue.size(), queue.peek());
		queue.remove();
		samples[1] = new QueueSample(new Date(), queue.size(), queue.peek());
		return samples;
	}
}
