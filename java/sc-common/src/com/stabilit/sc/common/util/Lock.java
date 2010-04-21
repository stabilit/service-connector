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
package com.stabilit.sc.common.util;

import java.util.concurrent.locks.ReentrantLock;

/**
 * @author JTraber
 * 
 */
public class Lock<T> {

	final ReentrantLock reentrantLock = new ReentrantLock();

	public void lock() throws InterruptedException {
		reentrantLock.lock(); // will wait until this thread gets the lock
	}

	public T runLocked(Lockable<T> lockable, T... params) {
		try {
			reentrantLock.lock();
			return lockable.run(params);
		} catch (Exception e) {
			return null;
		} finally {
			this.unlock();
		}
	}

	public void unlock() {
		reentrantLock.unlock();
	}
}
