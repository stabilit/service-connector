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
package com.stabilit.scm.common.util;

import java.text.ParseException;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.stabilit.scm.common.cmd.SCMPValidatorException;

/**
 * The Class ValidatorUtility.
 * 
 * @author JTraber
 */
public final class ValidatorUtility {
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
			throw new SCMPValidatorException("ParseException when parsing localDateTime: " + localDateTimeString);
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
			throw new SCMPValidatorException("iplist has wrong format.");
		}
	}

	/**
	 * Validate integer.
	 * 
	 * @param lowerLimitInc
	 *            the lower inclusive limit
	 * @param intStringValue
	 *            the integer string value
	 * @return the integer
	 * @throws SCMPValidatorException
	 *             the SCMP validator exception
	 */
	public static int validateInt(int lowerLimitInc, String intStringValue) throws SCMPValidatorException {
		if (intStringValue == null) {
			throw new SCMPValidatorException("intValue must be set.");
		}
		int intValue = 0;
		try {
			intValue = Integer.parseInt(intStringValue);
		} catch (NumberFormatException ex) {
			throw new SCMPValidatorException("intValue must be numeric.");
		}

		if (intValue < lowerLimitInc) {
			throw new SCMPValidatorException("intValue to low.");
		}
		return intValue;
	}

	/**
	 * Validate integer.
	 * 
	 * @param lowerLimitInc
	 *            the lower inclusive limit
	 * @param intStringValue
	 *            the integer string value
	 * @param upperLimitInc
	 *            the upper inclusive limit
	 * @return the integer
	 * @throws SCMPValidatorException
	 *             the SCMP validator exception
	 */
	public static int validateInt(int lowerLimitInc, String intStringValue, int upperLimitInc)
			throws SCMPValidatorException {
		if (intStringValue == null) {
			throw new SCMPValidatorException("intValue must be set.");
		}
		int intValue = 0;
		try {
			intValue = Integer.parseInt(intStringValue);
		} catch (NumberFormatException ex) {
			throw new SCMPValidatorException("intValue must be numeric.");
		}

		if (intValue < lowerLimitInc || intValue > upperLimitInc) {
			throw new SCMPValidatorException("intValue not within limits.");
		}
		return intValue;
	}

	/**
	 * Validate string.
	 * 
	 * @param minSize
	 *            the minimum size
	 * @param stringValue
	 *            the string value
	 * @param maxSize
	 *            the max size
	 * @throws SCMPValidatorException
	 *             the SCMP validator exception
	 */
	public static void validateString(int minSize, String stringValue, int maxSize) throws SCMPValidatorException {

		if (stringValue == null) {
			throw new SCMPValidatorException("stringValue must be set.");
		}
		int length = stringValue.getBytes().length;

		if (length < minSize || length > maxSize) {
			throw new SCMPValidatorException("stringValue length is not within limits.");
		}
	}

	/**
	 * Validate boolean. Be careful if booleanValue is null - null will be returned. If you unbox the return value to
	 * type of boolean a NullPointerException will be thrown.
	 * 
	 * @param booleanValue
	 *            the boolean value
	 * @return the boolean
	 */
	public static Boolean validateBoolean(String booleanValue) {
		if (booleanValue == null) {
			return null;
		}
		if ("0".equals(booleanValue)) {
			return false;
		}
		if ("1".equals(booleanValue)) {
			return true;
		}
		return null;
	}

	/**
	 * Validate boolean. Returns defaultValue if string value is not a valid boolean.
	 * 
	 * @param booleanValue
	 *            the boolean value
	 * @param defaultValue
	 *            the default value
	 * @return the boolean
	 */
	public static Boolean validateBoolean(String booleanValue, Boolean defaultValue) {
		Boolean booleanVal = ValidatorUtility.validateBoolean(booleanValue);
		if (booleanVal == null) {
			return defaultValue;
		}
		return booleanVal;
	}
}
