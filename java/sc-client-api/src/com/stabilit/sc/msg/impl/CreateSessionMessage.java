package com.stabilit.sc.msg.impl;

import com.stabilit.sc.io.Message;
import com.stabilit.sc.io.SCMPHeaderType;
import com.stabilit.sc.io.SCMPMsgType;

public class CreateSessionMessage extends Message {

	public static SCMPMsgType ID = SCMPMsgType.REQ_CREATE_SESSION;

	public CreateSessionMessage() {
		super(ID);
	}

	public String getServiceName() {
		return (String) attrMap.get(SCMPHeaderType.SERVICE_NAME.getName());
	}

	public void setServiceName(String serviceName) {
		attrMap.put(SCMPHeaderType.SERVICE_NAME.getName(), serviceName);
	}

	public String getIpAddressList() {
		return (String) attrMap.get(SCMPHeaderType.IP_ADDRESS_LIST.getName());
	}

	public void setIpAddressList(String ipAddressList) {
		attrMap.put(SCMPHeaderType.IP_ADDRESS_LIST.getName(), ipAddressList);
	}

	public String getSessionInfo() {
		return (String) attrMap.get(SCMPHeaderType.SESSION_INFO.getName());
	}

	public void setSessionInfo(String sessionInfo) {
		attrMap.put(SCMPHeaderType.SESSION_INFO.getName(), sessionInfo);
	}
}
