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
package com.stabilit.date;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author JTraber
 * 
 */
public class DateEval {

	/**
	 * @param args
	 * @throws ParseException
	 */
	public static void main(String[] args) throws ParseException {
		DateEval dateEval = new DateEval();
		dateEval.run();
	}

	public void run() {
		try {
			String localDateTimeString = "2010-12-12T12:12:12.065+0100";
			SimpleDateFormat SDFs = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
			// SDFs.setLenient(false);
			Date localDateTime = SDFs.parse(localDateTimeString);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
}
