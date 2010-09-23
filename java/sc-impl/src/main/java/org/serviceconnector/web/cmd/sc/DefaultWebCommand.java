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

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;
import org.serviceconnector.SCVersion;
import org.serviceconnector.util.DateTimeUtility;
import org.serviceconnector.web.IWebRequest;
import org.serviceconnector.web.IWebResponse;
import org.serviceconnector.web.IXMLLoader;

// TODO: Auto-generated Javadoc
/**
 * The Class DefaultWebCommand. Responsible for validation and execution of any
 * pure http web command. This Class uses a xml based model and the view is
 * built using xsl transformation.
 * 
 * @author JTraber
 */
public class DefaultWebCommand extends WebCommandAdapter {

	/** The Constant logger. */
	protected final static Logger logger = Logger
			.getLogger(DefaultWebCommand.class);

	/**
	 * Instantiates a new default web command.
	 */
	public DefaultWebCommand() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.serviceconnector.web.cmd.sc.WebCommandAdapter#getKey()
	 */
	@Override
	public String getKey() {
		return "default";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.serviceconnector.web.cmd.sc.WebCommandAdapter#run(org.serviceconnector
	 * .web.IWebRequest, org.serviceconnector.web.IWebResponse)
	 */
	@Override
	public void run(IWebRequest request, IWebResponse response)
			throws Exception {
		String url = request.getURL();
		OutputStream responseOutputStream = response.getOutputStream();
		if (isResource(url)) {
			String resourcePath = getResourcePath(url);
			InputStream is = this.getClass().getResourceAsStream(resourcePath);
			dumpStream(is, responseOutputStream);
			return;
		}
		// load xml model as stream
		ByteArrayOutputStream xmlOS = new ByteArrayOutputStream();
		XMLDocument xmlDocument = new XMLDocument(request);
		xmlDocument.load(xmlOS);
		// check if xmlview is yes or true
		if (isXMLView(request)) {
			dumpStream(new ByteArrayInputStream(xmlOS.toByteArray()),
					responseOutputStream);
			response.setContentType("text/xml");
			return;
		}
		// load stylesheet as stream
		XSLDocument xslDocument = new XSLDocument(request);
		// transform
		xslDocument.transform(new ByteArrayInputStream(xmlOS.toByteArray()),
				responseOutputStream);
		response.setContentType("text/html");
	}

	private boolean isXMLView(IWebRequest request) {
        String xmlView = request.getParameter("xmlview");
        return "yes".equals(xmlView); 
	}

	private void dumpStream(InputStream is, OutputStream os) throws IOException {
		byte[] buffer = new byte[1 << 16];
		int readBytes = -1;
		while ((readBytes = is.read(buffer)) > 0) {
			os.write(buffer, 0, readBytes);
		}
		return;
	}

	private boolean isResource(String url) {
	   return isCSS(url) || isScript(url) || isImage(url);	
	}
	
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
	
	private boolean isCSS(String url) {
		if (url == null) {
			return false;
		}
		return url.endsWith(".css");
	}

	private boolean isScript(String url) {
		if (url == null) {
			return false;
		}
		return url.endsWith(".js");
	}

	private boolean isImage(String url) {
		if (url == null) {
			return false;
		}
		return url.endsWith(".png") || url.endsWith(".jpg");
	}

	/**
	 * The Class XMLDocument.
	 */
	private class XMLDocument {

		/** The request. */
		private IWebRequest request;
		private IXMLLoader loader;

		/**
		 * Instantiates a new xML document.
		 * 
		 * @param request
		 *            the request
		 */
		public XMLDocument(IWebRequest request) {
			this.request = request;
			this.loader = DefaultXMLLoaderFactory.getXMLLoader(this.request.getURL());
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
			this.loader.load(request, os);
		}
		
	}

	/**
	 * The Class XSLDocument.
	 */
	private class XSLDocument {

		/** The request. */
		private IWebRequest request;

		/** The xsl input stream. */
		private InputStream xslInputStream;

		/**
		 * Instantiates a new xSL document.
		 * 
		 * @param request
		 *            the request
		 */
		public XSLDocument(IWebRequest request) {
			this.request = request;
			String url = this.request.getURL();
			String xslPath = this.getXSLPath(url);
			// load xsl input stream for given request
			xslInputStream = this.getClass().getResourceAsStream(xslPath);
			if (xslInputStream == null) {
				xslPath = this.getXSLPath("");
				xslInputStream = this.getClass().getResourceAsStream(xslPath);			
			}
		}

		private String getXSLPath(String url) {
			String rootPath = "/org/serviceconnector/web/xsl/";
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
		public void transform(InputStream xmlInputStream,
				OutputStream resultOutputStream) throws Exception {
			TransformerFactory tFactory = TransformerFactory.newInstance();
			tFactory.setURIResolver(new XSLURIResolver());
			StreamSource xslSourceStream = new StreamSource(xslInputStream);
			StreamSource xmlSourceStream = new StreamSource(xmlInputStream);
			StreamResult resultStream = new StreamResult(resultOutputStream);
			Transformer transformer = tFactory.newTransformer(xslSourceStream);
			transformer.transform(xmlSourceStream, resultStream);
		}

		private class XSLURIResolver implements URIResolver {
			public Source resolve(String href, String base)
					throws TransformerException {
				InputStream is = getClass().getResourceAsStream(
						"/org/serviceconnector/web/xsl/" + href);
				return new StreamSource(is);
			}
		}
	}

}