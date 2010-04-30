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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.stabilit.sc.common.scmp.KeepAlive;

/**
 * @author JTraber
 * 
 */
public class ValidatorUtility {

	private static final String SC_VERSION_REGEX = "(\\d\\.\\d)-(\\d*)";
	private static final String IP_LIST_REGEX = "(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})(/(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}))*?";
	private static final Pattern pat = Pattern.compile(IP_LIST_REGEX);
	private static final Pattern pattern = Pattern.compile(SC_VERSION_REGEX);

	public static void validateSCVersion(String currenSCVersion, String incomingSCVersion)
			throws ValidatorException {

		Matcher matchCurr = pattern.matcher(currenSCVersion);
		matchCurr.find();

		Float currReleaseAndVersionNr = Float.parseFloat(matchCurr.group(1));
		int currRevisionNr = Integer.parseInt(matchCurr.group(2));

		Matcher matchIn = pattern.matcher(incomingSCVersion);
		matchIn.find();

		Float incReleaseAndVersionNr = Float.parseFloat(matchIn.group(1));
		int incRevisionNr = Integer.parseInt(matchIn.group(2));

		if (incReleaseAndVersionNr <= currReleaseAndVersionNr) {
			int incReleaseNr = incReleaseAndVersionNr.intValue();
			int currReleaseNr = currReleaseAndVersionNr.intValue();

			if (incReleaseNr != currReleaseNr) {
				throw new ValidatorException("SCVersion not compatible.");
			}
			if (incReleaseAndVersionNr.equals(currReleaseAndVersionNr) && incRevisionNr > currRevisionNr) {
				throw new ValidatorException("SCVersion not compatible.");
			}
		} else {
			throw new ValidatorException("SCVersion not compatible.");
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

	public static KeepAlive validateKeepAlive(String keepAliveTimeout, String keepAliveInterval)
			throws ValidatorException {
		int keepAliveTimeoutInt = Integer.parseInt(keepAliveTimeout);
		int keepAliveIntervalInt = Integer.parseInt(keepAliveInterval);

		if (keepAliveInterval == null || keepAliveTimeout == null) {
			throw new ValidatorException("keepAliveTimeout/keepAliveInterval need to be set");
		}

		if (keepAliveTimeoutInt > 3600 || keepAliveIntervalInt > 3600) {
			throw new ValidatorException("keepAliveTimeout or keepAliveInterval is to high.");
		}

		if ((keepAliveTimeoutInt == 0 && keepAliveIntervalInt != 0)
				|| (keepAliveIntervalInt == 0 && keepAliveTimeoutInt != 0)) {
			throw new ValidatorException(
					"keepAliveTimeout and keepAliveInterval must either be both zero or both non zero!");
		}
		return new KeepAlive(keepAliveTimeoutInt, keepAliveIntervalInt);
	}

	public static void validateIpAddressList(String ipAddressListString) throws ValidatorException {
		Matcher m = pat.matcher(ipAddressListString);
		if (!m.matches()) {
			throw new ValidatorException("iplist has wrong format.");
		}
	}

	public static void validateInt(int lowerLimit, String intStringValue) throws ValidatorException {
		if (intStringValue == null) {
			throw new ValidatorException("intValue must be set.");
		}
		int intValue = 0;
		try {
			intValue = Integer.parseInt(intStringValue);
		} catch (NumberFormatException ex) {
			// TODO ExceptionListenerSupport.fireException(this, ex);
			throw new ValidatorException("intValue must be numeric.");
		}

		if (intValue <= lowerLimit)
			// TODO ExceptionListenerSupport.fireException(this, ex);
			throw new ValidatorException("intValue to low.");
	}

	public static void validateInt(int lowerLimit, String intStringValue, int upperLimit)
			throws ValidatorException {
		if (intStringValue == null) {
			throw new ValidatorException("intValue must be set.");
		}
		int intValue = 0;
		try {
			intValue = Integer.parseInt(intStringValue);
		} catch (NumberFormatException ex) {
			// TODO ExceptionListenerSupport.fireException(this, ex);
			throw new ValidatorException("intValue must be numeric.");
		}

		if (intValue <= lowerLimit || intValue >= upperLimit)
			throw new ValidatorException("intValue not within limits.");
	}

	public static void validateString(int minSize, String stringValue, int maxSize)
			throws ValidatorException {

		if (stringValue == null) {
			throw new ValidatorException("stringValue must be set.");
		}
		int length = stringValue.getBytes().length;

		if (length < minSize || length > maxSize) {
			throw new ValidatorException("stringValue length is not within limits.");
		}
	}
}
