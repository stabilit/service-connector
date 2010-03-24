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
package com.stabilit.sc.io;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author JTraber
 *
 */
public class IpAddressList {

	List<String> ipAddressList = new ArrayList<String>();

	public List<String> getIpAddressList() {
		return ipAddressList;
	}
	
	public void addIp(String ip) {
		ipAddressList.add(ip);
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		
		Iterator<String> iter = ipAddressList.iterator();
		
		while(iter.hasNext()) {
			sb.append(iter.next());
			if(iter.hasNext()) sb.append("/");
		}		
		return sb.toString();
	}
}
