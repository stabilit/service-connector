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
package com.stabilit.sc.example.client;

import org.apache.log4j.Logger;

import com.stabilit.sc.io.SCMP;
import com.stabilit.sc.msg.ClientListener;
import com.stabilit.sc.pool.IPoolConnection;

/**
 * @author JTraber
 * 
 */

public class ClientCallback extends ClientListener {

	int count = 0;
	Logger log = Logger.getLogger(ClientCallback.class);

	@Override
	public void messageReceived(IPoolConnection conn, SCMP scmp) throws Exception {
		super.messageReceived(conn, scmp);

		if (scmp.getMessageId().equals("asyncCall")) {
			log.debug("Messages asyncCall confirmed " + scmp.getMessageId() + " on TcpSPServerListener " + count);
			count++;
		}
	}
}
