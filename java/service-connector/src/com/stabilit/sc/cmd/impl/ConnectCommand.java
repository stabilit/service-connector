package com.stabilit.sc.cmd.impl;

import java.net.SocketAddress;
import java.util.Date;
import java.util.Map;

import org.apache.log4j.Logger;

import com.stabilit.sc.common.ctx.IRequestContext;
import com.stabilit.sc.common.factory.IFactoryable;
import com.stabilit.sc.common.io.IRequest;
import com.stabilit.sc.common.io.IResponse;
import com.stabilit.sc.common.io.KeepAlive;
import com.stabilit.sc.common.io.SCMP;
import com.stabilit.sc.common.io.SCMPErrorCode;
import com.stabilit.sc.common.io.SCMPHeaderAttributeKey;
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
	
	private static Logger log = Logger.getLogger(ConnectCommand.class);
	
	public ConnectCommand() {
		this.commandValidator = new ConnectCommandValidator();
	}

	@Override
	public SCMPMsgType getKey() {
		return SCMPMsgType.CONNECT;
	}

	@Override
	public ICommandValidator getCommandValidator() {
		return super.getCommandValidator();
	}

	@Override
	public void run(IRequest request, IResponse response) throws CommandException {
		log.debug("Run command " + this.getKey());
		IRequestContext requestContext = request.getContext();
		SocketAddress socketAddress = requestContext.getSocketAddress();
		ConnectionRegistry connectionRegistry = ConnectionRegistry.getCurrentInstance();
		// TODO is socketAddress the right thing to save a a unique key?

		MapBean<?> mapBean = connectionRegistry.get(socketAddress);

		if (mapBean != null) {
			log.debug("command error: already connected");
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
		public void validate(IRequest request, IResponse response) throws Exception {
			SCMP scmp = request.getSCMP();

			try {

				Map<String, String> scmpHeader = scmp.getHeader();
				// scVersion
				String scVersion = (String) scmpHeader.get(SCMPHeaderAttributeKey.SC_VERSION.getName());
				ValidatorUtility.validateSCVersion(SCMP.SC_VERSION, scVersion);
				request.setAttribute(SCMPHeaderAttributeKey.SC_VERSION.getName(), scVersion);

				Boolean compression = scmp.getHeaderBoolean(SCMPHeaderAttributeKey.COMPRESSION);
				if(compression == null) {
					compression = true;
				}
				request.setAttribute(SCMPHeaderAttributeKey.COMPRESSION.getName(), compression);

				// localDateTime
				Date localDateTime = ValidatorUtility.validateLocalDateTime((String) scmpHeader
						.get(SCMPHeaderAttributeKey.LOCAL_DATE_TIME.getName()));
				request.setAttribute(SCMPHeaderAttributeKey.LOCAL_DATE_TIME.getName(), localDateTime);

				// KeepAliveTimeout && KeepAliveInterval
				KeepAlive keepAlive = ValidatorUtility.validateKeepAlive((String) scmpHeader
						.get(SCMPHeaderAttributeKey.KEEP_ALIVE_TIMEOUT.getName()), (String) scmpHeader
						.get(SCMPHeaderAttributeKey.KEEP_ALIVE_INTERVAL.getName()));
				request.setAttribute(SCMPHeaderAttributeKey.KEEP_ALIVE_TIMEOUT.getName(), keepAlive);
			} catch (Throwable e) {
				log.debug("validation error: " + e.getMessage());
				SCMPValidatorException validatorException = new SCMPValidatorException();
				validatorException.setMessageType(getKey().getResponseName());
				throw validatorException;
			}
		}
	}
}