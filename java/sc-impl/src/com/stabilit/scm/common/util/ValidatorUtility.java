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
import com.stabilit.scm.common.log.listener.ExceptionPoint;
import com.stabilit.scm.common.scmp.internal.KeepAlive;

/**
 * The Class ValidatorUtility.
 * 
 * @author JTraber
 */
public final class ValidatorUtility {

	/** The KEEP ALIVE INTERVAL MAX. */
	private static final int KA_INTERVAL_MAX = 3600;
	/** The KEEP ALIVE TIMEOUT MAX. */
	private static final int KA_TIMEOUT_MAX = 3600;
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
	 */
	public static Date validateLocalDateTime(String localDateTimeString) throws SCMPValidatorException {
		if (localDateTimeString == null) {
			return null;
		}

		Date localDateTime = null;
		try {
			localDateTime = DateTimeUtility.SDF.parse(localDateTimeString);
		} catch (ParseException ex) {
			ExceptionPoint.getInstance().fireException(ValidatorUtility.class, ex);
			throw new SCMPValidatorException("ParseException when parsing localDateTime: " + localDateTimeString);
		}
		return localDateTime;
	}

	/**
	 * Validate keep alive.
	 * 
	 * @param keepAliveTimeout
	 *            the keep alive timeout
	 * @param keepAliveInterval
	 *            the keep alive interval
	 * @return the keep alive
	 * @throws SCMPValidatorException
	 * @throws ValidatorException
	 *             the validator exception
	 */
	public static KeepAlive validateKeepAlive(String keepAliveTimeout, String keepAliveInterval)
			throws SCMPValidatorException {
		int keepAliveTimeoutInt = 0;
		int keepAliveIntervalInt = 0;

		try {
			keepAliveTimeoutInt = Integer.parseInt(keepAliveTimeout);
			keepAliveIntervalInt = Integer.parseInt(keepAliveInterval);
		} catch (NumberFormatException ex) {
			ExceptionPoint.getInstance().fireException(ValidatorUtility.class, ex);
			throw new SCMPValidatorException("NumberFormatException when parsing keep alive parameters : "
					+ keepAliveTimeout + " - " + keepAliveInterval);
		}

		if (keepAliveInterval == null || keepAliveTimeout == null) {
			throw new SCMPValidatorException("keepAliveTimeout/keepAliveInterval need to be set");
		}

		if (keepAliveTimeoutInt > KA_TIMEOUT_MAX || keepAliveIntervalInt > KA_INTERVAL_MAX) {
			throw new SCMPValidatorException("keepAliveTimeout or keepAliveInterval is to high.");
		}

		if ((keepAliveTimeoutInt == 0 && keepAliveIntervalInt != 0)
				|| (keepAliveIntervalInt == 0 && keepAliveTimeoutInt != 0)) {
			throw new SCMPValidatorException(
					"keepAliveTimeout and keepAliveInterval must either be both zero or both non zero!");
		}
		return new KeepAlive(keepAliveTimeoutInt, keepAliveIntervalInt);
	}

	/**
	 * Validate ip address list.
	 * 
	 * @param ipAddressListString
	 *            the ip address list string
	 * @throws ValidatorException
	 *             the validator exception
	 */
	public static void validateIpAddressList(String ipAddressListString) throws SCMPValidatorException {
		Matcher m = PAT_IPLIST.matcher(ipAddressListString);
		if (!m.matches()) {
			throw new SCMPValidatorException("iplist has wrong format.");
		}
	}

	/**
	 * Validate int.
	 * 
	 * @param lowerLimit
	 *            the lower limit
	 * @param intStringValue
	 *            the int string value
	 * @throws ValidatorException
	 *             the validator exception
	 */
	public static int validateInt(int lowerLimit, String intStringValue) throws SCMPValidatorException {
		if (intStringValue == null) {
			throw new SCMPValidatorException("intValue must be set.");
		}
		int intValue = 0;
		try {
			intValue = Integer.parseInt(intStringValue);
		} catch (NumberFormatException ex) {
			ExceptionPoint.getInstance().fireException(ValidatorUtility.class, ex);
			throw new SCMPValidatorException("intValue must be numeric.");
		}

		if (intValue < lowerLimit) {
			throw new SCMPValidatorException("intValue to low.");
		}
		return intValue;
	}

	/**
	 * Validate int.
	 * 
	 * @param lowerLimit
	 *            the lower limit
	 * @param intStringValue
	 *            the int string value
	 * @param upperLimit
	 *            the upper limit
	 * @throws ValidatorException
	 *             the validator exception
	 */
	public static int validateInt(int lowerLimit, String intStringValue, int upperLimit) throws SCMPValidatorException {
		if (intStringValue == null) {
			throw new SCMPValidatorException("intValue must be set.");
		}
		int intValue = 0;
		try {
			intValue = Integer.parseInt(intStringValue);
		} catch (NumberFormatException ex) {
			ExceptionPoint.getInstance().fireException(ValidatorUtility.class, ex);
			throw new SCMPValidatorException("intValue must be numeric.");
		}

		if (intValue <= lowerLimit || intValue >= upperLimit) {
			throw new SCMPValidatorException("intValue not within limits.");
		}
		return intValue;
	}

	/**
	 * Validate string.
	 * 
	 * @param minSize
	 *            the min size
	 * @param stringValue
	 *            the string value
	 * @param maxSize
	 *            the max size
	 * @throws ValidatorException
	 *             the validator exception
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
