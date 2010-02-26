/*
 *-----------------------------------------------------------------------------*
 *                            Copyright © 2010 by                              *
 *                    STABILIT Informatik AG, Switzerland                      *
 *                            ALL RIGHTS RESERVED                              *
 *                                                                             *
 * Valid license from STABILIT is required for possession, use or copying.     *
 * This software or any other copies thereof may not be provided or otherwise  *
 * made available to any other person. No title to and ownership of the        *
 * software is hereby transferred. The information in this software is subject *
 * to change without notice and should not be construed as a commitment by     *
 * STABILIT Informatik AG.                                                     *
 *                                                                             *
 * All referenced products are trademarks of their respective owners.          *
 *-----------------------------------------------------------------------------*
 */
/**
 * 
 */
package com.stabilit.sc.cmd.impl;

import com.stabilit.sc.cmd.CommandException;
import com.stabilit.sc.cmd.ICommand;
import com.stabilit.sc.io.IRequest;
import com.stabilit.sc.io.IResponse;
import com.stabilit.sc.msg.IMessage;
import com.stabilit.sc.msg.impl.KeepAliveMessage;

/**
 * @author JTraber
 * 
 */
public class KeepAliveCommand implements ICommand {

	@Override
	public String getKey() {
		return "KeepAlive";
	}

	@Override
	public ICommand newCommand() {
		return new KeepAliveCommand();
	}

	@Override
	public void run(IRequest request, IResponse response) throws CommandException {
		IMessage result = new KeepAliveMessage();
		try {
			//response.setSCMP(result);
		} catch (Exception e) {
			throw new CommandException(e.toString());
		}
	}
}
