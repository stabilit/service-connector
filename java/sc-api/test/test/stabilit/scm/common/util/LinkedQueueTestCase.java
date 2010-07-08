/*
 *-----------------------------------------------------------------------------*
 *                                                                             *
 *       Copyright � 2010 STABILIT Informatik AG, Switzerland                  *
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
package test.stabilit.scm.common.util;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.stabilit.scm.common.scmp.SCMPMessage;
import com.stabilit.scm.common.util.LinkedNode;
import com.stabilit.scm.common.util.LinkedQueue;

/**
 * @author JTraber
 */
public class LinkedQueueTestCase {

	private LinkedQueue<SCMPMessage> queue;

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
		Assert.assertEquals("0", this.queue.getSize() + "");
		Assert.assertTrue(this.queue.isEmpty());
	}

	@Test
	public void manyConsumersOneProducerTest() {

	}

	@Test
	public void oneConsumerManyProducerTest() {

	}

	@Test
	public void manyConsumerManyProducerTest() {

	}

	private void insertIntoQueue(int numberOfMsg, String bodyString) {
		for (int i = 0; i < numberOfMsg; i++) {
			SCMPMessage message = new SCMPMessage();
			message.setBody(bodyString + i);
			this.queue.insert(message);
		}
	}
}
