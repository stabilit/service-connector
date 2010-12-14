package org.serviceconnector.test;

import java.text.DecimalFormat;

import org.serviceconnector.Constants;
import org.serviceconnector.scmp.SCMPHeadlineKey;

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

}
