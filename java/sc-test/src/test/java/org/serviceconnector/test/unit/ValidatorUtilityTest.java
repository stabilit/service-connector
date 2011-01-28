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
package org.serviceconnector.test.unit;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.util.ValidatorUtility;

/**
 * The Class ValidatorUtilityTest. Tests the validator utility.
 * 
 * @author JTraber
 */
public class ValidatorUtilityTest extends SuperUnitTest {

	/** The Constant logger. */
	private final static Logger logger = Logger.getLogger(ValidatorUtilityTest.class);
	/** The FOUR. */
	private static final int FOUR = 4;
	/** The SDF. */
	private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

	/**
	 * Description: Validate local date time test<br>
	 * Expectation: passes
	 */
	@Test
	public final void t01_validateLocalDateTimeTest() throws SCMPValidatorException {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(System.currentTimeMillis());
		java.util.Date date = cal.getTime();
		String localDateTimeString = SDF.format(date);
		ValidatorUtility.validateDateTime(localDateTimeString, SCMPError.HV_WRONG_LDT);
	}

	/**
	 * Description: Validate ip address list<br>
	 * Expectation: passes
	 */
	@Test
	public void t02_validateIpAddressList() throws SCMPValidatorException {
		// simply one ip address
		ValidatorUtility.validateIpAddressList("127.0.0.1");
		// two ip addresses
		ValidatorUtility.validateIpAddressList("127.0.0.1/10.0.0.13");
		// five ip addresses, several correct formats
		ValidatorUtility.validateIpAddressList("127.0.0.1/10.0.0.1/1.1.1.1/150.100.100.103/1.123.120.121");
	}

	/**
	 * Description: Validate ip address list wrong format of ip address, too many digits<br>
	 * Expectation: throws SCMPValidatorException
	 */
	@Test(expected = SCMPValidatorException.class)
	public void t03_validateIpAddressList() throws SCMPValidatorException {
		ValidatorUtility.validateIpAddressList("127.0.0.1545");
	}

	/**
	 * Description: Validate ip address list wrong format of ip address, missing digits<br>
	 * Expectation: throws SCMPValidatorException
	 */
	@Test(expected = SCMPValidatorException.class)
	public void t04_validateIpAddressList() throws SCMPValidatorException {
		ValidatorUtility.validateIpAddressList("127.0.0");
	}
	
	/**
	 * Description: Validate ip address list delimiter forbidden<br>
	 * Expectation: throws SCMPValidatorException
	 */
	@Test(expected = SCMPValidatorException.class)
	public void t05_validateIpAddressList() throws SCMPValidatorException {
		ValidatorUtility.validateIpAddressList("127.0.0.1/");
	}


	/**
	 * Description: Validate integer test<br>
	 * Expectation: passes
	 */
	@Test
	public void t20_validateIntTest() throws SCMPValidatorException {
		ValidatorUtility.validateInt(0, "1", SCMPError.HV_WRONG_MAX_SESSIONS);
	}

	
	/**
	 * Description: Validate integer test lower than lowerLimit<br>
	 * Expectation: throws SCMPValidatorException
	 */
	@Test(expected = SCMPValidatorException.class)
	public void t21_validateIntTest() throws SCMPValidatorException {
		ValidatorUtility.validateInt(0, "-1", SCMPError.HV_WRONG_MAX_SESSIONS);
	}

	/**
	 * Description: Validate integer no nummeric value<br>
	 * Expectation: throws SCMPValidatorException
	 */
	@Test(expected = SCMPValidatorException.class)
	public void t22_validateIntTest() throws SCMPValidatorException {
		ValidatorUtility.validateInt(0, "", SCMPError.HV_WRONG_MAX_SESSIONS);
	}
	
