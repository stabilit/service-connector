/*-----------------------------------------------------------------------------*
 *                                                                             *
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
 *-----------------------------------------------------------------------------*/
package com.stabilit.sc.log;

import java.io.IOException;

import com.stabilit.sc.factory.IFactoryable;

public interface ILogger extends IFactoryable {

	public void log(Object obj) throws IOException;

	public void log(byte[] buffer) throws IOException;

	void log(byte[] buffer, int offset, int length) throws IOException;
	
	public void log(String msg) throws IOException;

	public void log(Throwable t) throws IOException;

	public void log(Level level, String msg) throws IOException;

}
