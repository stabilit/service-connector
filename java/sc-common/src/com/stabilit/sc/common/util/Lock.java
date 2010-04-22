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
