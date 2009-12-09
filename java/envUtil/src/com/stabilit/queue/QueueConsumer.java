package com.stabilit.queue;

public class QueueConsumer implements Runnable {

	private RequestType type;

	public QueueConsumer(RequestType type) {
		this.type = type;
	}

	public void run() {
		Sc servCon = Sc.getInstance();
		Request request;

		while (true) {
			if ((request = servCon.poll(type)) == null) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} else {
				System.out.println("Request consumed, (Type : "
						+ request.getType() + ")" + request.getMessage());
			}
		}
	}
}
