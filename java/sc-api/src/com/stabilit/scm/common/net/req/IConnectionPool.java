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
package com.stabilit.scm.common.net.req;

/**
 * @author JTraber
 */
public interface IConnectionPool {

	public abstract IConnection getConnection() throws Exception;

	public abstract void freeConnection(IConnection connection) throws Exception;

	public abstract void setMaxConnections(int maxConnections);

	public abstract void setMinConnections(int minConnections);

	public abstract void setCloseOnFree(boolean closeOnFree);

	public abstract void initMinConnections();

	public abstract void destroy();

	public abstract int getMaxConnections();

	public abstract boolean hasFreeConnections();

	public abstract void connectionIdle(IConnection connection) throws Exception;
	
	public abstract int getKeepAliveInterval();
}
