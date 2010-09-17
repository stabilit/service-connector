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
package org.serviceconnector.common.util;

import java.text.ParseException;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.serviceconnector.common.cmd.SCMPValidatorException;
import org.serviceconnector.common.scmp.SCMPError;
import org.serviceconnector.common.util.DateTimeUtility;


/**
 * The Class ValidatorUtility. Provides validation functions for checking header fields of requestFs.
 * 
 * @author JTraber
 */
public final class ValidatorUtility {
	
	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(ValidatorUtility.class);
	
	/** The Constant IP_LIST_REGEX, regex for ip address list. */
	private static final String IP_LIST_REGEX = "(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})(/(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}))*?";
	/** The Constant PAT_IPLIST, pattern regex for ip address list. */
	private static final Pattern PAT_IPLIST = Pattern.compile(IP_LIST_REGEX);

	/**
	 * Instantiates a new validator utility.
	 */
	private ValidatorUtility() {
	}

	/**
	 * Validate local date time.
	 * 
	 * @param localDateTimeString
	 *            the local date time string
	 * @return the date
	 * @throws SCMPValidatorException
	 *             the SCMP validator exception
	 */
	public static Date validateLocalDateTime(String localDateTimeString) throws SCMPValidatorException {
		if (localDateTimeString == null) {
			return null;
		}

		Date localDateTime = null;
		try {
			localDateTime = DateTimeUtility.SDF.parse(localDateTimeString);
		} catch (ParseException ex) {
			throw new SCMPValidatorException(SCMPError.HV_WRONG_LDT, localDateTimeString);
		}
		return localDateTime;
	}

	/**
	 * Validate ip address list.
	 * 
	 * @param ipAddressListString
	 *            the ip address list string
	 * @throws SCMPValidatorException
	 *             the SCMP validator exception
	 */
	public static void validateIpAddressList(String ipAddressListString) throws SCMPValidatorException {
		Matcher m = PAT_IPLIST.matcher(ipAddressListString);
		if (!m.matches()) {
			throw new SCMPValidatorException(SCMPError.HV_WRONG_IPLIST_FORMAT, ipAddressListString);
		}
	}

	/**
	 * Validate integer.
	 * 
	 * @param lowerLimitInc
	 *            the lower inclusive limit
	 * @param intStringValue
	 *            the integer string value
	 * @param error
	 *            the error to be thrown in case of an invalidation
	 * @return the valid integer
	 * @throws SCMPValidatorException
	 *             the SCMP validator exception
	 */
	public static int validateInt(int lowerLimitInc, String intStringValue, SCMPError error)
			throws SCMPValidatorException {
		if (intStringValue == null) {
			throw new SCMPValidatorException(error, "IntValue must be set");
		}
		int intValue = 0;
		try {
			intValue = Integer.parseInt(intStringValue);
		} catch (NumberFormatException ex) {
			throw new SCMPValidatorException(error, "IntValue " + intStringValue + " must be numeric");
		}
		ValidatorUtility.validateInt(lowerLimitInc, intValue, error);
		return intValue;
	}

	/**
	 * Validate integer.
	 * 
	 * @param lowerLimitInc
	 *            the lower limit inclusive
	 * @param intValue
	 *            the integer value
	 * @param error
	 *            the error
	 * @throws SCMPValidatorException
	 *             the sCMP validator exception
	 */
	public static void validateInt(int lowerLimitInc, int intValue, SCMPError error) throws SCMPValidatorException {
		if (intValue < lowerLimitInc) {
			throw new SCMPValidatorException(error, "IntValue " + intValue + " too low");
		}
	}

	/**
	 * Validate integer.
	 * 
	 * @param lowerLimitInc
	 *            the lower inclusive limit
	 * @param intStringValue
	 *            the integer string value to validate
	 * @param upperLimitInc
	 *            the upper inclusive limit
	 * @param error
	 *            the error to be thrown in case of an invalidation
	 * @return the valid integer
	 * @throws SCMPValidatorException
	 *             the SCMP validator exception
	 */
	public static int validateInt(int lowerLimitInc, String intStringValue, int upperLimitInc, SCMPError error)
			throws SCMPValidatorException {
		if (intStringValue == null) {
			throw new SCMPValidatorException(error, "IntValue must be set");
		}
		int intValue = 0;
		try {
			intValue = Integer.parseInt(intStringValue);
		} catch (NumberFormatException ex) {
			throw new SCMPValidatorException(error, "IntValue " + intStringValue + " must be numeric");
		}
		ValidatorUtility.validateInt(lowerLimitInc, intValue, upperLimitInc, error);
		return intValue;
	}

	/**
	 * Validate integer.
	 * 
	 * @param lowerLimitInc
	 *            the lower limit inclusive
	 * @param intValue
	 *            the integer value
	 * @param upperLimitInc
	 *            the upper limit inclusive
	 * @param error
	 *            the error
	 * @throws SCMPValidatorException
	 *             the sCMP validator exception
	 */
	public static void validateInt(int lowerLimitInc, int intValue, int upperLimitInc, SCMPError error)
			throws SCMPValidatorException {
		if (intValue < lowerLimitInc || intValue > upperLimitInc) {
			throw new SCMPValidatorException(error, "IntValue " + intValue + " not within limits");
		}
	}

	/**
	 * Validate string.
	 * 
	 * @param minSizeInc
	 *            the minimum inclusive size
	 * @param stringValue
	 *            the string value
	 * @param maxSizeInc
	 *            the max inclusive size
	 * @throws SCMPValidatorException
	 *             the SCMP validator exception
	 */
	public static void validateStringLength(int minSizeInc, String stringValue, int maxSizeInc, SCMPError error)
			throws SCMPValidatorException {

		if (stringValue == null) {
			throw new SCMPValidatorException(error, "StringValue must be set");
		}
		int length = stringValue.getBytes().length;

		if (length < minSizeInc || length > maxSizeInc) {
			throw new SCMPValidatorException(error, "StringValue length " + length + " is not within limits "
					+ minSizeInc + " to " + maxSizeInc);
		}
	}

	/**
	 * Validate allowed characters.
	 * 
	 * @param stringValue
	 *            the string value
	 * @param error
	 *            the error
	 * @throws SCMPValidatorException
	 *             the SCMP validator exception
	 */
	public static void validateAllowedCharacters(String stringValue, SCMPError error) throws SCMPValidatorException {
		if (stringValue == null) {
			throw new SCMPValidatorException(error, "StringValue must be set");
		}
		byte[] buffer = stringValue.getBytes();

		for (int i = 0; i < buffer.length; i++) {
			if (ValidatorUtility.isCharacterAllowed(buffer[i]) == false) {
				throw new SCMPValidatorException(error, "String value contains forbidden character "
						+ new String(buffer));
			}
		}
	}

	/**
	 * Checks if is character is allowed.
	 * 
	 * @param ch
	 *            the character to check
	 * @return true, if is character allowed
	 */
	public static boolean isCharacterAllowed(byte ch) {
		// check if character is in allowed range and not the equal sign (61)
		return ch != 61 && ch >= 32 && ch < 127;
	}
}
