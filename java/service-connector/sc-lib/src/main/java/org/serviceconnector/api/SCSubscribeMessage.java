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
package org.serviceconnector.api;

import org.serviceconnector.Constants;

/**
 * The Class SCSubscribeMessage.
 */
public class SCSubscribeMessage extends SCMessage {

	/**
	 * The mask. The mask is used in SUBSCRIBE / CHANGE_SUBSCRIPTION to express the client interest and in PUBLISH to designate the message contents. Only printable characters are
	 * allowed.
	 */
	private String mask;
	/**
	 * The actual mask. The actual client subscription mask filled by SC in SRV_CHANGE_SUBSCRIPTION.
	 */
	private String actualMask;
	/**
	 * The no data interval in seconds. Interval in seconds the SC will wait to deliver RECEIVE_PUBLICATION response with noData flag set.
	 */
	private int noDataIntervalSeconds;

	/**
	 * Instantiates a new sC subscribe message.
	 */
	public SCSubscribeMessage() {
		this.noDataIntervalSeconds = Constants.DEFAULT_NO_DATA_INTERVAL_SECONDS;
	}

	/**
	 * Instantiates a new sC subscribe message with byte[] data.
	 *
	 * @param data the data
	 */
	public SCSubscribeMessage(byte[] data) {
		super(data);
	}

	/**
	 * Instantiates a new sC subscribe message with String data.
	 *
	 * @param data the data
	 */
	public SCSubscribeMessage(String data) {
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
	 * @param mask Any printable character, length > 0 and < 256 Byte<br />
	 *        Client may not subscribe with mask containing "%" character.<br />
	 *        Example: "000012100012832102FADF-----------X-----------"
	 */
	public void setMask(String mask) {
		this.mask = mask;
	}

	/**
	 * Gets the actual mask. The actual client subscription mask filled by SC in SRV_CHANGE_SUBSCRIPTION.
	 *
	 * @return the actual mask
	 */
	public String getActualMask() {
		return actualMask;
	}

	/**
	 * Sets the actual mask.
	 *
	 * @param actualMask Validation: Any printable character, length < 256Byte.<br />
	 *        Example: "000012100012832102FADF-----------X-----------"
	 */
	public void setActualMask(String actualMask) {
		// mask is set only internally by SC
		this.actualMask = actualMask;
	}

	/**
	 * Gets the no data interval in seconds.
	 *
	 * @return the no data interval in seconds
	 */
	public int getNoDataIntervalSeconds() {
		return this.noDataIntervalSeconds;
	}

	/**
	 * Sets the no data interval in seconds. Interval in seconds the SC will wait to deliver RECEIVE_PUBLICATION response with noData flag set.
	 *
	 * @param noDataIntervalSeconds Validation: Number > 0 and < 3600<br />
	 *        Example: 60
	 */
	public void setNoDataIntervalSeconds(Integer noDataIntervalSeconds) {
		if (noDataIntervalSeconds == null) {
			this.noDataIntervalSeconds = 0;
			return;
		}
		this.noDataIntervalSeconds = noDataIntervalSeconds;
	}
}
