package org.serviceconnector;

import java.text.DecimalFormat;

import junit.framework.Assert;

import org.serviceconnector.Constants;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPHeadlineKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.scmp.SCMPMessageFault;
import org.serviceconnector.scmp.SCMPMsgType;

public class TestUtil {

	public static DecimalFormat dfMsg = new DecimalFormat(Constants.SCMP_FORMAT_OF_MSG_SIZE);
	public static DecimalFormat dfHeader = new DecimalFormat(Constants.SCMP_FORMAT_OF_HEADER_SIZE);

	public static String getSCMPString(SCMPHeadlineKey headKey, String header, String body) {
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

		String scmpString = headKey.name() + TestUtil.dfMsg.format(messageSize) + TestUtil.dfHeader.format(headerSize) + " 1.0\n"
				+ msgString;
		return scmpString;
	}

	public static void verifyError(SCMPMessage result, SCMPError error, SCMPMsgType msgType) {
		Assert.assertEquals(msgType.getValue(), result.getMessageType());
		Assert.assertEquals(error.getErrorCode(), result.getHeader(SCMPHeaderAttributeKey.SC_ERROR_CODE));
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
			if (sb.length() > Constants.MAX_MESSAGE_SIZE) {
				break;
			}
			sb.append(i);
		}
		return sb.toString();
	}

}
