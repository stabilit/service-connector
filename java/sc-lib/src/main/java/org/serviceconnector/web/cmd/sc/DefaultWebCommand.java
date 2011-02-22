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

import org.apache.log4j.Logger;
import org.jboss.netty.handler.codec.http.Cookie;
import org.jboss.netty.handler.codec.http.DefaultCookie;
import org.serviceconnector.web.IWebRequest;
import org.serviceconnector.web.IWebResponse;
import org.serviceconnector.web.IWebSession;
import org.serviceconnector.web.IXMLLoader;
import org.serviceconnector.web.LoginException;
import org.serviceconnector.web.NotFoundException;
import org.serviceconnector.web.WebUtil;
import org.serviceconnector.web.cmd.IWebCommandAccessibleContext;
import org.serviceconnector.web.ctx.WebContext;


/**
 * The Class DefaultWebCommand. Responsible for validation and execution of any pure http web command. This Class uses a xml based
 * model and the view is built using xsl transformation.
 * 
 * @author JTraber
 */
public class DefaultWebCommand extends WebCommandAdapter {

	/** The Constant logger. */
	private final static Logger logger = Logger.getLogger(DefaultWebCommand.class);

	/**
	 * Instantiates a new default web command.
	 */
	public DefaultWebCommand() {
	}

	/** {@inheritDoc} */
	@Override
	public String getKey() {
		return "default";
	}

	/** {@inheritDoc} */
	@Override
	public void run(IWebRequest request, IWebResponse response) throws Exception {
		// check if session is available
		String url = request.getURL();
		if (url != null) {
			//logger.debug(url);
		} else {
			logger.warn("url is null");
		}
		OutputStream responseOutputStream = response.getOutputStream();
		if (isResource(url)) {
			String resourcePath = getResourcePath(url);
			InputStream is = WebUtil.loadResource(resourcePath);
			if (is == null) {
				throw new NotFoundException(url);
			}
			dumpStream(is, responseOutputStream);
			return;
		}
		IWebSession webSession = request.getSession(false);
		if (webSession == null) {
			Cookie jsessionidCookie = request.getCookie("JSESSIONID");
			if (jsessionidCookie != null) {
				jsessionidCookie.setMaxAge(0);
				response.addCookie(jsessionidCookie);			
			}
		}
		// load xml model as stream
		ByteArrayOutputStream xmlOS = new ByteArrayOutputStream();
		XMLDocument xmlDocument = new XMLDocument(request);
		if (this.isLoginAction(request)) {
			try {
				this.webCommandAccessible.login(request, response);
				response.redirect("/");
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
				this.webCommandAccessible.logout(request);
				response.redirect("/");
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
		if (this.webCommandAccessible.isAccessible(request)) {
			IWebCommandAccessibleContext accessibleContext = this.webCommandAccessible.getAccessibleContext();
			xmlDocument.setAccessibleContext(accessibleContext);
			xslDocument.setAccessibleContext(accessibleContext);
		}
		try {
			this.webCommandValidator.validate(request);
		} catch (Exception e) {
			xmlDocument.addException(e);
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
		byte[] buffer = new byte[1 << 16];
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

		/** The accessible context. */
		private IWebCommandAccessibleContext accessibleContext;

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
			this.accessibleContext = null;
			this.loader = WebContext.getXMLLoader(this.request.getURL());
		}

		public boolean isText() {
			return this.loader.isText();
		}

		/**
		 * Sets the accessible context.
		 * 
		 * @param accessibleContext
		 *            the new accessible context
		 */
		public void setAccessibleContext(IWebCommandAccessibleContext accessibleContext) {
			this.accessibleContext = accessibleContext;
		}

		/**
		 * Adds the exception which will be render into xml meta tag
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
			if (this.accessibleContext != null) {
				String userid = this.accessibleContext.getUserid();
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

		/** The accessible context. */
		private IWebCommandAccessibleContext accessibleContext;

		/**
		 * Instantiates a new xSL document.
		 * 
		 * @param request
		 *            the request
		 */
		public XSLDocument(IWebRequest request) {
			this.request = request;
			this.accessibleContext = null;
		}

		/**
		 * Sets the accessible context.
		 * 
		 * @param accessibleContext
		 *            the new accessible context
		 */
		public void setAccessibleContext(IWebCommandAccessibleContext accessibleContext) {
			this.accessibleContext = accessibleContext;
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
			if (this.accessibleContext == null) {
				return rootPath + "login.xsl";
			}
			String[] splitted = url.split("\\?");
			if (splitted.length <= 0) {
				return rootPath + "main.xsl";
			}
			if (splitted[0].startsWith("/")) {
				String name = splitted[0].substring(1);
				return rootPath + name + ".xsl";
			}
			return rootPath + "main.xsl";
		}

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
				//logger.debug("transform using xslt " + xslPath);
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

}