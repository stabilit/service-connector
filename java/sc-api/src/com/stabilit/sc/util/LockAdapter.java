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
package com.stabilit.sc.util;

/**
 * The Class LockAdapter. Adapter for Locks.
 */
public abstract class LockAdapter<T> implements Lockable<T> {

	/*
	 * (non-Javadoc)
	 * @see com.stabilit.sc.util.Lockable#run()
	 */
	@Override
	public T run() throws Exception {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.stabilit.sc.util.Lockable#run(java.lang.Object)
	 */
	@Override
	public T run(T obj) throws Exception {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.stabilit.sc.util.Lockable#run(java.lang.Object, java.lang.Object)
	 */
	@Override
	public T run(T obj1, T obj2) throws Exception {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.stabilit.sc.util.Lockable#run(java.lang.Object, java.lang.Object, java.lang.Object)
	 */
	@Override
	public T run(T obj1, T obj2, T obj3) throws Exception {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.stabilit.sc.util.Lockable#run(T[])
	 */
	@Override
	public T run(T... objects) throws Exception {
		return null;
	}
}
