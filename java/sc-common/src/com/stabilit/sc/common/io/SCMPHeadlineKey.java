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
public enum SCMPHeadlineKey {

	UNDEF, REQ, RES, EXC, PRQ, PRS;
	
	public static SCMPHeadlineKey getMsgHeaderKey(String headerKey) {
		return SCMPHeadlineKey.valueOf(headerKey);
	}
	public static SCMPHeadlineKey getMsgHeaderKey(byte[]b) {
		if (b == null) {
			return UNDEF;
		}
		if (b.length < 3) {
			return UNDEF;
		}
		if (b[0] == 'R' && b[1] == 'E') {
			if (b[2] == 'Q') {
				return REQ;
			}
			if (b[2] == 'S') {
				return RES;
			}
			return UNDEF;			
		}
		if (b[0] == 'P' && b[1] == 'R') {
			if (b[2] == 'Q') {
				return PRQ;
			}
			if (b[2] == 'S') {
				return PRS;
			}
			return UNDEF;			
		}
		if (b[0] == 'E' && b[1] == 'X' &&  b[2] == 'C') {
			return EXC;
		}
		return UNDEF;
	}

}
