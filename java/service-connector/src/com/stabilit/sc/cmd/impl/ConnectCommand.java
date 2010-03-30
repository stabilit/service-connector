package com.stabilit.sc.cmd.impl;

import java.net.SocketAddress;
import java.util.Date;
import java.util.Map;

import com.stabilit.sc.common.ctx.IRequestContext;
import com.stabilit.sc.common.factory.IFactoryable;
import com.stabilit.sc.common.io.IRequest;
import com.stabilit.sc.common.io.IResponse;
import com.stabilit.sc.common.io.KeepAlive;
import com.stabilit.sc.common.io.SCMP;
import com.stabilit.sc.common.io.SCMPErrorCode;
import com.stabilit.sc.common.io.SCMPHeaderType;
import com.stabilit.sc.common.io.SCMPMsgType;
import com.stabilit.sc.common.io.SCMPReply;
import com.stabilit.sc.common.util.MapBean;
import com.stabilit.sc.common.util.ValidatorUtility;
import com.stabilit.sc.registry.ConnectionRegistry;
import com.stabilit.sc.srv.cmd.CommandAdapter;
import com.stabilit.sc.srv.cmd.CommandException;
import com.stabilit.sc.srv.cmd.ICommandValidator;
import com.stabilit.sc.srv.cmd.SCMPCommandException;
import com.stabilit.sc.srv.cmd.SCMPValidatorException;

public class ConnectCommand extends CommandAdapter {

	public ConnectCommand() {
		this.commandValidator = new ConnectCommandValidator();
	}

	@Override
	public SCMPMsgType getKey() {
		return SCMPMsgType.REQ_CONNECT;
	}

	@Override
	public ICommandValidator getCommandValidator() {
		return super.getCommandValidator();
	}

	@Override
	public void run(IRequest request, IResponse response) throws CommandException {
		IRequestContext requestContext = request.getContext();
		SocketAddress socketAddress = requestContext.getSocketAddress();
		ConnectionRegistry connectionRegistry = ConnectionRegistry.getCurrentInstance();
		// TODO is socketAddress the right thing to save a a unique key?

		MapBean<?> mapBean = connectionRegistry.get(socketAddress);

		if (mapBean != null) {
			SCMPCommandException scmpCommandException = new SCMPCommandException(
					SCMPErrorCode.ALREADY_CONNECTED);
			scmpCommandException.setMessageType(getKey().getResponseName());
			throw scmpCommandException;
		}
		connectionRegistry.add(socketAddress, request.getAttributeMapBean());

		SCMPReply scmpReply = new SCMPReply();
		scmpReply.setMessageType(getKey().getResponseName());
		scmpReply.setLocalDateTime();
		response.setSCMP(scmpReply);
	}

	@Override
	public IFactoryable newInstance() {
		return this;
	}

	public class ConnectCommandValidator implements ICommandValidator {

		@Override
		public void validate(IRequest request, IResponse response) throws SCMPValidatorException {
			SCMP scmp = request.getSCMP();

			try {

				Map<String, String> scmpHeader = scmp.getHeader();
				// scmpVersion
				String scmpVersion = (String) scmpHeader.get(SCMPHeaderType.SCMP_VERSION.getName());
				ValidatorUtility.validateSCMPVersion(SCMP.VERSION, scmpVersion);
				request.setAttribute(SCMPHeaderType.SCMP_VERSION.getName(), scmpVersion);

				// compression
				String compression = (String) scmpHeader.get(SCMPHeaderType.COMPRESSION.getName());
				compression = ValidatorUtility.validateBoolean(compression, true);
				request.setAttribute(SCMPHeaderType.COMPRESSION.getName(), compression);

				// localDateTime
				Date localDateTime = ValidatorUtility.validateLocalDateTime((String) scmpHeader
						.get(SCMPHeaderType.LOCAL_DATE_TIME.getName()));
				request.setAttribute(SCMPHeaderType.LOCAL_DATE_TIME.getName(), localDateTime);

				// KeepAliveTimeout && KeepAliveInterval
				KeepAlive keepAlive = ValidatorUtility.validateKeepAlive((String) scmpHeader
						.get(SCMPHeaderType.KEEP_ALIVE_TIMEOUT.getName()), (String) scmpHeader
						.get(SCMPHeaderType.KEEP_ALIVE_INTERVAL.getName()));
				request.setAttribute(SCMPHeaderType.KEEP_ALIVE_TIMEOUT.getName(), keepAlive);
			} catch (Throwable e) {
				SCMPValidatorException validatorException = new SCMPValidatorException();
				validatorException.setMessageType(getKey().getResponseName());
				throw validatorException;
			}
		}
	}

}
