package com.stabilit.jmx.jdmkmlet;

/*
 * @(#)file      SquareMBean.java
 * @(#)author    Sun Microsystems, Inc.
 * @(#)version   1.4
 * @(#)lastedit  04/02/02
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/**
 * This is the management interface explicitly defined for the "Square"
 * standard MBean.
 *
 * The "Square" standard MBean implements this interface
 * in order to be manageable through a JMX agent.
 *
 * The "SquareMBean" interface shows how to expose for management:
 * - a read/write attribute (named "SideLength") through its getter and setter
 *   methods.
 */

public interface SquareMBean {

    /**
     * Getter: get the "SideLength" attribute of the "SquareMBean" standard
     * MBean.
     *
     * @return the current value of the "SideLength" attribute.
     */
    public Integer getSideLength();

    /** 
     * Setter: set the "SideLength" attribute of the "SquareMBean" standard
     * MBean.
     *
     * @param <VAR>sideLength</VAR> the new value of the "SideLength" attribute.
     */
    public void setSideLength(Integer sideLength);
}
