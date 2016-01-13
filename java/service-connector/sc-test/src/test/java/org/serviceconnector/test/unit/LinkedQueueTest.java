/*-----------------------------------------------------------------------------*
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
 *-----------------------------------------------------------------------------*/
package org.serviceconnector.test.unit;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.scmp.SCMPVersion;
import org.serviceconnector.util.LinkedNode;
import org.serviceconnector.util.LinkedQueue;

/**
 * @author JTraber
 */
public class LinkedQueueTest extends SuperUnitTest {

	private LinkedQueue<SCMPMessage> queue;
	public boolean killThreads = false;

	@Before
	public void beforeOneTest() throws Exception {
		super.beforeOneTest();
		this.queue = new LinkedQueue<SCMPMessage>();
	}

	/**
	 * Description: Test sequence in queue is right<br>
	 * Expectation: passes
	 */
	@Test
	public void t01_ProduceAndConsumeSequenceTest() {
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

	/**
	 * Description: Test getFirst & getLast methods, queue filled<br>
	 * Expectation: passes
	 */
	@Test
	public void t02_FirstLastNodeManyElementsTest() {
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

	/**
	 * Description: Test getFirst & getLast methods, queue empty<br>
	 * Expectation: passes
	 */
	@Test
	public void t03_FirstLastNodeNoElementsTest() {
		LinkedNode<SCMPMessage> node = this.queue.getFirst();
		Assert.assertNull(node);
		node = this.queue.getLast();
		Assert.assertEquals(null, node.getNext());
		Assert.assertEquals(null, node.getValue());
		Assert.assertEquals("0", this.queue.getSize() + "");
	}

	/**
	 * Description: Test getFirst & getLast methods, queue contains one element<br>
	 * Expectation: passes
	 */
	@Test
	public void t04_FirstLastNodeOneElementTest() {
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

	/**
	 * Description: Test empty queue<br>
	 * Expectation: passes
	 */
	@Test
	public void t10_EmptyQueueTest() {
		Assert.assertTrue(this.queue.isEmpty());
		SCMPMessage message = this.queue.extract();
		Assert.assertNull(message);
	}

	/**
	 * Description: Insert null value in queue<br>
	 * Expectation: passes
	 */
	@Test
	public void t11_InsertNullValueTest() {
		this.queue.insert(null);
		Assert.assertEquals("1", this.queue.getSize() + "");
		Assert.assertFalse(this.queue.isEmpty());
	}

	/**
	 * Description: Many consumer one producer test<br>
	 * Expectation: passes
	 */
	@Test
	public void t20_ManyConsumersOneProducerTest() throws Exception {
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

	/**
	 * Description: One consumer many producer test<br>
	 * Expectation: passes
	 */
	@Test
	public void t21_OneConsumerManyProducerTest() throws Exception {
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

	/**
	 * Description: Insert 10000 elements in queue<br>
	 * Expectation: passes
	 */
	@Test
	public void t30_Produce10000Test() throws Exception {

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 128; i++) {
			sb.append(i);
		}
		this.insertIntoQueue(10000, sb.toString());
		Assert.assertEquals("10000", this.queue.getSize() + "");
	}

	private void insertIntoQueue(int numberOfMsg, String bodyString) {
		for (int i = 0; i < numberOfMsg; i++) {
			SCMPMessage message = new SCMPMessage(SCMPVersion.CURRENT);
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

			while (!LinkedQueueTest.this.killThreads) {
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
			while (!LinkedQueueTest.this.killThreads) {
				SCMPMessage message = new SCMPMessage(SCMPVersion.CURRENT);
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