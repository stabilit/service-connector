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
package org.serviceconnector.web.cmd;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.transform.Transformer;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import net.sf.ehcache.config.InvalidConfigurationException;

import org.apache.log4j.Logger;
import org.serviceconnector.Constants;
import org.serviceconnector.web.IWebRequest;
import org.serviceconnector.web.IWebResponse;
import org.serviceconnector.web.LoginException;
import org.serviceconnector.web.NotFoundException;
import org.serviceconnector.web.WebCredentials;
import org.serviceconnector.web.WebSession;
import org.serviceconnector.web.WebUtil;
import org.serviceconnector.web.ctx.WebContext;
import org.serviceconnector.web.xml.IXMLLoader;

/**
 * The Class WebCommand. Responsible for validation and execution of any pure http web command. This Class uses a xml based model
 * and the view is built using
 * xsl transformation.
 * 
 * @author JTraber
 */
public class WebCommand {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = Logger.getLogger(WebCommand.class);

	public void run(IWebRequest request, IWebResponse response) throws Exception {
		// check if session is available
		String url = request.getURL();
		if (url != null) {
			LOGGER.trace(url);
		} else {
			LOGGER.warn("url is null");
		}
		OutputStream responseOutputStream = response.getOutputStream();
		if (isResource(url)) {
			String resourcePath = getResourcePath(url);
			InputStream is = WebUtil.loadResource(resourcePath);
			if (is == null) {
				throw new NotFoundException(url);
			}
			response.setContentType(getResourceType(url));
			dumpStream(is, responseOutputStream);
			return;
		}
		WebSession webSession = request.getSession(false);
		// load xml model as stream
		ByteArrayOutputStream xmlOS = new ByteArrayOutputStream();
		XMLDocument xmlDocument = new XMLDocument(request);
		if (this.isLoginAction(request)) {
			try {
				webSession = this.login(request, response);
				if (webSession != null) {
					response.redirect("/;sid=" + webSession.getId());
				} else {
					response.redirect("/;");
				}
				return;
			} catch (LoginException e) {
				xmlDocument.addException(e);
				xmlDocument.addErrorMessage("not authorized");
			} catch (Exception e) {
				xmlDocument.addException(e);
				xmlDocument.addErrorMessage(e.getMessage());
			}
		}
		if (this.isLogoutAction(request)) {
			try {
				this.logout(request);
				response.redirect(Constants.SLASH);
				return;
			} catch (Exception e) {
				xmlDocument.addException(e);
				xmlDocument.addErrorMessage("not authorized");
			}
		}
		response.addHeader("Pragma", "no-cache");
		response.addHeader("Expires", "-1");
		response.addHeader("Cache-Control", "no-store, no-cache, must-revalidate");

		// load stylesheet as stream
		XSLDocument xslDocument = new XSLDocument(request);
		// check if this web command is accessible
		if (this.isAccessible(request)) {
			xmlDocument.setCredentials(WebContext.getWebSCContextCredentials());
			xslDocument.setCredentials(WebContext.getWebSCContextCredentials());
		}
		if (xmlDocument.isText()) {
			xmlDocument.load(responseOutputStream);
			response.setContentType("text/xml");
			return;
		}
		xmlDocument.load(xmlOS);
		// check if xmlview is yes or true
		if (isXMLView(request)) {
			dumpStream(new ByteArrayInputStream(xmlOS.toByteArray()), responseOutputStream);
			response.setContentType("text/xml");
			return;
		}
		// transform
		xslDocument.transform(new ByteArrayInputStream(xmlOS.toByteArray()), responseOutputStream);
		response.setContentType("text/html");
	}

	/**
	 * Checks if is xML view.
	 * 
	 * @param request
	 *            the request
	 * @return true, if is xML view
	 */
	private boolean isXMLView(IWebRequest request) {
		String xmlView = request.getParameter("xmlview");
		return "yes".equals(xmlView);
	}

	/**
	 * Checks if is login action.
	 * 
	 * @param request
	 *            the request
	 * @return true, if is login action
	 */
	private boolean isLoginAction(IWebRequest request) {
		String action = request.getParameter("action");
		return "login".equals(action);
	}

	/**
	 * Checks if is logout action.
	 * 
	 * @param request
	 *            the request
	 * @return true, if is logout action
	 */
	private boolean isLogoutAction(IWebRequest request) {
		String action = request.getParameter("action");
		return "logout".equals(action);
	}

