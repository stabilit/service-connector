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
package test.stabilit.scm.common.scmp;

import org.junit.Test;

import com.stabilit.scm.common.cmd.SCMPValidatorException;
import com.stabilit.scm.common.scmp.SCMPVersion;

/**
 * The Class SCMPVersionTest.
 * 
 * @author JTrnka
 */
public class SCMPVersionTest {

	/**
	 * version compatibility tests.
	 * 
	 * @throws SCMPValidatorException
	 *             the sCMP validator exception
	 */
	@Test
	public void versionCompatibilityTest0() throws SCMPValidatorException {
		SCMPVersion.TEST.isSupported(SCMPVersion.TEST.toString());
	}

	/**
	 * Version compatibility test1.
	 * 
	 * @throws SCMPValidatorException
	 *             the sCMP validator exception
	 */
	@Test
	public void versionCompatibilityTest1() throws SCMPValidatorException {
		SCMPVersion.TEST.isSupported("3.2"); // TEST = 3.2
	}

	/**
	 * Version compatibility test2.
	 * 
	 * @throws SCMPValidatorException
	 *             the sCMP validator exception
	 */
	@Test
	public void versionCompatibilityTest2() throws SCMPValidatorException {
		SCMPVersion.TEST.isSupported("3.1");
	}

	/**
	 * Version compatibility test3.
	 * 
	 * @throws SCMPValidatorException
	 *             the sCMP validator exception
	 */
	@Test(expected = SCMPValidatorException.class)
	public void versionCompatibilityTest3() throws SCMPValidatorException {
		SCMPVersion.TEST.isSupported("3.3");
	}

	/**
	 * Version compatibility test4.
	 * 
	 * @throws SCMPValidatorException
	 *             the sCMP validator exception
	 */
	@Test(expected = SCMPValidatorException.class)
	public void versionCompatibilityTest4() throws SCMPValidatorException {
		SCMPVersion.TEST.isSupported("2.0");
	}

	/**
	 * Version compatibility test5.
	 * 
	 * @throws SCMPValidatorException
	 *             the sCMP validator exception
	 */
	@Test(expected = SCMPValidatorException.class)
	public void versionCompatibilityTest5() throws SCMPValidatorException {
		SCMPVersion.TEST.isSupported("4.0");
	}

	/**
	 * Version compatibility test10.
	 * 
	 * @throws SCMPValidatorException
	 *             the sCMP validator exception
	 */
	@Test(expected = SCMPValidatorException.class)
	public void versionCompatibilityTest10() throws SCMPValidatorException {
		SCMPVersion.TEST.isSupported("A.b");
	}

	/**
	 * Version compatibility test11.
	 * 
	 * @throws SCMPValidatorException
	 *             the sCMP validator exception
	 */
	@Test(expected = SCMPValidatorException.class)
	public void versionCompatibilityTest11() throws SCMPValidatorException {
		SCMPVersion.TEST.isSupported("11");
	}
}
