package org.serviceconnector;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.Assert;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.EnvironmentConfiguration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.SystemConfiguration;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPHeaderKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.scmp.SCMPMessageFault;
import org.serviceconnector.scmp.SCMPMsgType;
import org.serviceconnector.scmp.SCMPVersion;

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

		String scmpString = headKey.name() + TestUtil.dfMsg.format(messageSize) + TestUtil.dfHeader.format(headerSize) + " "
				+ SCMPVersion.CURRENT + "\n" + msgString;
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

	public static void checkLogFile(String log4jProperties, String logFileToCheck) throws Exception {
		CompositeConfiguration compositeConfig = new CompositeConfiguration();
		compositeConfig.addConfiguration(new EnvironmentConfiguration());
		compositeConfig.addConfiguration(new PropertiesConfiguration(log4jProperties));
		compositeConfig.addConfiguration(new SystemConfiguration());
		// Read & parse properties file.
		String logDirPath = userDir + fs + compositeConfig.getString(TestConstants.logDirectoryToken);

		File fileToCheck = new File(logDirPath + fs + logFileToCheck);
		BufferedReader br = new BufferedReader(new FileReader(fileToCheck));
		String strLine;

		while ((strLine = br.readLine()) != null) {
			Matcher mEXC = PAT_EXC.matcher(strLine);
			if (mEXC.matches()) {
				throw new Exception("EXC found in " + logFileToCheck + "\n" + strLine);
			}
			Matcher mERROR = PAT_ERROR.matcher(strLine);
			if (mERROR.matches()) {
				throw new Exception("ERROR found in " + logFileToCheck + "\n" + strLine);
			}
		}
	}

	public static void deleteLogDir(String log4jProperties) throws ConfigurationException {
		CompositeConfiguration compositeConfig = new CompositeConfiguration();
		compositeConfig.addConfiguration(new EnvironmentConfiguration());
		compositeConfig.addConfiguration(new PropertiesConfiguration(log4jProperties));
		compositeConfig.addConfiguration(new SystemConfiguration());
		// Read & parse properties file.
		String logDirPath = userDir + fs + compositeConfig.getString(TestConstants.logDirectoryToken);

		File dirToDelete = new File(logDirPath);
		TestUtil.deleteDir(dirToDelete);
	}

	public static boolean deleteDir(File dir) {
		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDir(new File(dir, children[i]));
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
}
