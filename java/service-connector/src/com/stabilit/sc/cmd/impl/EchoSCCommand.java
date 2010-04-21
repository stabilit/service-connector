package com.stabilit.sc.cmd.impl;

import org.apache.log4j.Logger;

import com.stabilit.sc.common.factory.IFactoryable;
import com.stabilit.sc.common.io.EncoderDecoderFactory;
import com.stabilit.sc.common.io.IEncoderDecoder;
import com.stabilit.sc.common.io.IRequest;
import com.stabilit.sc.common.io.IResponse;
import com.stabilit.sc.common.io.SCMP;
import com.stabilit.sc.common.io.SCMPHeaderAttributeKey;
import com.stabilit.sc.common.io.SCMPMsgType;
import com.stabilit.sc.common.io.SCMPReply;
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

		SCMP scmpReply = new SCMPReply();
		Object obj = scmp.getBody();
		scmpReply.setMessageType(getKey().getResponseName());
		scmpReply.setSessionId(scmp.getSessionId());
		scmpReply.setHeader(scmp, SCMPHeaderAttributeKey.SEQUENCE_NR);
		scmpReply.setBody(obj);
		if (obj.toString().length() > 100) {
			System.out.println("EchoSCCommand body = " + obj.toString().substring(0, 100));
		} else {
			System.out.println("EchoSCCommand body = " + obj.toString());
		}
		response.setSCMP(scmpReply);
		// don't use large message encoder/decoder
		//response.setEncoderDecoder((IEncoderDecoder) EncoderDecoderFactory.getCurrentEncoderDecoderFactory().newInstance());
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
