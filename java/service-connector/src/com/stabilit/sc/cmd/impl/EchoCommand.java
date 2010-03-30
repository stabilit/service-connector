package com.stabilit.sc.cmd.impl;

import com.stabilit.sc.cln.msg.impl.EchoMessage;
import com.stabilit.sc.cmd.CommandAdapter;
import com.stabilit.sc.cmd.CommandException;
import com.stabilit.sc.cmd.ICommandValidator;
import com.stabilit.sc.cmd.SCMPValidatorException;
import com.stabilit.sc.common.factory.IFactoryable;
import com.stabilit.sc.common.io.IRequest;
import com.stabilit.sc.common.io.IResponse;
import com.stabilit.sc.common.io.SCMP;
import com.stabilit.sc.common.io.SCMPMsgType;

public class EchoCommand extends CommandAdapter {

	public EchoCommand() {
		this.commandValidator = new EchoCommandValidator();
	}

	@Override
	public SCMPMsgType getKey() {
		return SCMPMsgType.REQ_ECHO;
	}

	@Override
	public ICommandValidator getCommandValidator() {
		return super.getCommandValidator();
	}

	@Override
	public void run(IRequest request, IResponse response) throws CommandException {
		
		EchoMessage echoReq = (EchoMessage) request.getSCMP().getBody();
		
		String text = (String) echoReq.getAttribute("text");
		text.toUpperCase();
		
		EchoMessage echo = new EchoMessage();
		echo.setAttribute("text", text);
				
		SCMP scmpReply = new SCMP();
		scmpReply.setBody(echo);
		response.setSCMP(scmpReply);
	}

	@Override
	public IFactoryable newInstance() {
		return this;
	}

	public class EchoCommandValidator implements ICommandValidator {

		@Override
		public void validate(IRequest request, IResponse response) throws SCMPValidatorException {
		}
	}

}
