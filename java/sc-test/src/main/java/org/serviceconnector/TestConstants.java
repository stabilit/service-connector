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
package org.serviceconnector;

public class TestConstants {
	public static final String HOST = "localhost";
	public static final int PORT_HTTP = 7000;
	public static final int PORT_TCP = 9000;
	public static final int PORT_MIN = 1;
	public static final int PORT_MAX = 65535;
	public static final int PORT_LISTENER = 30000; 

	public static final String SERVER_TYPE_SESSION = "session";
	public static final String SERVER_TYPE_PUBLISH = "publish";
	

	public static final String log4jSrvProperties = "log4j-srv.properties";
	public static final String log4jSCProperties = "log4j-sc.properties";
	public static final String log4jSCcascadedProperties = "log4j-sc-cascaded.properties";
	public static final String SCProperties = "sc.properties";
	public static final String SCcascadedProperties = "sc-cascaded.properties";
	
	public static final String sesServiceName1 = "session-1";
	public static final String sesServiceName2 = "session-2";
	public static final String sesServerName1 = "session-1";
	public static final String pubServiceName1 = "publish-1";
	public static final String pubServiceName2 = "publish-2";
	public static final String pubServerName1 = "publish-1";
	public static final String filServiceName1 = "file-1";
	
	public static final String killServerCmd = "kill";
	public static final String rejectSessionCmd = "reject";
	
	public static final String pangram = "The quick brown fox jumps over a lazy dog.";
	public static final String stringLength32 = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
	public static final String stringLength33 = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
	public static final String stringLength128 = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
	public static final String stringLength256 = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
			                                     "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
	public static final String stringLength257 = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
												 "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
	
	public static final int dataLength60kB = 61440;
	public static final int dataLength1MB = 1048576;
	public static final String mask =    "0000121ABCDEFGHIJKLMNO-----------X-----------";
	public static final String mask1 =   "0000635KLMNOPQRSTVWXYZ-----------X-----------";
	public static final String maskSrv = "0000121%%%%%%%%%%%%%%%-----------X-----------";
	public static final String appErrorText = "application error text";
	public static final int appErrorCode = 45234;

	public static final String logDirectoryToken = "log-path";
	public static final String configPortToken = "sc-tcp.port";
	public static final String scRunable = "sc.jar";
	public static final String serverRunable = "test-server.jar";
	public static final String clientRunable = "test-client.jar";
}
