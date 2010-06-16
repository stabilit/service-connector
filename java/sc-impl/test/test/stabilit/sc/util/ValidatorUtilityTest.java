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
package test.stabilit.sc.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import junit.framework.Assert;

import org.junit.Test;

import com.stabilit.scm.common.cmd.SCMPValidatorException;
import com.stabilit.scm.common.util.ValidatorUtility;

/**
 * The Class ValidatorUtilityTest. Tests the validator utility.
 * 
 * @author JTraber
 */
public class ValidatorUtilityTest {

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
		} catch (SCMPValidatorException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Validate keep alive test.
	 */
	@Test
	public void validateKeepAliveTest() {
		// TODO test keep alive Validation if necessary
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
			Assert.assertEquals("iplist has wrong format.", e.getMessage());
		}

		try {
			// wrong format of ip address, missing digets
			ValidatorUtility.validateIpAddressList("127.0.0");
			Assert.fail("Should throw exception");
		} catch (SCMPValidatorException e) {
			Assert.assertEquals("iplist has wrong format.", e.getMessage());
		}

		try {
			// wrong format of ip address, delimiter forbidden
			ValidatorUtility.validateIpAddressList("127.0.0.1/");
			Assert.fail("Should throw exception");
		} catch (SCMPValidatorException e) {
			Assert.assertEquals("iplist has wrong format.", e.getMessage());
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
			ValidatorUtility.validateInt(0, "1");
		} catch (SCMPValidatorException e) {
			Assert.fail("Should not throw exception");
		}

		try {
			// lower than lowerLimit
			ValidatorUtility.validateInt(0, "-1");
			Assert.fail("Should throw exception");
		} catch (SCMPValidatorException e) {
			Assert.assertEquals("intValue to low.", e.getMessage());
		}

		try {
			// no nummeric value
			ValidatorUtility.validateInt(0, "");
			Assert.fail("Should throw exception");
		} catch (SCMPValidatorException e) {
			Assert.assertEquals("intValue must be numeric.", e.getMessage());
		}

		// validate int value with lower & upper limit
		try {
			// greater than lowerLimit and lower than upperLimit
			ValidatorUtility.validateInt(0, "1", 2);
		} catch (SCMPValidatorException e) {
			Assert.fail("Should not throw exception");
		}

		try {
			// lower than lowerLimit
			ValidatorUtility.validateInt(0, "-1", 2);
			Assert.fail("Should throw exception");
		} catch (SCMPValidatorException e) {
			Assert.assertEquals("intValue not within limits.", e.getMessage());
		}

		try {
			// higher than upperLimit
			ValidatorUtility.validateInt(0, "3", 2);
			Assert.fail("Should throw exception");
		} catch (SCMPValidatorException e) {
			Assert.assertEquals("intValue not within limits.", e.getMessage());
		}

		try {
			// no nummeric value
			ValidatorUtility.validateInt(0, "", 1);
			Assert.fail("Should throw exception");
		} catch (SCMPValidatorException e) {
			Assert.assertEquals("intValue must be numeric.", e.getMessage());
		}
	}

	/**
	 * Validate string test.
	 */
	@Test
	public void validateStringTest() {

		try {
			// length is between 1 and 4
			ValidatorUtility.validateString(1, "abc", FOUR);
			// length is between 1 and 4
			ValidatorUtility.validateString(1, "a", FOUR);
		} catch (SCMPValidatorException e) {
			Assert.fail("Should not throw exception");
		}

		try {
			// length is shorter than 2
			ValidatorUtility.validateString(2, "a", FOUR);
			Assert.fail("Should throw exception");
		} catch (SCMPValidatorException e) {
			Assert.assertEquals("stringValue length is not within limits.", e.getMessage());
		}

		try {
			// length is longer than 2
			ValidatorUtility.validateString(1, "abc", 2);
			Assert.fail("Should throw exception");
		} catch (SCMPValidatorException e) {
			Assert.assertEquals("stringValue length is not within limits.", e.getMessage());
		}
	}
}
