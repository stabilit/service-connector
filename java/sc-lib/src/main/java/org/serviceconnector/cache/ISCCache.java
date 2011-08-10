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
package org.serviceconnector.cache;

import java.util.Date;
import java.util.List;

/**
 * The Interface ISCCache. Abstracts SC caches.
 */
public interface ISCCache<T> {

	/**
	 * Gets the object from cache.
	 * 
	 * @param key
	 *            the key
	 * @return the object
	 */
	public abstract T get(Object key);

	/**
	 * Put the object in cache.
	 * 
	 * @param key
	 *            the key
	 * @param value
	 *            the value to store
	 * @param timeToLiveSeconds
	 *            seconds the object has to stay in cache from now on
	 */
	public abstract void put(Object key, T value, int timeToLiveSeconds);

	/**
	 * Replace the object in cache.
	 * 
	 * @param key
	 *            the key
	 * @param value
	 *            the value to store
	 * @param timeToLiveSeconds
	 *            seconds the object has to stay in cache from now on
	 */
	public abstract void replace(Object key, T value, int timeToLiveSeconds);

	/**
	 * Gets the keys with expiry check.
	 * 
	 * @return the keys with expiry check
	 */
	public abstract List<String> getKeysWithExpiryCheck();

	/**
	 * Gets the expiration time.
	 * 
	 * @param key
	 *            the key
	 * @return the expiration time
	 */
	public abstract Date getExpirationTime(String key);

	/**
	 * Gets the creation time.
	 * 
	 * @param key
	 *            the key
	 * @return the creation time
	 */
	public abstract Date getCreationTime(String key);

	/**
	 * Gets the last access time.
	 * 
	 * @param key
	 *            the key
	 * @return the last access time
	 */
	public abstract Date getLastAccessTime(String key);

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
	public abstract long getNumberOfMessagesInStore();

	/**
	 * Gets the disk store size.
	 * 
	 * @return the disk store size
	 */
	public abstract long getNumberOfMessagesInDiskStore();

	/**
	 * Removes the object from cache.
	 * 
	 * @param key
	 *            the key
	 * @return true, if successful
	 */
	public abstract T remove(Object key);

	/**
	 * Removes all the objects from cache.
	 */
	public abstract void removeAll();

	/**
	 * Destroys the cache.
	 */
	public abstract void destroy();
}
