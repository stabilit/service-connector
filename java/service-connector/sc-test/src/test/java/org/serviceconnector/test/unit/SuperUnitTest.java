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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.serviceconnector.log.Loggers;

public class SuperUnitTest {

	/** The Constant testLogger. */
	protected static final Logger testLogger = LoggerFactory.getLogger(Loggers.TEST.getValue());

	protected int threadCount = 0;

	@Rule
	public TestName name = new TestName();

	@Before
	public void beforeOneTest() throws Exception {
		testLogger.info(">> " + name.getMethodName() + " <<");
		threadCount = Thread.activeCount();
	}

	@After
	public void afterOneTest() {
		testLogger.info("Number of threads=" + Thread.activeCount() + " created=" + (Thread.activeCount() - threadCount));
	}
}
