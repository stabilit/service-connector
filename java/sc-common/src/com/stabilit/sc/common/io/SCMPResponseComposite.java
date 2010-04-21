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
public class SCMPResponseComposite extends SCMP {

	private SCMP scmp;
	private int offset;
	private int scmpCallLength;
	private SCMP current;
	
	public SCMPResponseComposite(IResponse response) {
		this.scmp = response.getSCMP();
		this.scmpCallLength = this.scmp.getBodyLength();
		this.offset = 0;
		this.current = null;
	}
	
	public SCMP getFirst() {
		this.offset = 0;
		this.current = new SCMPResponsePart(scmp, this.offset);
		this.offset += current.getBodyLength();		
		return this.current;
	}
	
	public boolean hasNext() {
        return this.offset < this.scmpCallLength;		
	}
	
	public SCMP getNext() {
	    if (this.hasNext()) {
			this.current = new SCMPResponsePart(scmp, this.offset);			    
			this.offset += current.getBodyLength();
			return this.current;
	    }
	    this.current = null;
	    return this.current;
	}
			
}
