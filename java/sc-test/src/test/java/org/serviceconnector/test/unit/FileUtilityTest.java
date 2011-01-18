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

import junit.framework.Assert;

import org.junit.Test;
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.util.FileUtility;

public class FileUtilityTest extends SuperUnitTest {
	
	/**
	 * Description: adjust absolute path<br>
	 * Expectation: returns unchanged pass
	 */
	@Test
	public void t01_adjustAbsolutePath() throws SCMPValidatorException {
		Assert.assertEquals("path is not the same", System.getProperty("user.dir"), FileUtility.adjustPath(System.getProperty("user.dir")));
	}

	/**
	 * Description: adjust relative path<br>
	 * Expectation: returns user-dir + relative path
	 */
	@Test
	public void t01_adjustrelativePath() throws SCMPValidatorException {
		Assert.assertEquals("path is not the same", System.getProperty("user.dir")+"\\log", FileUtility.adjustPath("../log"));
	}

	
}
