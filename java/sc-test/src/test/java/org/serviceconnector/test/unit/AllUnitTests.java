/*
 *       Copyright � 2010 STABILIT Informatik AG, Switzerland                  *
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
 */
package org.serviceconnector.test.unit;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.serviceconnector.test.unit.cln.SCClientTest;
import org.serviceconnector.test.unit.srv.NewServerTest;
import org.serviceconnector.test.unit.srv.SCServerTest;

@RunWith(Suite.class)
@Suite.SuiteClasses( { 
		SCMessageTest.class,	
		SCClientTest.class, 
		SCServerTest.class, 
		NewServerTest.class})
public class AllUnitTests {
}
