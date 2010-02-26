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
package com.stabilit.sc.registrar;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.stabilit.sc.app.client.IClientConnection;
import com.stabilit.sc.msg.IMessage;
import com.stabilit.sc.serviceserver.handler.IResponseHandler;

/**
 * @author JTraber
 * 
 */
public class RegistrarResponseHandler implements IResponseHandler<IClientConnection> {

	private final BlockingQueue<IMessage> answer = new LinkedBlockingQueue<IMessage>();

	public Object getMessageSync() {
		IMessage scmp;
		boolean interrupted = false;
		for (;;) {
			try {
				// take() wartet bis Message in Queue kommt!
				scmp = answer.take();
				break;
			} catch (InterruptedException e) {
				interrupted = true;
			}
		}

		if (interrupted) {
			Thread.currentThread().interrupt();
		}
		return scmp;
	}

	/* (non-Javadoc)
	 * @see com.stabilit.sc.serviceserver.IResponseHandler#messageReceived(com.stabilit.sc.pool.Connection, com.stabilit.sc.io.SCMP)
	 */
	@Override
	public void messageReceived(IClientConnection con, Object obj) throws Exception {
		answer.offer((IMessage)obj);		
	}
}
