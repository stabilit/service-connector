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
package org.serviceconnector.api;

/**
 * The Class SCPublishMessage.
 */
public class SCPublishMessage extends SCMessage {

	/** The mask. */
	private String mask = null;

	/**
	 * Instantiates a new sC publish message.
	 */
	public SCPublishMessage() {
	}

	/**
	 * Instantiates a new sC publish message.
	 * 
	 * @param data
	 *            the data
	 */
	public SCPublishMessage(byte[] data) {
		super(data);
	}

	/**
	 * Instantiates a new sC publish message.
	 * 
	 * @param data
	 *            the data
	 */
	public SCPublishMessage(String data) {
		super(data);
	}

	/**
	 * Gets the mask.
	 * 
	 * @return the mask
	 */
	public String getMask() {
		return mask;
	}

	/**
	 * Sets the mask.
	 * 
	 * @param mask
	 *            the new mask
	 */
	public void setMask(String mask) {
		this.mask = mask;
	}
}
