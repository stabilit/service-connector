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
package org.serviceconnector.common.log;

public interface IConnectionLogger {

	public abstract void logConnect(String className, String hostName, int port);

	public abstract void logDisconnect(String className, String hostName, int port);

	public abstract void logReadBuffer(String className, String hostName, int port, byte[] data, int offset, int length);

	public abstract void logWriteBuffer(String className, String hostName, int port, byte[] data, int offset, int length);

	public abstract void logKeepAlive(String className, String hostName, int port, int nrOfIdles);
	
	public abstract boolean isDebugEnabled();
	
	public abstract boolean isInfoEnabled();
}