	/**
	 * Dump stream.
	 * 
	 * @param is
	 *            the is
	 * @param os
	 *            the os
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private void dumpStream(InputStream is, OutputStream os) throws IOException {
		byte[] buffer = new byte[Constants.SIZE_64KB];
		int readBytes = -1;
		while ((readBytes = is.read(buffer)) > 0) {
			os.write(buffer, 0, readBytes);
		}
		return;
	}

	/**
	 * Checks if is resource.
	 * 
	 * @param url
	 *            the url
	 * @return true, if is resource
	 */
	private boolean isResource(String url) {
		return isCSS(url) || isScript(url) || isImage(url);
	}

	/**
	 * Gets the resource path.
	 * 
	 * @param url
	 *            the url
	 * @return the resource path
	 */
	public String getResourcePath(String url) {
		String path = "/org/serviceconnector/web";
		if (isCSS(url)) {
			return path + "/css" + url;
		}
		if (isImage(url)) {
			return path + "/images" + url;
		}
		if (isScript(url)) {
			return path + "/js" + url;
		}
		return null;
	}

	/**
	 * Gets the resource type
	 * 
	 * @param url
	 *            the url
	 * @return the resource type
	 */
	public String getResourceType(String url) {
		if (isCSS(url)) {
			return "text/css";
		}
		if (isImage(url)) {
			if (url.endsWith(".png")) {
				return "image/png";
			}
			if (url.endsWith(".jpg")) {
				return "image/jpg";
			}
			if (url.endsWith(".gif")) {
				return "image/gif";
			}
			return "image/*";
		}
		if (isScript(url)) {
			return "text/javascript";
		}
		return null;
	}

	/**
	 * Checks if is cSS.
	 * 
	 * @param url
	 *            the url
	 * @return true, if is cSS
	 */
	private boolean isCSS(String url) {
		if (url == null) {
			return false;
		}
		return url.endsWith(".css");
	}

	/**
	 * Checks if is script.
	 * 
	 * @param url
	 *            the url
	 * @return true, if is script
	 */
	private boolean isScript(String url) {
		if (url == null) {
			return false;
		}
		return url.endsWith(".js");
	}

	/**
	 * Checks if is image.
	 * 
	 * @param url
	 *            the url
	 * @return true, if is image
	 */
	private boolean isImage(String url) {
		if (url == null) {
			return false;
		}
		return url.endsWith(".png") || url.endsWith(".jpg") || url.endsWith(".gif");
	}

	/**
	 * The Class XMLDocument.
	 */
	private class XMLDocument {

		/** The request. */
		private IWebRequest request;

		/** The loader. */
		private IXMLLoader loader;

		/** The exception list. */
		private List<Exception> exceptionList;

		/** The message list. */
		private List<Message> messageList;
		private WebCredentials credentials;

		/**
		 * Instantiates a new xML document.
		 * 
		 * @param request
		 *            the request
		 */
		public XMLDocument(IWebRequest request) {
			this.request = request;
			this.exceptionList = new ArrayList<Exception>();
			this.messageList = new ArrayList<Message>();
			this.credentials = null;
			this.loader = WebContext.getXMLLoader(this.request.getURL());
		}

		/**
		 * Checks if is text.
		 * 
		 * @return true, if is text
		 */
		public boolean isText() {
			return this.loader.isText();
		}

		/**
		 * Sets the credentials.
		 * 
		 * @param credentials
		 *            the new credentials
		 */
		public void setCredentials(WebCredentials credentials) {
			this.credentials = credentials;
		}

		/**
		 * Adds the exception which will be render into xml meta tag.
		 * 
		 * @param ex
		 *            the ex
		 */
		public void addException(Exception ex) {
			this.exceptionList.add(ex);
		}

		/**
		 * Adds the message.
		 * 
		 * @param msg
		 *            the msg
		 */
		@SuppressWarnings("unused")
		public void addMessage(String msg) {
			this.messageList.add(new Message(msg, "info"));
		}

		/**
		 * Adds the error message.
		 * 
		 * @param msg
		 *            the msg
		 */
		public void addErrorMessage(String msg) {
			this.messageList.add(new Message(msg, "error"));
		}

		/**
		 * Load.
		 * 
		 * @param os
		 *            the os
		 * @throws Exception
		 *             the exception
		 */
		public void load(OutputStream os) throws Exception {
			for (Exception e : exceptionList) {
				this.loader.addMeta("exception", e.toString());
			}
			for (Message msg : messageList) {
				this.loader.addMeta(msg.getMap());
			}
			if (this.credentials != null) {
				String userid = this.credentials.getUserId();
				if (userid != null) {
					this.loader.addMeta("userid", userid);
				}
			}
			this.loader.load(request, os);
		}

		/**
		 * The Class Message.
		 */
		private class Message {

			/** The map. */
			private Map<String, String> map;

			/**
			 * Instantiates a new message.
			 * 
			 * @param text
			 *            the text
			 * @param type
			 *            the type
			 */
			public Message(String text, String type) {
				this.map = new HashMap<String, String>();
				this.map.put("message", text);
				this.map.put("type", type);
			}

