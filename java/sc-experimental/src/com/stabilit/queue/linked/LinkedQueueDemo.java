package com.stabilit.queue.linked;

import com.stabilit.queue.linked.LinkedQueue.INode;

public class LinkedQueueDemo {

	public static void main(String[] args) {
		LinkedQueue<Object> lq = new LinkedQueue<Object>();
		INode<Object> start = lq.insert("hans");
		lq.insert("peter");
		lq.insert("karl");
		lq.insert("caro");
		
		while (start != null) {
			System.out.println(start.getValue());
			start = start.getNext();
		}
	}
}
