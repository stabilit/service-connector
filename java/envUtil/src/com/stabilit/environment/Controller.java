package com.stabilit.environment;

public class Controller {

	public static void main(String[] args) {
		Controller contr = new Controller();
		contr.runCase2();
	}

	public void runCase1() {
		QueueConsumer consumer1 = new QueueConsumer(RequestType.ONE);
		QueueConsumer consumer2 = new QueueConsumer(RequestType.THREE);
		QueueProducer producer1 = new QueueProducer(RequestType.ONE);
		QueueProducer producer2 = new QueueProducer(RequestType.THREE);
		new Thread(consumer1).start();
		new Thread(consumer2).start();
		new Thread(producer1).start();
		new Thread(producer2).start();
	}
	
	public void runCase2() {
		QueueConsumer consumer1 = new QueueConsumer(RequestType.ONE);
//		QueueConsumer consumer2 = new QueueConsumer(RequestType.TWO);
		QueueProducer producer1 = new QueueProducer(RequestType.ONE);
//		QueueProducer producer2 = new QueueProducer(RequestType.TWO);
		new Thread(consumer1).start();
//		new Thread(consumer2).start();
		new Thread(producer1).start();
//		new Thread(producer2).start();
	}
}
