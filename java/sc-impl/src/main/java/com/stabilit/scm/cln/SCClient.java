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
package com.stabilit.scm.cln;

import java.security.InvalidParameterException;

import com.stabilit.scm.cln.service.IFileService;
import com.stabilit.scm.cln.service.IPublishService;
import com.stabilit.scm.cln.service.ISCClient;
import com.stabilit.scm.cln.service.ISessionService;
import com.stabilit.scm.common.call.SCMPAttachCall;
import com.stabilit.scm.common.call.SCMPCallFactory;
import com.stabilit.scm.common.call.SCMPDetachCall;
import com.stabilit.scm.common.conf.Constants;
import com.stabilit.scm.common.net.req.ConnectionPool;
import com.stabilit.scm.common.net.req.IConnectionPool;
import com.stabilit.scm.common.net.req.IRequester;
import com.stabilit.scm.common.net.req.Requester;
import com.stabilit.scm.common.net.req.RequesterContext;
import com.stabilit.scm.common.scmp.SCMPFault;
import com.stabilit.scm.common.scmp.SCMPMessage;
import com.stabilit.scm.common.service.ISCContext;
import com.stabilit.scm.common.service.SCServiceException;
import com.stabilit.scm.common.util.SynchronousCallback;

/**
 * The Class SCClient. Client to an SC.
 * 
 * @author JTraber
 */
public class SCClient implements ISCClient {

	/** The host of the SC. */
	private String host;
	/** The port of the SC. */
	private int port;
	/** The max connections to use in pool. */
	private int maxConnections;
	/** The keep alive interval. */
	private int keepAliveIntervalInSeconds;
	/** The connection pool. */
	private IConnectionPool connectionPool;
	/** Identifies low level component to use for communication default for clients is {netty.http}. */
	private String conType;
	/** The requester. */
	protected IRequester requester;
	/** The context. */
	private ServiceConnectorContext context;
	/** The callback. */
	private SCClientCallback callback;

	/**
	 * Instantiates a new SC client.
	 * 
	 * @param connectionType
	 *            the connection type
	 */
	public SCClient() {
		this.host = null;
		this.port = -1;
		this.conType = Constants.DEFAULT_CLIENT_CON;
		this.keepAliveIntervalInSeconds = Constants.DEFAULT_KEEP_ALIVE_INTERVAL;
		this.context = new ServiceConnectorContext();
		this.callback = null;
		this.maxConnections = Constants.DEFAULT_MAX_CONNECTIONS;
		this.connectionPool = null;
	}

	/** {@inheritDoc} */
	@Override
	public ISCContext getContext() {
		return this.context;
	}

	/** {@inheritDoc} */
	@Override
	public void attach(String host, int port) throws Exception {
		this.attach(host, port, Constants.DEFAULT_KEEP_ALIVE_INTERVAL);
	}

	/** {@inheritDoc} */
	@Override
	public void attach(String host, int port, int keepAliveIntervalInSeconds) throws Exception {
		if (this.callback != null) {
			throw new SCServiceException(
					"already attached before - detach first, attaching in sequence is not allowed.");
		}
		if (port < 1 || port > 0xFFFF) {
			throw new InvalidParameterException("Port is not within 1 and 0xFFFF.");
		}
		if (keepAliveIntervalInSeconds < 0 || keepAliveIntervalInSeconds > 3600) {
			throw new InvalidParameterException("Keep alive interval is not within 0 and 3600.");
		}
		if (host == null) {
			throw new InvalidParameterException("Host must be set.");
		}
		this.port = port;
		this.host = host;
		this.keepAliveIntervalInSeconds = keepAliveIntervalInSeconds;
		this.connectionPool = new ConnectionPool(host, port, this.conType, keepAliveIntervalInSeconds);
		this.connectionPool.setMaxConnections(this.maxConnections);
		this.requester = new Requester(new RequesterContext(this.context.getConnectionPool(), null));
		SCMPAttachCall attachCall = (SCMPAttachCall) SCMPCallFactory.ATTACH_CALL.newInstance(this.requester);
		this.callback = new SCClientCallback();
		try {
			attachCall.invoke(this.callback);
		} catch (Exception e) {
			this.callback = null;
			this.connectionPool.destroy();
			throw new SCServiceException("attach client failed", e);
		}
		SCMPMessage reply = this.callback.getMessageSync();
		if (reply.isFault()) {
			this.callback = null;
			this.connectionPool.destroy();
			SCMPFault fault = (SCMPFault) reply;
			throw new SCServiceException("attach client failed", fault.getCause());
		}
	}

