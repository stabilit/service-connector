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
package com.stabilit.sc.srv.client;

import com.stabilit.sc.cln.client.Client;
import com.stabilit.sc.common.factory.IFactoryable;
import com.stabilit.sc.common.io.EncoderDecoderFactory;
import com.stabilit.sc.common.io.IEncoderDecoder;
import com.stabilit.sc.common.io.SCMP;

/**
 * @author JTraber
 * 
 */
public class SCClient extends Client {

	public SCClient() {
	}
	
	@Override
	public IFactoryable newInstance() {
		return new SCClient();
	}

	@Override
	public SCMP sendAndReceive(SCMP scmp) throws Exception {
		IEncoderDecoder encoderDecoder = EncoderDecoderFactory.getCurrentEncoderDecoderFactory().newInstance(scmp);
		clientConnection.setEncoderDecoder(encoderDecoder);
		return clientConnection.sendAndReceive(scmp);
	}
}
