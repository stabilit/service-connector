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

import org.junit.Test;
import org.serviceconnector.SCVersion;
import org.serviceconnector.cmd.SCMPValidatorException;

/**
 * The Class SCVersionTestCase.
 * 
 * @author JTrnka
 */
public final class SCVersionTest extends SuperUnitTest{

	/**
	 * Description: Version compatibility test<br>
	 * Expectation: passes
	 */
	@Test
	public void t01_versionCompatibilityTest0() throws SCMPValidatorException {
		SCVersion.TEST.isSupported(SCVersion.TEST.toString());
	}

	/**
	 * Description: Version compatibility test1<br>
	 * Expectation: passes
	 */
	@Test
	public void t02_versionCompatibilityTest1() throws SCMPValidatorException {
		SCVersion.TEST.isSupported("3.2-005"); // TEST = 3.2-5
	}

	/**
	 * Description: Version compatibility test<br>
	 * Expectation: passes
	 */
	@Test
	public void t03_versionCompatibilityTest2() throws SCMPValidatorException {
		SCVersion.TEST.isSupported("3.2-003");
	}

	/**
	 * Description: Version compatibility test<br>
	 * Expectation: passes
	 */
	@Test
	public void t04_versionCompatibilityTest3() throws SCMPValidatorException {
		SCVersion.TEST.isSupported("3.1-006");
	}

	/**
	 * Description: Version compatibility test<br>
	 * Expectation: throws SCMPValidatorException
	 */
	@Test(expected = SCMPValidatorException.class)
	public void t05_versionCompatibilityTest4() throws SCMPValidatorException {
		SCVersion.TEST.isSupported("3.3-001");
	}

	/**
	 * Description: Version compatibility test<br>
	 * Expectation: throws SCMPValidatorException
	 */
	@Test(expected = SCMPValidatorException.class)
	public void t06_versionCompatibilityTest5() throws SCMPValidatorException {
		SCVersion.TEST.isSupported("2.0-000");
	}

	/**
	 * Description: Version compatibility test<br>
	 * Expectation: throws SCMPValidatorException
	 */
	@Test(expected = SCMPValidatorException.class)
	public void t07_versionCompatibilityTest6() throws SCMPValidatorException {
		SCVersion.TEST.isSupported("4.0-001");
	}

	// formatting

	/**
	 * Description: Version compatibility test<br>
	 * Expectation: throws SCMPValidatorException
	 */
	@Test(expected = SCMPValidatorException.class)
	public void t08_versionCompatibilityTest10() throws SCMPValidatorException {
		SCVersion.TEST.isSupported("3.2-5");
	}

	/**
	 * Description: Version compatibility test<br>
	 * Expectation: throws SCMPValidatorException
	 */
	@Test(expected = SCMPValidatorException.class)
	public void t09_versionCompatibilityTest11() throws SCMPValidatorException {
		SCVersion.TEST.isSupported("3.2.5");
	}

	/**
	 * Description: Version compatibility test<br>
	 * Expectation: throws SCMPValidatorException
	 */
	@Test(expected = SCMPValidatorException.class)
	public void t10_versionCompatibilityTest12() throws SCMPValidatorException {
		SCVersion.TEST.isSupported("a.b-c");
	}

	/**
	 * Description: Version compatibility test<br>
	 * Expectation: throws SCMPValidatorException
	 */
	@Test(expected = SCMPValidatorException.class)
	public void t11_versionCompatibilityTest13() throws SCMPValidatorException {
		SCVersion.TEST.isSupported("11");
	}
}
