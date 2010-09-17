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
package test.serviceconnector.common.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.serviceconnector.common.cmd.SCMPValidatorException;
import org.serviceconnector.common.scmp.SCMPError;
import org.serviceconnector.common.util.ValidatorUtility;


/**
 * The Class ValidatorUtilityTest. Tests the validator utility.
 * 
 * @author JTraber
 */
public class ValidatorUtilityTestCase {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(ValidatorUtilityTestCase.class);
	
	/** The FOUR. */
	private static final int FOUR = 4;
	/** The SDF. */
	private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

	/**
	 * Validate local date time test.
	 */
	@Test
	@SuppressWarnings("deprecation")
	public final void validateLocalDateTimeTest() {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(System.currentTimeMillis());
		java.util.Date date = cal.getTime();

		String localDateTimeString = SDF.format(date);
		try {
			Date validDate = ValidatorUtility.validateLocalDateTime(localDateTimeString);
			Assert.assertEquals(date.toGMTString(), validDate.toGMTString());
		} catch (SCMPValidatorException ex) {
			logger.error("validateLocalDateTimeTest", ex);
		}
	}

	/**
	 * Validate ip address list.
	 */
	@Test
	public void validateIpAddressList() {
		try {
			// simply one ip adress
			ValidatorUtility.validateIpAddressList("127.0.0.1");
			// two ip adresses
			ValidatorUtility.validateIpAddressList("127.0.0.1/10.0.0.13");
			// five ip adresses, serveral correct formats
			ValidatorUtility.validateIpAddressList("127.0.0.1/10.0.0.1/1.1.1.1/150.100.100.103/1.123.120.121");
		} catch (SCMPValidatorException e) {
			Assert.fail("Should not throw exception");
		}

		try {
			// wrong format of ip address, too many digets
			ValidatorUtility.validateIpAddressList("127.0.0.1545");
			Assert.fail("Should throw exception");
		} catch (SCMPValidatorException e) {
			Assert.assertEquals(SCMPError.HV_WRONG_IPLIST_FORMAT.getErrorText() + " [127.0.0.1545]", e.getMessage());
		}

		try {
			// wrong format of ip address, missing digets
			ValidatorUtility.validateIpAddressList("127.0.0");
			Assert.fail("Should throw exception");
		} catch (SCMPValidatorException e) {
			Assert.assertEquals(SCMPError.HV_WRONG_IPLIST_FORMAT.getErrorText() + " [127.0.0]", e.getMessage());
		}

		try {
			// wrong format of ip address, delimiter forbidden
			ValidatorUtility.validateIpAddressList("127.0.0.1/");
			Assert.fail("Should throw exception");
		} catch (SCMPValidatorException e) {
			Assert.assertEquals(SCMPError.HV_WRONG_IPLIST_FORMAT.getErrorText() + " [127.0.0.1/]", e.getMessage());
		}
	}

	/**
	 * Validate int test.
	 */
	@Test
	public void validateIntTest() {
		// validate int value with lower limit
		try {
			// greater than lowerLimit
			ValidatorUtility.validateInt(0, "1", SCMPError.HV_WRONG_MAX_SESSIONS);
		} catch (SCMPValidatorException e) {
			Assert.fail("Should not throw exception");
		}

		try {
			// lower than lowerLimit
			ValidatorUtility.validateInt(0, "-1", SCMPError.HV_WRONG_MAX_SESSIONS);
			Assert.fail("Should throw exception");
		} catch (SCMPValidatorException e) {
			Assert.assertEquals(SCMPError.HV_WRONG_MAX_SESSIONS.getErrorText() + " [IntValue -1 too low]", e
					.getMessage());
		}

		try {
			// no nummeric value
			ValidatorUtility.validateInt(0, "", SCMPError.HV_WRONG_MAX_SESSIONS);
			Assert.fail("Should throw exception");
		} catch (SCMPValidatorException e) {
			Assert.assertEquals(SCMPError.HV_WRONG_MAX_SESSIONS.getErrorText() + " [IntValue  must be numeric]", e
					.getMessage());
		}

		// validate int value with lower & upper limit
		try {
			// greater than lowerLimit and lower than upperLimit
			ValidatorUtility.validateInt(0, "1", 2, SCMPError.HV_WRONG_PORTNR);
		} catch (SCMPValidatorException e) {
			Assert.fail("Should not throw exception");
		}

		try {
			// lower than lowerLimit
			ValidatorUtility.validateInt(0, "-1", 2, SCMPError.HV_WRONG_PORTNR);
			Assert.fail("Should throw exception");
		} catch (SCMPValidatorException e) {
			Assert.assertEquals(SCMPError.HV_WRONG_PORTNR.getErrorText() + " [IntValue -1 not within limits]", e
					.getMessage());
		}

		try {
			// higher than upperLimit
			ValidatorUtility.validateInt(0, "3", 2, SCMPError.HV_WRONG_PORTNR);
			Assert.fail("Should throw exception");
		} catch (SCMPValidatorException e) {
			Assert.assertEquals(SCMPError.HV_WRONG_PORTNR.getErrorText() + " [IntValue 3 not within limits]", e
					.getMessage());
		}

		try {
			// no nummeric value
			ValidatorUtility.validateInt(0, "", 1, SCMPError.HV_WRONG_PORTNR);
			Assert.fail("Should throw exception");
		} catch (SCMPValidatorException e) {
			Assert.assertEquals(SCMPError.HV_WRONG_PORTNR.getErrorText() + " [IntValue  must be numeric]", e
					.getMessage());
		}
	}

	/**
	 * Validate string test.
	 */
	@Test
	public void validateStringTest() {

		try {
			// length is between 1 and 4
			ValidatorUtility.validateStringLength(1, "abc", FOUR, SCMPError.HV_WRONG_SESSION_INFO);
			// length is between 1 and 4
			ValidatorUtility.validateStringLength(1, "a", FOUR, SCMPError.HV_WRONG_SESSION_INFO);
		} catch (SCMPValidatorException e) {
			Assert.fail("Should not throw exception");
		}

		try {
			// length is shorter than 2
			ValidatorUtility.validateStringLength(2, "a", FOUR, SCMPError.HV_WRONG_SESSION_INFO);
			Assert.fail("Should throw exception");
		} catch (SCMPValidatorException e) {
			Assert.assertEquals(SCMPError.HV_WRONG_SESSION_INFO.getErrorText()
					+ " [StringValue length 1 is not within limits 2 to 4]", e.getMessage());
		}

		try {
			// length is longer than 2
			ValidatorUtility.validateStringLength(1, "abc", 2, SCMPError.HV_WRONG_SESSION_INFO);
			Assert.fail("Should throw exception");
		} catch (SCMPValidatorException e) {
			Assert.assertEquals(SCMPError.HV_WRONG_SESSION_INFO.getErrorText()
					+ " [StringValue length 3 is not within limits 1 to 2]", e.getMessage());
		}
	}
}
