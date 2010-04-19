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
package com.stabilit.sc.common.io;

/**
 * @author JTraber
 * 
 */
public enum SCMPBodyType {

	binary("binary"), text("text"), message("message"), undefined("undefined");

	private String name;

	private SCMPBodyType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public static SCMPBodyType getBodyType(String bodyType) {
		if (bodyType == null) {
			return undefined;
		}
		return SCMPBodyType.valueOf(bodyType);
	}

	// mime types http://msdn.microsoft.com/en-us/library/ms775147%28VS.85%29.aspx
	public String getMimeType() {
		switch (this) {
		case binary:
			return "application/octet-stream";
		case text:
			return "text/plain";
		case message:
			return "text/plain";
		default:
			return "application/octet-stream";
		}
	}
}
