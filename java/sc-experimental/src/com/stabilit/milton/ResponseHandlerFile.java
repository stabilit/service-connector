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
package com.stabilit.milton;

import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.ResponseHandler;

/**
 * @author JTraber
 * 
 */
public class ResponseHandlerFile implements ResponseHandler {

	@Override
	public OutputStream createExceptionReply() {
		return null;
	}

	@Override
	public OutputStream createReply() {
		return null;
	}
}
