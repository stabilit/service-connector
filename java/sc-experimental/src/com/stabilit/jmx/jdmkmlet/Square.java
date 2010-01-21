package com.stabilit.jmx.jdmkmlet;

/*
 * @(#)file      Square.java
 * @(#)author    Sun Microsystems, Inc.
 * @(#)version   1.4
 * @(#)lastedit  04/02/02
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/**
 * Simple definition of a standard MBean, named "Square".
 *
 * The "Square" standard MBean shows how to expose attributes and
 * operations for management by implementing its corresponding
 * "SquareMBean" management interface.
 *
 * This MBean has one attribute exposed for management by a JMX agent:
 * - the read/write "SideLength" attribute.
 */

public class Square implements SquareMBean {

    //
    // -----------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------
    //

    /**
     * Creates a new 5cm x 5cm square object.
     *
     * @param <VAR>sideLength</VAR> the new value of the "SideLength" attribute.
     */
    public Square() {
    }

    /**
     * Creates a new square object with a given side length (in cm).
     *
     * @param <VAR>sideLength</VAR> the new value of the "SideLength" attribute.
     */
    public Square(Integer sideLength) {
	this.sideLength = sideLength;
    }

    //
    // -----------------------------------------------------
    // IMPLEMENTATION OF THE SquareMBean INTERFACE
    // -----------------------------------------------------
    //

    /**
     * Getter: get the "SideLength" attribute of the "SquareMBean" standard
     * MBean.
     *
     * @return the current value of the "SideLength" attribute.
     */
    public Integer getSideLength() {
        return sideLength;
    }

    /** 
     * Setter: set the "SideLength" attribute of the "SquareMBean" standard
     * MBean.
     *
     * @param <VAR>sideLength</VAR> the new value of the "SideLength" attribute.
     */
    public void setSideLength(Integer sideLength) {
        this.sideLength = sideLength;
    }

    //
    // -----------------------------------------------------
    // ATTRIBUTES ACCESSIBLE FOR MANAGEMENT BY A JMX AGENT
    // -----------------------------------------------------
    //

    /**
     * Default side length is 5 cm.
     */
    private Integer sideLength = new Integer(5);
}
