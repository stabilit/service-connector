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
package org.serviceconnector.test.sc;

import org.junit.Test;
import org.serviceconnector.SCVersion;
import org.serviceconnector.cmd.SCMPValidatorException;


/**
 * The Class SCVersionTestCase.
 * 
 * @author JTrnka
 */
public final class SCVersionTestCase {

	/**
	 * version compatibility tests.
	 * 
	 * @throws SCMPValidatorException
	 *             the sCMP validator exception
	 */
	@Test
	public void versionCompatibilityTest0() throws SCMPValidatorException {
		SCVersion.TEST.isSupported(SCVersion.TEST.toString());
	}

	/**
	 * Version compatibility test1.
	 * 
	 * @throws SCMPValidatorException
	 *             the sCMP validator exception
	 */
	@Test
	public void versionCompatibilityTest1() throws SCMPValidatorException {
		SCVersion.TEST.isSupported("3.2-005"); // TEST = 3.2-5
	}

	/**
	 * Version compatibility test2.
	 * 
	 * @throws SCMPValidatorException
	 *             the sCMP validator exception
	 */
	@Test
	public void versionCompatibilityTest2() throws SCMPValidatorException {
		SCVersion.TEST.isSupported("3.2-003");
	}

	/**
	 * Version compatibility test3.
	 * 
	 * @throws SCMPValidatorException
	 *             the sCMP validator exception
	 */
	@Test
	public void versionCompatibilityTest3() throws SCMPValidatorException {
		SCVersion.TEST.isSupported("3.1-006");
	}

	/**
	 * Version compatibility test4.
	 * 
	 * @throws SCMPValidatorException
	 *             the sCMP validator exception
	 */
	@Test(expected = SCMPValidatorException.class)
	public void versionCompatibilityTest4() throws SCMPValidatorException {
		SCVersion.TEST.isSupported("3.3-001");
	}

	/**
	 * Version compatibility test5.
	 * 
	 * @throws SCMPValidatorException
	 *             the sCMP validator exception
	 */
	@Test(expected = SCMPValidatorException.class)
	public void versionCompatibilityTest5() throws SCMPValidatorException {
		SCVersion.TEST.isSupported("2.0-000");
	}

	/**
	 * Version compatibility test6.
	 * 
	 * @throws SCMPValidatorException
	 *             the sCMP validator exception
	 */
	@Test(expected = SCMPValidatorException.class)
	public void versionCompatibilityTest6() throws SCMPValidatorException {
		SCVersion.TEST.isSupported("4.0-001");
	}

	// formatting

	/**
	 * Version compatibility test10.
	 * 
	 * @throws SCMPValidatorException
	 *             the sCMP validator exception
	 */
	@Test(expected = SCMPValidatorException.class)
	public void versionCompatibilityTest10() throws SCMPValidatorException {
		SCVersion.TEST.isSupported("3.2-5");
	}

	/**
	 * Version compatibility test11.
	 * 
	 * @throws SCMPValidatorException
	 *             the sCMP validator exception
	 */
	@Test(expected = SCMPValidatorException.class)
	public void versionCompatibilityTest11() throws SCMPValidatorException {
		SCVersion.TEST.isSupported("3.2.5");
	}

	/**
	 * Version compatibility test12.
	 * 
	 * @throws SCMPValidatorException
	 *             the sCMP validator exception
	 */
	@Test(expected = SCMPValidatorException.class)
	public void versionCompatibilityTest12() throws SCMPValidatorException {
		SCVersion.TEST.isSupported("a.b-c");
	}

	/**
	 * Version compatibility test13.
	 * 
	 * @throws SCMPValidatorException
	 *             the sCMP validator exception
	 */
	@Test(expected = SCMPValidatorException.class)
	public void versionCompatibilityTest13() throws SCMPValidatorException {
		SCVersion.TEST.isSupported("11");
	}
}
