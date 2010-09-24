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
package org.serviceconnector.web.cmd.sc;

import org.apache.log4j.Logger;
import org.serviceconnector.conf.CommunicatorConfig;
import org.serviceconnector.conf.SystemConfigurationException;
import org.serviceconnector.net.res.IResponder;
import org.serviceconnector.net.res.ResponderRegistry;
import org.serviceconnector.web.IWebRequest;
import org.serviceconnector.web.IWebSession;
import org.serviceconnector.web.LoginException;
import org.serviceconnector.web.cmd.IWebCommand;
import org.serviceconnector.web.cmd.IWebCommandAccessible;
import org.serviceconnector.web.cmd.IWebCommandAccessibleContext;
import org.serviceconnector.web.cmd.WebCommandFactory;

// TODO: Auto-generated Javadoc
/**
 * A factory for creating ServiceConnectorWebCommand objects. Provides access to
 * concrete instances of Service Connector Web commands.
 * 
 * @author JTraber
 */
public class ServiceConnectorWebCommandFactory extends WebCommandFactory {

	/** The Constant logger. */
	protected final static Logger logger = Logger
			.getLogger(ServiceConnectorWebCommandFactory.class);

	/**
	 * Instantiates a new service connector command factory.
	 */
	public ServiceConnectorWebCommandFactory() {
		init(this);
	}

	/**
	 * Instantiates a new service connector web command factory.
	 *
	 * @param webCommandFactory the web command factory
	 */
	public ServiceConnectorWebCommandFactory(WebCommandFactory webCommandFactory) {
		init(webCommandFactory);
	}

	/**
	 * Initialize the web command factory.
	 *
	 * @param webCommandFactory the web command factory
	 */
	private void init(WebCommandFactory webCommandFactory) {
		IWebCommand defaultWebCommand = new DefaultWebCommand();
		IWebCommandAccessible serviceConnectorWebAccessible = new ServiceConnectorWebAccessible();
		defaultWebCommand.setCommandAccessible(serviceConnectorWebAccessible);
		webCommandFactory.addWebCommand(defaultWebCommand.getKey(),
				defaultWebCommand);
	}

	/**
	 * The Class ServiceConnectorWebAccessible.
	 */
	private class ServiceConnectorWebAccessible implements
			IWebCommandAccessible {

		/** The accessible context. */
		private IWebCommandAccessibleContext accessibleContext;
		
		/**
		 * Instantiates a new service connector web accessible.
		 */
		public ServiceConnectorWebAccessible() {
			this.accessibleContext = new ServiceConnectorWebAccessibleContext();
		}
		
		/** {@inheritDoc} */
		@Override
		public void login(IWebRequest request) throws Exception {
			String userid = (String) request.getParameter("userid");
			String password = (String) request.getParameter("password");
			String contextUserid = this.getAccessibleContext().getUserid();
			String contextPassword = this.getAccessibleContext().getPassword();
			if (contextUserid == null || contextPassword == null) {
				throw new SystemConfigurationException("system configuration has no credentials");			
			}
			if (userid == null || password == null) {
				throw new LoginException("not authorized");
			}
			if (userid.equals(contextUserid) == false) {
				throw new LoginException("not authorized");			
			}
			if (password.equals(contextPassword) == false) {
				throw new LoginException("not authorized");			
			}
			IWebSession webSession = request.getSession(false);
			if (webSession == null) {
				// check if has been created before
				throw new LoginException("internal error, no session");						
			}
			webSession.setAttribute("userid", userid);
			return;
		}
		
		/** {@inheritDoc} */
		@Override
		public boolean isAccessible(IWebRequest request) throws Exception {
			String url = request.getURL();
			if (url == null) {
				return false;
			}
			if (url.startsWith("/ajax/")) {
				return true;
			}
			IWebSession webSession = request.getSession(false);
			if (webSession == null) {
				return false;
			}
			String userid = (String) webSession.getAttribute("userid");
			if (userid != null) {
				return true;
			}
			return false;
		}

		/** {@inheritDoc} */
		@Override
		public void logout(IWebRequest request) throws Exception {
			IWebSession webSession = request.getSession(false);
			if (webSession == null) {
				return;
			}
			webSession.removeAttribute("userid");
			
		}
		/** {@inheritDoc} */
		@Override
		public IWebCommandAccessibleContext getAccessibleContext() {
			return accessibleContext;
		}
		
		/**
		 * The Class ServiceConnectorWebAccessibleContext.
		 */
		private class ServiceConnectorWebAccessibleContext implements IWebCommandAccessibleContext {
			
			/**
			 * Instantiates a new service connector web accessible context.
			 */
			public ServiceConnectorWebAccessibleContext() {
			}
			
			/** {@inheritDoc} */
			@Override
			public String getUserid() {
				IResponder responder = ResponderRegistry.getCurrentInstance().getCurrentResponder();
				CommunicatorConfig respConfig = responder.getResponderConfig();
				return respConfig.getUserid();
			}

			/** {@inheritDoc} */
			@Override
			public String getPassword() {
				IResponder responder = ResponderRegistry.getCurrentInstance().getCurrentResponder();
				CommunicatorConfig respConfig = responder.getResponderConfig();
				return respConfig.getPassword();
			}
		}

	}

}