			/**
			 * Gets the map.
			 * 
			 * @return the map
			 */
			public Map<String, String> getMap() {
				return map;
			}
		}

	}

	/**
	 * The Class XSLDocument.
	 */
	private class XSLDocument {

		/** The request. */
		private IWebRequest request;
		/** The credentials. */
		private WebCredentials credentials;

		/**
		 * Instantiates a new xSL document.
		 * 
		 * @param request
		 *            the request
		 */
		public XSLDocument(IWebRequest request) {
			this.request = request;
			this.credentials = null;
		}

		/**
		 * Sets the credentials.
		 * 
		 * @param credentials
		 *            the new credentials
		 */
		public void setCredentials(WebCredentials credentials) {
			this.credentials = credentials;
		}

		/**
		 * Gets the xSL path.
		 * 
		 * @param url
		 *            the url
		 * @return the xSL path
		 */
		private String getXSLPath(String url) {
			if (url == null) {
				url = this.request.getURL();
			}
			String rootPath = "/org/serviceconnector/web/xsl/";
			String rootAjaxPath = "/org/serviceconnector/web/xsl/ajax/";
			if (this.isAjax(url)) {
				String id = request.getParameter("id");
				if (id != null) {
					return rootAjaxPath + id + ".xsl";
				}
			}
			if (this.credentials == null) {
				return rootPath + "login.xsl";
			}
			String[] splitted = url.split("\\?");
			if (splitted.length <= 0) {
				return rootPath + "main.xsl";
			}
			if (splitted[0].startsWith(Constants.SLASH)) {
				String name = splitted[0].substring(1);
				return rootPath + name + ".xsl";
			}
			return rootPath + "main.xsl";
		}

		/**
		 * Checks if is ajax.
		 * 
		 * @param url
		 *            the url
		 * @return true, if is ajax
		 */
		private boolean isAjax(String url) {
			if (url == null) {
				return false;
			}
			return url.startsWith("/ajax/");
		}

		/**
		 * Transform.
		 * 
		 * @param xmlInputStream
		 *            the xml input stream
		 * @param resultOutputStream
		 *            the result output stream
		 * @throws Exception
		 *             the exception
		 */
		public void transform(InputStream xmlInputStream, OutputStream resultOutputStream) throws Exception {
			String xslPath = this.getXSLPath(null);
			Transformer transformer = XSLTTransformerFactory.getInstance().newTransformer(xslPath);
			if (transformer == null) {
				transformer = XSLTTransformerFactory.getInstance().newTransformer(this.getXSLPath(""));
			} else {
				LOGGER.trace("transform using xslt " + xslPath);
			}
			if (transformer == null) {
				throw new NotFoundException("xslt resource " + xslPath + " not found");
			}
			StreamSource xmlSourceStream = new StreamSource(xmlInputStream);
			StreamResult resultStream = new StreamResult(resultOutputStream);
			transformer.transform(xmlSourceStream, resultStream);
			transformer = null;
		}
	}

	public WebSession login(IWebRequest request, IWebResponse response) throws Exception {
		String userid = request.getParameter("userid");
		String password = request.getParameter("password");
		String contextUserid = WebContext.getWebSCContextCredentials().getUserId();
		String contextPassword = WebContext.getWebSCContextCredentials().getPassword();
		if (contextUserid == null || contextPassword == null) {
			throw new InvalidConfigurationException("system configuration has no credentials");
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
		WebSession webSession = request.getSession(true);
		if (webSession == null) {
			// check if has been created before
			throw new LoginException("internal error, no session");
		}
		webSession.setCredentials(new WebCredentials(contextUserid, contextPassword));
		webSession.setUserAgent(request.getHeader("User-Agent"));
		webSession.setRemoteHost(request.getRemoteHost());
		webSession.setRemotePort(request.getRemotePort());
		webSession.setHost(request.getHost());
		webSession.setPort(request.getPort());
		request.setAttribute("JSESSIONID", webSession.getId());
		return webSession;
	}

	public void logout(IWebRequest request) throws Exception {
		WebSession webSession = request.getSession(false);
		if (webSession == null) {
			return;
		}
		webSession.getCredentials().clear();
		WebContext.getWebSessionRegistry().removeSession(webSession);
	}

	public boolean isAccessible(IWebRequest request) throws Exception {
		String url = request.getURL();
		if (url == null) {
			return false;
		}
		if (url.startsWith("/ajax/")) {
			return true;
		}
		WebSession webSession = request.getSession(false);
		if (webSession == null) {
			return false;
		}
		String userid = webSession.getCredentials().getUserId();
		if (userid != null) {
			return true;
		}
		return false;
	}

}