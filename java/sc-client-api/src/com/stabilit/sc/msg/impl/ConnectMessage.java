package com.stabilit.sc.msg.impl;

import com.stabilit.sc.io.Message;
import com.stabilit.sc.io.SCMPHeaderType;
import com.stabilit.sc.io.SCMPMsgType;

public class ConnectMessage extends Message {

	public static SCMPMsgType ID = SCMPMsgType.REQ_CONNECT;

	public ConnectMessage() {
		super(ID);
	}

	public String getVersion() {
		return (String) attrMap.get(SCMPHeaderType.SCMP_VERSION.getName());
	}

	public void setVersion(String version) {
		attrMap.put(SCMPHeaderType.SCMP_VERSION.getName(), version);
	}

	public boolean isCompression() {
		return (Boolean) attrMap.get(SCMPHeaderType.COMPRESSION.getName());
	}

	public void setCompression(boolean compression) {
		attrMap.put(SCMPHeaderType.COMPRESSION.getName(), compression);
	}

	public String getLocalDateTime() {
		return (String) attrMap.get(SCMPHeaderType.LOCAL_DATE_TIME.getName());
	}

	public void setLocalDateTime(String localDateTime) {
		attrMap.put(SCMPHeaderType.LOCAL_DATE_TIME.getName(), localDateTime);
	}

	public int getKeepAliveTimeout() {
		return (Integer) attrMap.get(SCMPHeaderType.KEEP_ALIVE_TIMEOUT.getName());
	}

	public void setKeepAliveTimeout(int keepAliveTimeout) {
		attrMap.put(SCMPHeaderType.KEEP_ALIVE_TIMEOUT.getName(), keepAliveTimeout);
	}

	public int getKeepAliveInterval() {
		return (Integer) attrMap.get(SCMPHeaderType.KEEP_ALIVE_INTERVAL.getName());
	}

	public void setKeepAliveInterval(int keepAliveInterval) {
		attrMap.put(SCMPHeaderType.KEEP_ALIVE_INTERVAL.getName(), keepAliveInterval);
	}
}
