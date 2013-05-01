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
package org.serviceconnector.util;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A {@link ThreadFactory} that sets names and priority to the threads created by this factory. Threads created by this factory
 * will take names in the form of the string <code>namePrefix + " thread-" + threadNum</code> where <tt>threadNum</tt> is the
 * count of threads created by this type of factory.
 */
public class NamedPriorityThreadFactory implements ThreadFactory {

	/** The number of threads created by this factory. */
	private static AtomicInteger threadNumber = new AtomicInteger(1);
	/** The name prefix for the thread names. */
	private final String namePrefix;
	/** The thread priority for the created threads. */
	private final int threadPrio;

	/**
	 * Constructor accepting the prefix and priority of the threads that will be created by this {@link ThreadFactory}
	 * 
	 * @param namePrefix
	 *            Prefix for names of threads
	 */
	public NamedPriorityThreadFactory(String namePrefix, int threadPrio) {
		this.namePrefix = namePrefix;
		this.threadPrio = threadPrio;
	}

	/**
	 * Constructor accepting the prefix of the threads that will be created by this {@link ThreadFactory}
	 * 
	 * @param namePrefix
	 *            the name prefix
	 */
	public NamedPriorityThreadFactory(String namePrefix) {
		this(namePrefix, Thread.NORM_PRIORITY);
	}

	/**
	 * Returns a new thread using a name and priority as specified by this factory {@inheritDoc}
	 */
	public Thread newThread(Runnable runnable) {
		Thread th = new Thread(runnable, namePrefix + " thread-" + threadNumber.getAndIncrement());
		th.setPriority(this.threadPrio);
		return th;
	}
}
