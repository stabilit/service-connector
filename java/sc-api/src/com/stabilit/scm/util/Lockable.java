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
package com.stabilit.scm.util;

/**
 * The Interface Lockable. Needs to be implemented if <code>Lock</code> construct wants to be used. Construct to get thread
 * safety.
 * 
 * @param <T> Type of Lockable
 * @author JTraber
 */
public interface Lockable<T> {

	/**
	 * Run.
	 * 
	 * @return the t
	 * @throws Exception
	 *             the exception
	 */
	public T run() throws Exception;

	/**
	 * Run.
	 * 
	 * @param obj
	 *            the obj
	 * @return the t
	 * @throws Exception
	 *             the exception
	 */
	public T run(T obj) throws Exception;

	/**
	 * Run.
	 * 
	 * @param obj1
	 *            the obj1
	 * @param obj2
	 *            the obj2
	 * @return the t
	 * @throws Exception
	 *             the exception
	 */
	public T run(T obj1, T obj2) throws Exception;

	/**
	 * Run.
	 * 
	 * @param obj1
	 *            the obj1
	 * @param obj2
	 *            the obj2
	 * @param obj3
	 *            the obj3
	 * @return the t
	 * @throws Exception
	 *             the exception
	 */
	public T run(T obj1, T obj2, T obj3) throws Exception;

	/**
	 * Run.
	 * 
	 * @param objects
	 *            the objects
	 * @return the t
	 * @throws Exception
	 *             the exception
	 */
	public T run(T... objects) throws Exception;
}
