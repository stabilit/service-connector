package com.stabilit.sc.cmd.impl;

import org.apache.log4j.Logger;

import com.stabilit.sc.common.factory.IFactoryable;
import com.stabilit.sc.common.io.IRequest;
import com.stabilit.sc.common.io.IResponse;
import com.stabilit.sc.common.io.SCMP;
import com.stabilit.sc.common.io.SCMPHeaderAttributeType;
import com.stabilit.sc.common.io.SCMPMsgType;
import com.stabilit.sc.common.io.SCMPPart;
import com.stabilit.sc.srv.cmd.CommandAdapter;
import com.stabilit.sc.srv.cmd.ICommandValidator;
import com.stabilit.sc.srv.cmd.SCMPValidatorException;
import com.stabilit.sc.srv.cmd.SCOnly;

public class EchoSCCommand extends CommandAdapter implements SCOnly {

	private static Logger log = Logger.getLogger(EchoSCCommand.class);

	public EchoSCCommand() {
		this.commandValidator = new EchoSCCommandValidator();
	}

	@Override
	public SCMPMsgType getKey() {
		return SCMPMsgType.ECHO_SC;
	}

	@Override
	public ICommandValidator getCommandValidator() {
		return super.getCommandValidator();
	}

	@Override
	public void run(IRequest request, IResponse response) throws Exception {
		log.debug("Run command " + this.getKey());
		SCMP scmp = request.getSCMP();

		SCMP scmpReply = null;
		if (scmp.isPart()) {
			String messageId = scmp.getHeader(SCMPHeaderAttributeType.SCMP_MESSAGE_ID.getName());
			String sequenceNr = scmp.getHeader(SCMPHeaderAttributeType.SEQUENCE_NR.getName());
			String offset = scmp.getHeader(SCMPHeaderAttributeType.SCMP_OFFSET.getName());			
			scmpReply = new SCMPPart();
			scmpReply.setHeader(SCMPHeaderAttributeType.SCMP_MESSAGE_ID.getName(), messageId);
			scmpReply.setHeader(SCMPHeaderAttributeType.SEQUENCE_NR.getName(), sequenceNr);
			scmpReply.setHeader(SCMPHeaderAttributeType.SCMP_OFFSET.getName(), offset);
		} else {
			scmpReply = new SCMP();
		}
		Object obj = scmp.getBody();
		scmpReply.setMessageType(getKey().getResponseName());
		scmpReply.setSessionId(scmp.getSessionId());
		scmpReply.setBody(obj);
		if (obj.toString().length() > 100) {
			System.out.println("EchoCommand not transitive body = " + obj.toString().substring(0, 100));
		} else {
			System.out.println("EchoCommand not transitive body = " + obj.toString());
		}
		response.setSCMP(scmpReply);
		return;
	}

	@Override
	public IFactoryable newInstance() {
		return this;
	}

	public class EchoSCCommandValidator implements ICommandValidator {

		@Override
		public void validate(IRequest request, IResponse response) throws SCMPValidatorException {
		}
	}

}
