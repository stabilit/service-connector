/*
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
 */
package org.serviceconnector.cache;

/**
 * The Class CacheExpiredException.
 */
public class CacheExpiredException extends CacheException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -16989349079433186L;

	/**
	 * Instantiates a new SCMP cache exception.
	 */
	public CacheExpiredException() {
		super();
	}

	/**
	 * Instantiates a new SCMP cache exception.
	 *
	 * @param message the message
	 */
	public CacheExpiredException(String message) {
		super(message);
	}

	
}
