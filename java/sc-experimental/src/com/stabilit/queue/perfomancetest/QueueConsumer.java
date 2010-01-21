package com.stabilit.queue.perfomancetest;

public class QueueConsumer implements Runnable {

	private String identifier;
	private int numberOfMess;
	private long startTime;

	public QueueConsumer(String id, int numberOfMess, long startTime) {
		identifier = id;
		this.numberOfMess = numberOfMess;
		this.startTime = startTime;
	}

	public void run() {
		Sc2 servCon = Sc2.getInstance();
		for (int i = 0; i < numberOfMess; i++) {
			Request req = null;
			try {
				req = servCon.poll();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (req == null)
				continue;
//			System.out.println("Message consumed from " + identifier + " : "
//					+ req.getMessage());
		}

		long endTime = System.currentTimeMillis();
		System.out.println(" ---- Total Time : " + (endTime - startTime)
				+ " Ms");
	}
}
