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
package com.stabilit.sc.scmp;

/**
 * The Class KeepAlive. Holds a concrete keep alive configuration of a connection.
 * 
 * @author JTraber
 */
public class KeepAlive {

	/** The keep alive timeout. */
	int keepAliveTimeout = 0;
	/** The keep alive interval. */
	int keepAliveInterval = 0;

	/**
	 * Instantiates a new keep alive.
	 * 
	 * @param keepAliveTimeout
	 *            the keep alive timeout
	 * @param keepAliveInterval
	 *            the keep alive interval
	 */
	public KeepAlive(int keepAliveTimeout, int keepAliveInterval) {
		this.keepAliveInterval = keepAliveInterval;
		this.keepAliveTimeout = keepAliveTimeout;
	}

	/**
	 * Gets the keep alive interval.
	 * 
	 * @return the keep alive interval
	 */
	public int getKeepAliveInterval() {
		return keepAliveInterval;
	}

	/**
	 * Sets the keep alive interval.
	 * 
	 * @param keepAliveInterval
	 *            the new keep alive interval
	 */
	public void setKeepAliveInterval(int keepAliveInterval) {
		this.keepAliveInterval = keepAliveInterval;
	}

	/**
	 * Gets the keep alive timeout.
	 * 
	 * @return the keep alive timeout
	 */
	public int getKeepAliveTimeout() {
		return keepAliveTimeout;
	}

	/**
	 * Sets the keep alive timeout.
	 * 
	 * @param keepAliveTimeout
	 *            the new keep alive timeout
	 */
	public void setKeepAliveTimeout(int keepAliveTimeout) {
		this.keepAliveTimeout = keepAliveTimeout;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return keepAliveTimeout + "," + keepAliveInterval;
	}
}
