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
package org.serviceconnector.web;

import java.io.FileInputStream;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;
import org.serviceconnector.web.cmd.sc.DefaultXMLLoaderFactory;

// TODO: Auto-generated Javadoc
/**
 * The Class WebUtil.
 */
public abstract class WebUtil {

	/** The Constant XMLSDF. */
	public static final SimpleDateFormat XMLSDF = new SimpleDateFormat("yyyy-MM-dd");

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(DefaultXMLLoaderFactory.class);

	/**
	 * Load resource.
	 * 
	 * @param name
	 *            the name
	 * @return the input stream
	 */
	public static InputStream loadResource(String name) {
		if (name == null) {
			return null;
		}
		try {
			InputStream is = ClassLoader.getSystemResourceAsStream(name);
			if (is != null) {
				return is;
			}
			is = WebUtil.class.getResourceAsStream(name);
			if (is != null) {
				return is;
			}
			is = new FileInputStream(name);
			if (is != null) {
				return is;
			}
			if (name.startsWith("/resources")) {
				return null;
			}
			return loadResource("/resources" + name);
		} catch (Exception e) {
			if (name.startsWith("/resources")) {
				return null;
			}
			return loadResource("/resources" + name);
		}
	}

	/**
	 * Gets the xML date as string.
	 * 
	 * @param date
	 *            the date
	 * @return the xML date as string
	 */
	public static String getXMLDateAsString(Date date) {
		synchronized (XMLSDF) { // XMLSDF is not thread safe
			return XMLSDF.format(date);
		}
	}

	/**
	 * Gets the xML date from string.
	 * 
	 * @param date
	 *            the date
	 * @return the xML date from string
	 */
	public static Date getXMLDateFromString(String date) {
		synchronized (XMLSDF) { // XMLSDF is not thread safe
			try {
				return XMLSDF.parse(date);
			} catch (ParseException e) {
				return new Date();
			}
		}
	}

	/**
	 * Gets the xML next date as string.
	 * 
	 * @param date
	 *            the date
	 * @return the xML next date as string
	 */
	public static String getXMLNextDateAsString(Date date) {
		Calendar c = Calendar.getInstance();
		return getXMLDateAsString(new Date(date.getYear(), date.getMonth(), date.getDate() + 1));
	}

	/**
	 * Gets the xML previous date as string.
	 * 
	 * @param date
	 *            the date
	 * @return the xML previous date as string
	 */
	public static String getXMLPreviousDateAsString(Date date) {
		Calendar c = Calendar.getInstance();
		return getXMLDateAsString(new Date(date.getYear(), date.getMonth(), date.getDate() - 1));
	}
}
