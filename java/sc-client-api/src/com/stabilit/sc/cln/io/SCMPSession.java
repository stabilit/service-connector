package com.stabilit.sc.cln.io;

import com.stabilit.sc.common.io.SCMP;
import com.stabilit.sc.common.io.SCMPHeaderAttributeKey;

/**
 * @author JTraber
 * 
 */
public class SCMPSession extends SCMP {

	public SCMPSession(SCMP scmp) {
		String sessionId = scmp.getSessionId();
		this.setSessionId(sessionId);
		String serviceName = scmp.getHeader(SCMPHeaderAttributeKey.SERVICE_NAME.getName());
		this.setHeader(SCMPHeaderAttributeKey.SERVICE_NAME.getName(), serviceName);		
		String msgType = scmp.getHeader(SCMPHeaderAttributeKey.MSG_TYPE.getName());
		this.setHeader(SCMPHeaderAttributeKey.MSG_TYPE.getName(), msgType);
	}

	public void addSessionRegistry() {
		String sessionId = getSessionId();
		this.setSessionId(sessionId);
		String serviceName = getHeader(SCMPHeaderAttributeKey.SERVICE_NAME.getName());
		SCMPSessionRegistry sessionRegistry = SCMPSessionRegistry.getCurrentInstance();
		sessionRegistry.add(sessionId, serviceName);
	}

	public void removeSessionRegistry() {
		String sessionId = getSessionId();
		this.setSessionId(sessionId);
		String serviceName = getHeader(SCMPHeaderAttributeKey.SERVICE_NAME.getName());
		SCMPSessionRegistry sessionRegistry = SCMPSessionRegistry.getCurrentInstance();
		sessionRegistry.remove(sessionId, serviceName);
	}

}
