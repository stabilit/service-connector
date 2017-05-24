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
package org.serviceconnector.test.unit.scmp;

import org.junit.Test;
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.scmp.SCMPVersion;

/**
 * The Class SCMPVersionTest.
 *
 * @author JTrnka
 */
public class SCMPVersionTest {

	/**
	 * Description: Version compatibility test<br>
	 * Expectation: passes
	 */
	@Test
	public void t01_versionCompatibilityTest0() throws SCMPValidatorException {
		SCMPVersion.TEST.isSupported(SCMPVersion.TEST.toString().getBytes());
	}

	/**
	 * Description: Version compatibility test<br>
	 * Expectation: passes
	 */
	@Test
	public void t02_versionCompatibilityTest1() throws SCMPValidatorException {
		String version = "3.2";
		SCMPVersion.TEST.isSupported(version.getBytes()); // TEST = 3.2
	}

	/**
	 * Description: Version compatibility test<br>
	 * Expectation: passes
	 */
	@Test
	public void t03_versionCompatibilityTest2() throws SCMPValidatorException {
		String version = "3.1";
		SCMPVersion.TEST.isSupported(version.getBytes());
	}

	/**
	 * Description: Version compatibility test<br>
	 * Expectation: throws SCMPValidatorException
	 */
	@Test(expected = SCMPValidatorException.class)
	public void t04_versionCompatibilityTest3() throws SCMPValidatorException {
		String version = "3.3";
		SCMPVersion.TEST.isSupported(version.getBytes());
	}

	/**
	 * Description: Version compatibility test<br>
	 * Expectation: throws SCMPValidatorException
	 */
	@Test(expected = SCMPValidatorException.class)
	public void t05_versionCompatibilityTest4() throws SCMPValidatorException {
		String version = "2.0";
		SCMPVersion.TEST.isSupported(version.getBytes());
	}

	/**
	 * Description: Version compatibility test<br>
	 * Expectation: throws SCMPValidatorException
	 */
	@Test(expected = SCMPValidatorException.class)
	public void t06_versionCompatibilityTest5() throws SCMPValidatorException {
		String version = "4.0";
		SCMPVersion.TEST.isSupported(version.getBytes());
	}

	/**
	 * Description: Version compatibility test<br>
	 * Expectation: throws SCMPValidatorException
	 */
	@Test(expected = SCMPValidatorException.class)
	public void t07_versionCompatibilityTest10() throws SCMPValidatorException {
		String version = "A.b";
		SCMPVersion.TEST.isSupported(version.getBytes());
	}

	/**
	 * Description: Version compatibility test<br>
	 * Expectation: throws SCMPValidatorException
	 */
	@Test(expected = SCMPValidatorException.class)
	public void t08_versionCompatibilityTest11() throws SCMPValidatorException {
		String version = "11";
		SCMPVersion.TEST.isSupported(version.getBytes());
	}
}
