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
package org.serviceconnector;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.EnvironmentConfiguration;
import org.apache.commons.configuration.SystemConfiguration;
import org.serviceconnector.net.SCMPHeaderKey;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.scmp.SCMPMessageFault;
import org.serviceconnector.scmp.SCMPMsgType;
import org.serviceconnector.scmp.SCMPVersion;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import junit.framework.Assert;

public class TestUtil {
	private static final String EXC_REGEX = ".*<-EXC.*";
	private static final Pattern PAT_EXC = Pattern.compile(EXC_REGEX);
	private static final String ERROR_REGEX = ".*ERROR.*";
	private static final Pattern PAT_ERROR = Pattern.compile(ERROR_REGEX);
	public static DecimalFormat dfMsg = new DecimalFormat(Constants.SCMP_FORMAT_OF_MSG_SIZE);
	public static DecimalFormat dfHeader = new DecimalFormat(Constants.SCMP_FORMAT_OF_HEADER_SIZE);
	public static String fs = System.getProperty("file.separator");
	public static String userDir = System.getProperty("user.dir");

	public static String getSCMPString(SCMPHeaderKey headKey, String header, String body) {
		int headerSize = 0;
		int bodySize = 0;
		String msgString = "";

		if (header != null) {
			headerSize = header.length();
			msgString += header;
		}
		if (body != null) {
			bodySize = body.length();
			msgString += body;
		}
		int messageSize = headerSize + bodySize;

		String scmpString = headKey.name() + TestUtil.dfMsg.format(messageSize) + TestUtil.dfHeader.format(headerSize) + " " + SCMPVersion.CURRENT + "\n" + msgString;
		return scmpString;
	}

	public static void verifyError(SCMPMessage result, SCMPError error, SCMPMsgType msgType) {
		Assert.assertEquals(msgType.getValue(), result.getMessageType());
		Assert.assertEquals(error.getErrorCode(), result.getHeaderInt(SCMPHeaderAttributeKey.SC_ERROR_CODE).intValue());
	}

	public static void checkReply(SCMPMessage message) throws Exception {
		if (message.isFault()) {
			SCMPMessageFault fault = (SCMPMessageFault) message;
			Exception ex = fault.getCause();
			if (ex != null) {
				throw ex;
			}
			throw new Exception(fault.getHeader(SCMPHeaderAttributeKey.SC_ERROR_TEXT));
		}
	}

	public static String getLargeString() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < Integer.MAX_VALUE; i++) {
			if (sb.length() > Constants.DEFAULT_MESSAGE_PART_SIZE) {
				break;
			}
			sb.append(i);
		}
		return sb.toString();
	}

	public static String get10MBString() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < Integer.MAX_VALUE; i++) {
			if (sb.length() > 10485760) {
				break;
			}
			sb.append(i);
		}
		return sb.toString();
	}

	public static String get50MBString() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < Integer.MAX_VALUE; i++) {
			if (sb.length() > 52428800) {
				break;
			}
			sb.append(i);
		}
		return sb.toString();
	}

	public static void checkLogFile(String logbackFileName, String logFileToCheck) throws Exception {
		CompositeConfiguration compositeConfig = new CompositeConfiguration();
		compositeConfig.addConfiguration(new EnvironmentConfiguration());
		// read the logPath from logback configurationFile file
		Configuration logbackConfig = readPropertiesFromLogbackXml(logbackFileName);
		compositeConfig.addConfiguration(logbackConfig);
		compositeConfig.addConfiguration(new SystemConfiguration());
		// retrieve the logDir property
		String logDirPath = userDir + fs + compositeConfig.getString(TestConstants.logDirectoryToken);

		File fileToCheck = new File(logDirPath + fs + logFileToCheck);
		try (FileReader fr = new FileReader(fileToCheck); BufferedReader br = new BufferedReader(fr)) {
			String strLine;

			while ((strLine = br.readLine()) != null) {
				Matcher mEXC = PAT_EXC.matcher(strLine);
				if (mEXC.matches()) {
					throw new Exception("EXC found in " + fileToCheck.getPath() + "\n" + strLine);
				}
				Matcher mERROR = PAT_ERROR.matcher(strLine);
				if (mERROR.matches()) {
					throw new Exception("ERROR found in " + fileToCheck.getPath() + "\n" + strLine);
				}
			}
		}
	}

	public static void deleteLogDir(String logbackFileName) throws Exception {
		CompositeConfiguration compositeConfig = new CompositeConfiguration();
		compositeConfig.addConfiguration(new EnvironmentConfiguration());
		// read the logPath from logback configurationFile file
		Configuration logbackConfig = readPropertiesFromLogbackXml(logbackFileName);
		compositeConfig.addConfiguration(logbackConfig);
		compositeConfig.addConfiguration(new SystemConfiguration());
		// Read & parse properties file.
		String logDirPath = userDir + fs + compositeConfig.getString(TestConstants.logDirectoryToken);

		File dirToDelete = new File(logDirPath);
		TestUtil.deleteDir(dirToDelete);
	}

	public static boolean deleteDir(File dir) {
		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (String element : children) {
				boolean success = deleteDir(new File(dir, element));
				if (!success) {
					return false;
				}
			}
		}
		// The directory is now empty so delete it
		return dir.delete();
	}

	public static void create200MBFile(String filePath) throws Exception {
		File file = new File(filePath);
		if (file.exists() == false) {
			file.createNewFile();
		}
		byte[] garbage = TestUtil.get10MBString().getBytes();
		FileOutputStream fout = new FileOutputStream(filePath);
		OutputStream out = new BufferedOutputStream(fout);
		for (int i = 0; i < 20; i++) {
			out.write(garbage);
			out.flush();
		}
		out.close();
	}

	private static Configuration readPropertiesFromLogbackXml(String logbackFileName)
			throws FileNotFoundException, UnsupportedEncodingException, XPathExpressionException, Exception {
		// read the logback XML file
		InputStream inputStream = new FileInputStream(new File("src/main/resources/", logbackFileName));
		InputStreamReader inputReader = new InputStreamReader(inputStream, "UTF-8");
		InputSource inputSource = new InputSource(inputReader);
		inputSource.setEncoding("UTF-8");
		// XPath query to retrieve all property elements
		String xpath = "/configuration/property";
		XPath xPath = XPathFactory.newInstance().newXPath();
		NodeList propertyNodes = (NodeList) xPath.evaluate(xpath, inputSource, XPathConstants.NODESET);
		int length = propertyNodes.getLength();
		if (length == 0) {
			throw new Exception("No properties defined in the file " + logbackFileName);
		}
		// go through all the properties from the logback file and add them to the configuration
		Configuration configuration = new BaseConfiguration();
		for (int i = 0; i < length; i++) {
			Node item = propertyNodes.item(i);
			String key = item.getAttributes().getNamedItem("name").getNodeValue();
			String value = item.getAttributes().getNamedItem("value").getNodeValue();
			configuration.addProperty(key, value);
		}
		return configuration;
	}

}
