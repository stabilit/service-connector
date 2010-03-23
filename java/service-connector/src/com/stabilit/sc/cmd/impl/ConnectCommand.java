package com.stabilit.sc.cmd.impl;

import java.net.SocketAddress;
import java.util.Date;

import com.stabilit.sc.cmd.CommandAdapter;
import com.stabilit.sc.cmd.CommandException;
import com.stabilit.sc.cmd.ICommandValidator;
import com.stabilit.sc.cmd.SCMPCommandException;
import com.stabilit.sc.cmd.SCMPValidatorException;
import com.stabilit.sc.ctx.IRequestContext;
import com.stabilit.sc.factory.IFactoryable;
import com.stabilit.sc.io.IRequest;
import com.stabilit.sc.io.IResponse;
import com.stabilit.sc.io.KeepAlive;
import com.stabilit.sc.io.SCMP;
import com.stabilit.sc.io.SCMPErrorCode;
import com.stabilit.sc.io.SCMPHeaderType;
import com.stabilit.sc.io.SCMPMsgType;
import com.stabilit.sc.io.SCMPReply;
import com.stabilit.sc.msg.impl.ConnectMessage;
import com.stabilit.sc.registry.ConnectionRegistry;
import com.stabilit.sc.util.MapBean;
import com.stabilit.sc.util.ValidatorUtility;

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
			scmpCommandException.setMessageType(SCMPMsgType.RES_CONNECT.getResponseName());
			throw scmpCommandException;
		}
		connectionRegistry.add(socketAddress, request.getAttributeMapBean());

		SCMPReply scmpReply = new SCMPReply();
		scmpReply.setMessageType(SCMPMsgType.REQ_CONNECT.getResponseName());
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
				// TODO msg in body??
				ConnectMessage msg = (ConnectMessage) scmp.getBody();

				// scmpVersion
				String scmpVersion = (String) msg.getAttribute(SCMPHeaderType.SCMP_VERSION.getName());
				ValidatorUtility.validateSCMPVersion(SCMP.VERSION, scmpVersion);
				request.setAttribute(SCMPHeaderType.SCMP_VERSION.getName(), scmpVersion);

				// compression
				String compression = (String) msg.getAttribute(SCMPHeaderType.COMPRESSION.getName());
				compression = ValidatorUtility.validateCompression(compression);
				request.setAttribute(SCMPHeaderType.COMPRESSION.getName(), compression);

				// localDateTime
				Date localDateTime = ValidatorUtility.validateLocalDateTime((String) msg
						.getAttribute(SCMPHeaderType.LOCAL_DATE_TIME.getName()));
				request.setAttribute(SCMPHeaderType.LOCAL_DATE_TIME.getName(), localDateTime);

				// KeepAliveTimeout && KeepAliveInterval
				KeepAlive keepAlive = ValidatorUtility.validateKeepAlive((String) msg
						.getAttribute(SCMPHeaderType.KEEP_ALIVE_TIMEOUT.getName()), (String) msg
						.getAttribute(SCMPHeaderType.KEEP_ALIVE_INTERVAL.getName()));
				request.setAttribute(SCMPHeaderType.KEEP_ALIVE_TIMEOUT.getName(), keepAlive);
			} catch (Throwable e) {
				SCMPValidatorException validatorException = new SCMPValidatorException();
				validatorException.setMessageType(SCMPMsgType.REQ_CONNECT.getResponseName());
				throw validatorException;
			}
		}
	}

}
