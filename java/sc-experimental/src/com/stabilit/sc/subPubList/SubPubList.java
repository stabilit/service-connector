/*
 *-----------------------------------------------------------------------------*
 *                            Copyright © 2010 by                              *
 *                    STABILIT Informatik AG, Switzerland                      *
 *                            ALL RIGHTS RESERVED                              *
 *                                                                             *
 * Valid license from STABILIT is required for possession, use or copying.     *
 * This software or any other copies thereof may not be provided or otherwise  *
 * made available to any other person. No title to and ownership of the        *
 * software is hereby transferred. The information in this software is subject *
 * to change without notice and should not be construed as a commitment by     *
 * STABILIT Informatik AG.                                                     *
 *                                                                             *
 * All referenced products are trademarks of their respective owners.          *
 *-----------------------------------------------------------------------------*
 */
/**
 * 
 */
package com.stabilit.sc.subPubList;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author JTraber
 * 
 */
public class SubPubList implements Runnable {

	List<String> messages = Collections.synchronizedList(new LinkedList<String>());
	Map<String, Integer> serviceNames = Collections.synchronizedMap(new HashMap<String, Integer>());

	public synchronized String getNextMsg(String serviceName) {
		int index = 0;
		if (serviceNames.containsKey(serviceName)) {
			index = (Integer) serviceNames.get(serviceName);
			index++;
		}

		if (messages.size() <= index) {
			return null;
		}
		serviceNames.put(serviceName, index);
		String value = messages.get(index);
		notifyAll();
		return value;
	}

	public synchronized void putNewMsg(String msg) {
		messages.add(msg);
	}

	@Override
	public synchronized void run() {
		TreeMap<String, Integer> sortedMap;
		while (true) {
			sortedMap = new TreeMap<String, Integer>(new ComparatorMap(serviceNames));
			sortedMap.putAll(serviceNames);

			int index = 0;
			if (sortedMap.size() > 0) {
				index = sortedMap.firstEntry().getValue();
			}

			for (int i = 0; i < index; i++) {
				System.out.println("remove msg : " + messages.get(0));
				messages.remove(0);
			}

			if (index != 0) {
				for (Iterator<String> iterator = serviceNames.keySet().iterator(); iterator.hasNext();) {
					String serviceName = iterator.next();
					int count = serviceNames.get(serviceName);
					count -= index;
					serviceNames.put(serviceName, count);
				}
			}

			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public class ComparatorMap implements Comparator<Object> {

		private Map<String, Integer> unsortedMap;

		public ComparatorMap(Map<String, Integer> unsortedMap) {
			this.unsortedMap = unsortedMap;
		}

		@Override
		public int compare(Object o1, Object o2) {
			return unsortedMap.get(o1) > unsortedMap.get(o2) ? 1 : -1;
		}
	}
}
