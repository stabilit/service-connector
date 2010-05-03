/*
 *-----------------------------------------------------------------------------*
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
 *-----------------------------------------------------------------------------*
/*
/**
 * 
 */
package test.stabilit.sc.common.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import test.stabilit.sc.common.net.DefaultFrameDecoderTest;
import test.stabilit.sc.common.net.HttpFrameDecoderTest;
import test.stabilit.sc.common.scmp.impl.LargeMessageEncoderDecoderTest;
import test.stabilit.sc.common.scmp.internal.SCMPCompositeTest;
import test.stabilit.sc.common.scmp.internal.SCMPLargeRequestTest;
import test.stabilit.sc.common.scmp.internal.SCMPLargeResponseTest;
import test.stabilit.sc.common.util.ValidatorUtilityTest;


/**
 * @author JTraber
 * 
 */
@RunWith(Suite.class)
@SuiteClasses( { DefaultFrameDecoderTest.class,
				 HttpFrameDecoderTest.class,
				 SCMPCompositeTest.class,
				 SCMPLargeRequestTest.class,
				 SCMPLargeResponseTest.class,
				 DefaultFrameDecoderTest.class,
				 LargeMessageEncoderDecoderTest.class, 
				 ValidatorUtilityTest.class})
public class CommonTest {
}
