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
package com.stabilit.sc.service;

import java.io.InputStream;

import com.stabilit.sc.exception.ScConnectionException;

/**
 * File Service.
 * @author JTraber
 *
 */
class FileService implements IFileService {

	/** {@inheritDoc} */
	@Override
	public InputStream downloadFile(String name) {
		return null;
	}

	/** {@inheritDoc} */
	@Override
	public void uploadFile(InputStream file) {
	}

	/** {@inheritDoc} */
	@Override
	public void connect(int timeout, ConnectionCtx connectionInformation) throws ScConnectionException {
	}

	/** {@inheritDoc} */
	@Override
	public void disconnect(int timeout) throws ScConnectionException {
	}
}
