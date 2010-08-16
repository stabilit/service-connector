/*
 *-----------------------------------------------------------------------------*
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
 *-----------------------------------------------------------------------------*
/*
/**
 * 
 */
package com.stabilit.scm.common.service;

/**
 * The Class SCServiceException. Used to notify errors on SC service level.
 * 
 * @author JTraber
 */
public class SCServiceException extends Exception {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 75222936621899150L;

	/**
	 * Instantiates a new sC service exception.
	 * 
	 * @param message
	 *            the message
	 * @param cause
	 *            the cause
	 */
	public SCServiceException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Instantiates a new sC service exception.
	 * 
	 * @param message
	 *            the message
	 */
	public SCServiceException(String message) {
		super(message);
	}
}
