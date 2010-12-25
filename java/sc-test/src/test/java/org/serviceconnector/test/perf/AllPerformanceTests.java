/*
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
 */
package org.serviceconnector.test.perf;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.serviceconnector.test.perf.api.cln.APIAttachBenchmark;
import org.serviceconnector.test.perf.api.cln.APIExecuteBenchmark;
import org.serviceconnector.test.perf.api.cln.APIReceivePublicationBenchmark;
import org.serviceconnector.test.perf.api.cln.APISessionBenchmark;
import org.serviceconnector.test.perf.api.srv.APIPublishBenchmark;

@RunWith(Suite.class)
@Suite.SuiteClasses( {
	// API Client benchmarks
	APIAttachBenchmark.class,
	APISessionBenchmark.class, 
	APIExecuteBenchmark.class,
	APIReceivePublicationBenchmark.class,
	
	// API server benchmarks
	APIPublishBenchmark.class
	})
public class AllPerformanceTests {
}

