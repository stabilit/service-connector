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

import org.serviceconnector.Constants;
import org.serviceconnector.cmd.SCMPValidatorException;

/**
 * The Class SCSubscribeMessage.
 */
public class SCSubscribeMessage extends SCMessage {

	/** The mask. */
	private String mask;
	/** The actual mask. */
	private String actualMask;
	/** The no data interval in seconds. */
	private int noDataIntervalInSeconds;

	/**
	 * Instantiates a new sC subscribe message.
	 */
	public SCSubscribeMessage() {
		this.noDataIntervalInSeconds = Constants.DEFAULT_NO_DATA_INTERVAL_SECONDS;
	}

	/**
	 * Instantiates a new sC subscribe message.
	 * 
	 * @param data
	 *            the data
	 */
	public SCSubscribeMessage(byte[] data) {
		super(data);
	}

	/**
	 * Instantiates a new sC subscribe message.
	 * 
	 * @param data
	 *            the data
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
	 * @param mask
	 *            the new mask
	 * @throws SCMPValidatorException
	 *             the sCMP validator exception
	 */
	public void setMask(String mask) throws SCMPValidatorException {
		this.mask = mask;
	}

	/**
	 * Gets the actual mask.
	 * 
	 * @return the actual mask
	 */
	public String getActualMask() {
		return actualMask;
	}

	/**
	 * Sets the actual mask.
	 * 
	 * @param actualMask
	 *            the new actual mask
	 * @throws SCMPValidatorException
	 *             the sCMP validator exception
	 */
	public void setActualMask(String actualMask) throws SCMPValidatorException {
		// mask is set only internally by SC
		this.actualMask = actualMask;
	}

	/**
	 * Gets the no data interval in seconds.
	 * 
	 * @return the no data interval in seconds
	 */
	public int getNoDataIntervalInSeconds() {
		return this.noDataIntervalInSeconds;
	}

	/**
	 * Sets the no data interval in seconds.
	 * 
	 * @param noDataIntervalInSeconds
	 *            the new no data interval in seconds
	 * @throws SCMPValidatorException
	 *             the sCMP validator exception
	 */
	public void setNoDataIntervalInSeconds(Integer noDataIntervalInSeconds) throws SCMPValidatorException {
		if (noDataIntervalInSeconds == null) {
			this.noDataIntervalInSeconds = 0;
			return;
		}
		this.noDataIntervalInSeconds = noDataIntervalInSeconds;
	}
}
