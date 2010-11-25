/*
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
 */
package org.serviceconnector.cache.impl;

// TODO: Auto-generated Javadoc
/**
 * The Interface ISCMPCacheImpl.
 */
public interface ICacheImpl {
	
	/**
	 * Gets the.
	 *
	 * @param key the key
	 * @return the object
	 */
	public abstract Object get(Object key);
	
	/**
	 * Put.
	 *
	 * @param key the key
	 * @param value the value
	 */
	public abstract void put(Object key, Object value);
	
	/**
	 * Removes the.
	 *
	 * @param key the key
	 * @return true, if successful
	 */
	public abstract boolean remove(Object key);
	
	/**
	 * Gets the element size.
	 *
	 * @return the element size
	 */
	public abstract int getElementSize();
	
	/**
	 * Gets the cache name.
	 *
	 * @return the cache name
	 */
	public abstract String getCacheName();
	
	/**
	 * Gets the memory store size.
	 *
	 * @return the memory store size
	 */
	public abstract long getMemoryStoreSize();
	
	/**
	 * Gets the disk store size.
	 *
	 * @return the disk store size
	 */
	public abstract long getDiskStoreSize();

	/**
	 * Gets the size in bytes.
	 *
	 * @return the size in bytes
	 */
	public abstract long getSizeInBytes();
}
