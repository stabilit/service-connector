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
package test.serviceconnector.common.scmp;

import org.junit.Test;
import org.serviceconnector.common.cmd.SCMPValidatorException;
import org.serviceconnector.common.scmp.SCMPVersion;


/**
 * The Class SCMPVersionTest.
 * 
 * @author JTrnka
 */
public class SCMPVersionTestCase {

	/**
	 * version compatibility tests.
	 * 
	 * @throws SCMPValidatorException
	 *             the sCMP validator exception
	 */
	@Test
	public void versionCompatibilityTest0() throws SCMPValidatorException {
		SCMPVersion.TEST.isSupported(SCMPVersion.TEST.toString().getBytes());
	}

	/**
	 * Version compatibility test1.
	 * 
	 * @throws SCMPValidatorException
	 *             the sCMP validator exception
	 */
	@Test
	public void versionCompatibilityTest1() throws SCMPValidatorException {
		String version = "3.2";
		SCMPVersion.TEST.isSupported(version.getBytes()); // TEST = 3.2
	}

	/**
	 * Version compatibility test2.
	 * 
	 * @throws SCMPValidatorException
	 *             the sCMP validator exception
	 */
	@Test
	public void versionCompatibilityTest2() throws SCMPValidatorException {
		String version = "3.1";
		SCMPVersion.TEST.isSupported(version.getBytes());
	}

	/**
	 * Version compatibility test3.
	 * 
	 * @throws SCMPValidatorException
	 *             the sCMP validator exception
	 */
	@Test(expected = SCMPValidatorException.class)
	public void versionCompatibilityTest3() throws SCMPValidatorException {
		String version = "3.3";
		SCMPVersion.TEST.isSupported(version.getBytes());
	}

	/**
	 * Version compatibility test4.
	 * 
	 * @throws SCMPValidatorException
	 *             the sCMP validator exception
	 */
	@Test(expected = SCMPValidatorException.class)
	public void versionCompatibilityTest4() throws SCMPValidatorException {
		String version = "2.0";
		SCMPVersion.TEST.isSupported(version.getBytes());
	}

	/**
	 * Version compatibility test5.
	 * 
	 * @throws SCMPValidatorException
	 *             the sCMP validator exception
	 */
	@Test(expected = SCMPValidatorException.class)
	public void versionCompatibilityTest5() throws SCMPValidatorException {
		String version = "4.0";
		SCMPVersion.TEST.isSupported(version.getBytes());
	}

	/**
	 * Version compatibility test10.
	 * 
	 * @throws SCMPValidatorException
	 *             the sCMP validator exception
	 */
	@Test(expected = SCMPValidatorException.class)
	public void versionCompatibilityTest10() throws SCMPValidatorException {
		String version = "A.b";
		SCMPVersion.TEST.isSupported(version.getBytes());
	}

	/**
	 * Version compatibility test11.
	 * 
	 * @throws SCMPValidatorException
	 *             the sCMP validator exception
	 */
	@Test(expected = SCMPValidatorException.class)
	public void versionCompatibilityTest11() throws SCMPValidatorException {
		String version = "11";
		SCMPVersion.TEST.isSupported(version.getBytes());
	}
}
