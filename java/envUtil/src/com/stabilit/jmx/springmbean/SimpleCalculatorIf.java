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
package com.stabilit.jmx.springmbean;

/**
 * Interface to expose Model MBean via Spring.
 */
public interface SimpleCalculatorIf {
	public int add(final int augend, final int addend);

	public int subtract(final int minuend, final int subtrahend);

	public int multiply(final int factor1, final int factor2);

	public double divide(final int dividend, final int divisor);
}
