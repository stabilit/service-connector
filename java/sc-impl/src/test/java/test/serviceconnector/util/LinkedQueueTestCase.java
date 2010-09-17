/*
 *-----------------------------------------------------------------------------*
 *                                                                             *
 *       Copyright © 2010 STABILIT Informatik AG, Switzerland                  *
 *                                                                             *
 *  Licensed under the Apache License, Version 2.0 (the "License");            *
 *  you may not use this file except in compliance with the License.           *
 *  You may obtain a copy of the License at                                    *
 *                                                                             *
 *  http://www.apache.org/licenses/LICENSE-2.0                                 *
 *                                                                             *
 *  Unless required by applicable law or agreed to in writing, software        *
 *  distributed under the License is distributed on an "AS IS" BASIS,          *
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   *
 *  See the License for the specific language governing permissions and        *
 *  limitations under the License.                                             *
 *-----------------------------------------------------------------------------*
/*
/**
 * 
 */
package test.serviceconnector.util;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.util.LinkedNode;
import org.serviceconnector.util.LinkedQueue;


/**
 * @author JTraber
 */
public class LinkedQueueTestCase {

	private LinkedQueue<SCMPMessage> queue;
	public boolean killThreads = false;

	@Before
	public void setUp() {
		this.queue = new LinkedQueue<SCMPMessage>();
	}

	@Test
	public void produceAndConsumeSequenceTest() {
		int numberOfMsg = 10;
		String bodyString = "body nr: ";
		this.insertIntoQueue(numberOfMsg, bodyString);

		Assert.assertFalse(this.queue.isEmpty());

		Assert.assertEquals(numberOfMsg, this.queue.getSize());

		for (int i = 0; i < numberOfMsg; i++) {
			SCMPMessage message = this.queue.extract();
			Assert.assertEquals(bodyString + i, message.getBody().toString());
		}

		Assert.assertEquals(0, this.queue.getSize());
		Assert.assertTrue(this.queue.isEmpty());
	}

	@Test
	public void firstLastNodeManyElementsTest() {
		int numberOfMsg = 10;
		String bodyString = "body nr: ";
		this.insertIntoQueue(numberOfMsg, bodyString);

		LinkedNode<SCMPMessage> node = this.queue.getFirst();
		SCMPMessage message = node.value;
		Assert.assertEquals(bodyString + "0", message.getBody());

		node = this.queue.getLast();
		message = node.value;
		Assert.assertEquals(bodyString + "9", message.getBody());
	}

	@Test
	public void firstLastNodeNoElementsTest() {
		LinkedNode<SCMPMessage> node = this.queue.getFirst();
		Assert.assertNull(node);
		node = this.queue.getLast();
		Assert.assertEquals(null, node.getNext());
		Assert.assertEquals(null, node.getValue());
		Assert.assertEquals("0", this.queue.getSize() + "");
	}

	@Test
	public void firstLastNodeOneElementTest() {
		String body = "body";
		this.insertIntoQueue(1, body);

		LinkedNode<SCMPMessage> node = this.queue.getFirst();
		Assert.assertEquals(null, node.getNext());
		Assert.assertEquals(body + "0", node.getValue().getBody().toString());
		node = this.queue.getLast();
		Assert.assertEquals(null, node.getNext());
		Assert.assertEquals(body + "0", node.getValue().getBody().toString());
		Assert.assertEquals("1", this.queue.getSize() + "");
	}

	@Test
	public void emptyQueueTest() {
		Assert.assertTrue(this.queue.isEmpty());
		SCMPMessage message = this.queue.extract();
		Assert.assertNull(message);
	}

	@Test
	public void insertNullValueTest() {
		this.queue.insert(null);
		Assert.assertEquals("1", this.queue.getSize() + "");
		Assert.assertFalse(this.queue.isEmpty());
	}

	@Test
	public void manyConsumersOneProducerTest() throws Exception {
		QueueProducer producer = new QueueProducer(queue);
		QueueConsumer consumer1 = new QueueConsumer(queue);
		QueueConsumer consumer2 = new QueueConsumer(queue);
		QueueConsumer consumer3 = new QueueConsumer(queue);
		QueueConsumer consumer4 = new QueueConsumer(queue);
		producer.start();
		consumer1.start();
		consumer2.start();
		consumer3.start();
		consumer4.start();
		this.killThreads = true;
	}

	@Test
	public void oneConsumerManyProducerTest() throws Exception {
		QueueConsumer consumer = new QueueConsumer(queue);
		QueueProducer producer1 = new QueueProducer(queue);
		QueueProducer producer2 = new QueueProducer(queue);
		QueueProducer producer3 = new QueueProducer(queue);
		QueueProducer producer4 = new QueueProducer(queue);
		consumer.start();
		producer1.start();
		producer2.start();
		producer3.start();
		producer4.start();
		this.killThreads = true;
	}

	@Test
	public void produce10000Test() throws Exception {

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 128; i++) {
			sb.append(i);
		}
		this.insertIntoQueue(10000, sb.toString());
		Assert.assertEquals("10000", this.queue.getSize() + "");
	}

	private void insertIntoQueue(int numberOfMsg, String bodyString) {
		for (int i = 0; i < numberOfMsg; i++) {
			SCMPMessage message = new SCMPMessage();
			message.setBody(bodyString + i);
			this.queue.insert(message);
		}
	}

	private class QueueConsumer extends Thread {

		private LinkedQueue<SCMPMessage> queue;

		public QueueConsumer(LinkedQueue<SCMPMessage> queue) {
			this.queue = queue;
		}

		@Override
		public void run() {

			while (!LinkedQueueTestCase.this.killThreads) {
				SCMPMessage message = queue.extract();
				if (message == null) {
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
					}
				}
			}
		}
	}

	private class QueueProducer extends Thread {

		private LinkedQueue<SCMPMessage> queue;

		public QueueProducer(LinkedQueue<SCMPMessage> queue) {
			this.queue = queue;
		}

		@Override
		public void run() {
			int i = 0;
			while (!LinkedQueueTestCase.this.killThreads) {
				SCMPMessage message = new SCMPMessage();
				message.setBody(i++);
				queue.insert(message);
				try {
					Thread.sleep(600);
				} catch (InterruptedException e) {
				}
			}
		}
	}
}
