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
package org.serviceconnector.web.cmd.sc;

import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;
import org.serviceconnector.web.WebUtil;
import org.serviceconnector.web.ctx.WebContext;


// TODO: Auto-generated Javadoc
/**
 * A factory for creating DefaultXMLLoader objects.
 */
public class XSLTTransformerFactory {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(XSLTTransformerFactory.class);

	/** The transformer factory. */
	protected static XSLTTransformerFactory transformerFactory = new XSLTTransformerFactory();

	/** The translet map. */
	private Map<String, Templates> transletMap = new ConcurrentHashMap<String, Templates>();

	/**
	 * Instantiates a new xSLT transformer factory.
	 */
	private XSLTTransformerFactory() {
	}

	/**
	 * Adds the translet.
	 *
	 * @param key the key
	 * @param translet the translet
	 */
	public void addTranslet(String key, Templates translet) {
		if (WebContext.getWebConfiguration().isTransletEnabled()) {
		    this.transletMap.put(key, translet);
		}
	}

	/**
	 * Clear translet.
	 */
	public void clearTranslet() {
	    this.transletMap.clear();	
	}
	
	/**
	 * Creates a new transformer instance which is threadsafe.
	 *
	 * @param xslPath the xsl path
	 * @return the transformer
	 * @throws Exception the exception
	 */
	public synchronized Transformer newTransformer(String xslPath) throws Exception {
		Templates translet = transletMap.get(xslPath);
		if (translet != null) {
			return translet.newTransformer();
		}
		// load xsl input stream for given request
		InputStream xslInputStream = WebUtil.loadResource(xslPath);
		if (xslInputStream == null) {
			return null;
		}
		TransformerFactory tFactory = TransformerFactory.newInstance();
		tFactory.setURIResolver(new XSLURIResolver());
		StreamSource xslSourceStream = new StreamSource(xslInputStream);
		translet = tFactory.newTemplates(xslSourceStream);
		this.addTranslet(xslPath, translet);
		return translet.newTransformer();
	}
	
	/**
	 * The Class XSLURIResolver.
	 */
	private class XSLURIResolver implements URIResolver {

		/*
		 * (non-Javadoc)
		 * @see javax.xml.transform.URIResolver#resolve(java.lang.String, java.lang.String)
		 */
		public Source resolve(String href, String base) throws TransformerException {
			InputStream is = WebUtil.loadResource("/org/serviceconnector/web/xsl/" + href);
			return new StreamSource(is);
		}
	}

	
	/**
	 * Gets the single instance of XSLTTransformerFactory.
	 *
	 * @return single instance of XSLTTransformerFactory
	 */
	public static XSLTTransformerFactory getInstance() {
		return transformerFactory;
	}	
	
}