	/** {@inheritDoc} */
	@Override
	public boolean isAttached() {
		return this.callback != null;
	}

	/** {@inheritDoc} */
	@Override
	public void detach() throws Exception {
		try {
			if (this.callback == null) {

				// detach not possible - client not attached just ignore
				return;
			}
			SCMPDetachCall detachCall = (SCMPDetachCall) SCMPCallFactory.DETACH_CALL.newInstance(this.requester);
			try {
				detachCall.invoke(this.callback);
			} catch (Exception e) {
				throw new SCServiceException("detach client failed", e);
			}
			SCMPMessage reply = this.callback.getMessageSync();
			if (reply.isFault()) {
				SCMPFault fault = (SCMPFault) reply;
				throw new SCServiceException("detach client failed", fault.getCause());
			}
		} finally {
			this.callback = null;
			if (this.connectionPool != null) {
				// destroy connection pool
				this.connectionPool.destroy();
			}
		}
	}

	/**
	 * Gets the connection type. Default {netty.http}
	 * 
	 * @return the connection type in use
	 */
	@Override
	public String getConnectionType() {
		return conType;
	}

	/**
	 * Sets the connection type. Should only be used if you really need to change low level technology careful.
	 * 
	 * @param conType
	 *            the new connection type, identifies low level communication technology
	 */
	public void setConnectionType(String conType) {
		this.conType = conType;
	}

	/** {@inheritDoc} */
	@Override
	public String getHost() {
		return host;
	}

	/** {@inheritDoc} */
	@Override
	public int getPort() {
		return port;
	}

	/** {@inheritDoc} */
	@Override
	public int getKeepAliveIntervalInSeconds() {
		return this.keepAliveIntervalInSeconds;
	}

	/** {@inheritDoc} */
	@Override
	public IFileService newFileService(String serviceName) throws Exception {
		if (serviceName == null) {
			throw new InvalidParameterException("Service name must be set");
		}
		if (this.callback == null) {
			throw new SCServiceException("newFileService not possible - client not attached.");
		}
		return null;
	}

	/** {@inheritDoc} */
	@Override
	public ISessionService newSessionService(String serviceName) throws Exception {
		if (serviceName == null) {
			throw new InvalidParameterException("Service name must be set");
		}
		if (this.callback == null) {
			throw new SCServiceException("newSessionService not possible - client not attached.");
		}
		return new SessionService(serviceName, this.context);
	}

	/** {@inheritDoc} */
	@Override
	public IPublishService newPublishService(String serviceName) throws Exception {
		if (serviceName == null) {
			throw new InvalidParameterException("Service name must be set");
		}
		if (this.callback == null) {
			throw new SCServiceException("newPublishService not possible - client not attached.");
		}
		return new PublishService(serviceName, this.context);
	}

	/** {@inheritDoc} */
	@Override
	public void setMaxConnections(int maxConnections) {
		if (maxConnections < 1) {
			throw new InvalidParameterException("Max connections must be greater than zero");
		}
		this.maxConnections = maxConnections;
	}

	/** {@inheritDoc} */
	@Override
	public int getMaxConnections() {
		return this.maxConnections;
	}

	/**
	 * The Class ServiceConnectorContext.
	 */
	class ServiceConnectorContext implements ISCContext {

		/** {@inheritDoc} */
		@Override
		public IConnectionPool getConnectionPool() {
			return SCClient.this.connectionPool;
		}

		/** {@inheritDoc} */
		@Override
		public ISCClient getServiceConnector() {
			return SCClient.this;
		}
	}

	/**
	 * The Class SCClientCallback.
	 */
	private class SCClientCallback extends SynchronousCallback {
		// nothing to implement in this case - everything is done by super-class
	}
}