	/**
	 * Description: Validate integer value with lower & upper limit<br>
	 * Expectation: passes
	 */
	@Test
	public void t23_validateIntTest() throws SCMPValidatorException {
		ValidatorUtility.validateInt(0, "1", 2, SCMPError.HV_WRONG_PORTNR);
	}

	
	/**
	 * Description: Validate integer lower than lowerLimit<br>
	 * Expectation: throws SCMPValidatorException
	 */
	@Test(expected = SCMPValidatorException.class)
	public void t24_validateIntTest() throws SCMPValidatorException {
		ValidatorUtility.validateInt(0, "-1", 2, SCMPError.HV_WRONG_PORTNR);
	}

	
	/**
	 * Description: Validate integer higher than upperLimit<br>
	 * Expectation: throws SCMPValidatorException
	 */
	@Test(expected = SCMPValidatorException.class)
	public void t25_validateIntTest() throws SCMPValidatorException {
		ValidatorUtility.validateInt(0, "3", 2, SCMPError.HV_WRONG_PORTNR);
	}

	/**
	 * Description: Validate integer no nummeric value<br>
	 * Expectation: throws SCMPValidatorException
	 */
	@Test(expected = SCMPValidatorException.class)
	public void t26_validateIntTest() throws SCMPValidatorException {
		ValidatorUtility.validateInt(0, "", 1, SCMPError.HV_WRONG_PORTNR);
	}

	
	/**
	 * Description: Validate integer test<br>
	 * Expectation: passes
	 */
	@Test
	public void t30_validateLongTest() throws SCMPValidatorException {
		ValidatorUtility.validateLong(0, "1", SCMPError.HV_WRONG_MAX_SESSIONS);
	}
	
	/**
	 * Description: Validate integer test lower than lowerLimit<br>
	 * Expectation: throws SCMPValidatorException
	 */
	@Test(expected = SCMPValidatorException.class)
	public void t31_validateLongTest() throws SCMPValidatorException {
		ValidatorUtility.validateLong(0, "-1", SCMPError.HV_WRONG_MAX_SESSIONS);
	}

	/**
	 * Description: Validate integer no nummeric value<br>
	 * Expectation: throws SCMPValidatorException
	 */
	@Test(expected = SCMPValidatorException.class)
	public void t32_validateLongTest() throws SCMPValidatorException {
		ValidatorUtility.validateLong(0, "", SCMPError.HV_WRONG_MAX_SESSIONS);
	}
	
	/**
	 * Description: Validate integer value with lower & upper limit<br>
	 * Expectation: passes
	 */
	@Test
	public void t33_validateLongTest() throws SCMPValidatorException {
		ValidatorUtility.validateLong(0, "1", 2, SCMPError.HV_WRONG_PORTNR);
	}

	
	/**
	 * Description: Validate integer lower than lowerLimit<br>
	 * Expectation: throws SCMPValidatorException
	 */
	@Test(expected = SCMPValidatorException.class)
	public void t34_validateLongTest() throws SCMPValidatorException {
		ValidatorUtility.validateLong(0, "-1", 2, SCMPError.HV_WRONG_PORTNR);
	}
	
	/**
	 * Description: Validate string test<br>
	 * Expectation: passes
	 */
	@Test
	public void t40_validateStringTest() throws SCMPValidatorException {
		// length is between 1 and 4
		ValidatorUtility.validateStringLength(1, "abc", FOUR, SCMPError.HV_WRONG_SESSION_INFO);
		// length is between 1 and 4
		ValidatorUtility.validateStringLength(1, "a", FOUR, SCMPError.HV_WRONG_SESSION_INFO);
	}

	/**
	 * Description: Validate string test length is shorter than 2<br>
	 * Expectation: throws SCMPValidatorException
	 */
	@Test(expected = SCMPValidatorException.class)
	public void t41_validateStringTest() throws SCMPValidatorException {
		ValidatorUtility.validateStringLength(2, "a", FOUR, SCMPError.HV_WRONG_SESSION_INFO);
	}

	/**
	 * Description: Validate string test length is longer than 2<br>
	 * Expectation: throws SCMPValidatorException
	 */
	@Test(expected = SCMPValidatorException.class)
	public void t42t40_validateStringTest_validateStringTest() throws SCMPValidatorException {
		ValidatorUtility.validateStringLength(1, "abc", 2, SCMPError.HV_WRONG_SESSION_INFO);
	}

}