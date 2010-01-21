/*
 * QueueSamplerMXBean.java - MXBean interface describing the management
 * operations and attributes for the QueueSampler MXBean. In this case
 * there is a read-only attribute "QueueSample" and an operation "clearQueue".
 */

package com.example.jmx.mxbean;

public interface QueueSamplerMXBean {
    public QueueSample getQueueSample();    
    public QueueSample[] getQueueSamples();
    public void clearQueue();
    public String[] getQueue();
}
