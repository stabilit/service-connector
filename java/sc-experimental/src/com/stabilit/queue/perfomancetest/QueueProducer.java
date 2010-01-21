package com.stabilit.queue.perfomancetest;

public class QueueProducer implements Runnable {

	private String identifier;
	private int numberOfMess;
	private long startTime;

	public QueueProducer(String id, int numberOfMess, long startTime) {
		identifier = id;
		this.numberOfMess = numberOfMess;
		this.startTime = startTime;
	}

	@Override
	public void run() {
		Sc2 servCon = Sc2.getInstance();
		for (int i = 0; i < numberOfMess; i++) {
			servCon.put("Message in queue from " + identifier);
//			System.out.println("Producing Message: Message from " + identifier);
		}
		long endTime = System.currentTimeMillis();
		System.out.println(" ---- Total Time : " + (endTime - startTime)
				+ " Ms");
		
	}
}
