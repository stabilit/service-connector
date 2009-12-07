package com.stabilit.environment;

public class QueueProducer implements Runnable {

	private RequestType type;

	public QueueProducer(RequestType type) {
		this.type = type;
	}

	@Override
	public void run() {
		Sc servCon = Sc.getInstance();

		for (int count = 0; true; count++) {
			boolean result = servCon.put("Hello World " + count, type);
			
			if(!result) {
				System.out.println("Producing Error: Hello World " + count + " " + type);
				count--;
			}
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
