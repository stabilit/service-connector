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
package com.stabilit.sc.common.util;

import java.text.ParseException;
import java.util.Date;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.stabilit.sc.common.io.KeepAlive;
import com.stabilit.sc.common.io.SCMPHeaderAttributeKey;

/**
 * @author JTraber
 * 
 */
public class ValidatorUtility {

	private static final String SC_VERSION_REGEX = "(\\d\\.\\d)-(\\d*)";
	private static final String IP_LIST_REGEX = "(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}/??)+";

	// TODO validation rules are wrong at this time!
	public static void validateSCVersion(String currenSCVersion, String incomingSCVersion)
			throws ValidationExcpetion {
		Pattern pattern = Pattern.compile(SC_VERSION_REGEX);

		Matcher matchCurr = pattern.matcher(currenSCVersion);
		matchCurr.find();

		float currReleaseAndVersionNr = Float.parseFloat(matchCurr.group(1));
		int currRevisionNr = Integer.parseInt(matchCurr.group(2));

		Matcher matchIn = pattern.matcher(incomingSCVersion);
		matchIn.find();

		float incReleaseAndVersionNr = Float.parseFloat(matchIn.group(1));
		int incRevisionNr = Integer.parseInt(matchIn.group(2));

		if (incReleaseAndVersionNr <= currReleaseAndVersionNr) {
			if (incReleaseAndVersionNr == currReleaseAndVersionNr && incRevisionNr > currRevisionNr) {
				throw new ValidationExcpetion("SCVersion not compatible.");
			}
		} else {
			throw new ValidationExcpetion("SCVersion not compatible.");
		}
	}

	public static Date validateLocalDateTime(String localDateTimeString) throws ParseException {
		if (localDateTimeString == null) {
			return null;
		}
		// localDateTime validation with regex
		// Pattern pat = Pattern
		// .compile("[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}.[0-9]{3}\\+[0-9]{4}");
		// Matcher m = pat.matcher(localDateTimeString);
		// m.matches();
		Date localDateTime = DateTimeUtility.SDF.parse(localDateTimeString);
		return localDateTime;
	}

	public static Integer convertUnsignedInteger(Map<String, String> map, SCMPHeaderAttributeKey key,
			Integer defaultValue) {

		String obj = map.get(key.getName());

		if (obj == null) {
			return defaultValue;
		}
		Integer value = Integer.parseInt(obj);
		return value;
	}

	public static KeepAlive validateKeepAlive(String keepAliveTimeout, String keepAliveInterval)
			throws ValidationException {
		int keepAliveTimeoutInt = Integer.parseInt(keepAliveTimeout);
		int keepAliveIntervalInt = Integer.parseInt(keepAliveInterval);

		if (keepAliveInterval == null || keepAliveTimeout == null) {
			throw new ValidationException("keepAliveTimeout/keepAliveInterval need to be set");
		}

		if (keepAliveTimeoutInt > 3600 || keepAliveIntervalInt > 3600) {
			throw new ValidationException("keepAliveTimeout or keepAliveInterval is to high.");
		}

		if ((keepAliveTimeoutInt == 0 && keepAliveIntervalInt != 0)
				|| (keepAliveIntervalInt == 0 && keepAliveTimeoutInt != 0)) {
			throw new ValidationException(
					"keepAliveTimeout and keepAliveInterval must either be both zero or both non zero!");
		}
		return new KeepAlive(keepAliveTimeoutInt, keepAliveIntervalInt);
	}

	public static void validateIpAddressList(String ipAddressListString) throws ValidationException {

		Pattern pat = Pattern.compile(IP_LIST_REGEX);
		Matcher m = pat.matcher(ipAddressListString);
		if (!m.matches()) {
			throw new ValidationException("iplist has wrong format.");
		}
	}

	public static String validateInt(int lowerLimit, String intStringValue) throws ValidationException {
		if (intStringValue == null) {
			throw new ValidationException("intValue must be set.");
		}
		int intValue = 0;
		try {
			intValue = Integer.parseInt(intStringValue);
		} catch (NumberFormatException ex) {
			throw new ValidationException("intValue must be numeric.");
		}

		if (intValue <= lowerLimit)
			throw new ValidationException("intValue to low.");

		return intStringValue;
	}

	public static String validateInt(int lowerLimit, String intStringValue, int upperLimit)
			throws ValidationException {
		if (intStringValue == null) {
			throw new ValidationException("intValue must be set.");
		}
		int intValue = 0;
		try {
			intValue = Integer.parseInt(intStringValue);
		} catch (NumberFormatException ex) {
			throw new ValidationException("intValue must be numeric.");
		}

		if (intValue <= lowerLimit || intValue >= upperLimit)
			throw new ValidationException("intValue not within limits.");

		return intStringValue;
	}

	public static void validateString(int minSize, String stringValue, int maxSize)
			throws ValidationException {

		if (stringValue == null) {
			throw new ValidationException("stringValue must be set.");
		}

		int length = stringValue.getBytes().length;

		if (length < minSize || length > maxSize) {
			throw new ValidationException("stringValue length is not within limits.");
		}
	}
}
