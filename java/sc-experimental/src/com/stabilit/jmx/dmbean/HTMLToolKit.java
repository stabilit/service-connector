package com.stabilit.jmx.dmbean;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import java.lang.reflect.*;
import java.rmi.*;

public class HTMLToolKit {
	private static String indent() {
		return ("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
	}

	private static StringBuffer getClassString(Class[] classes) {
		StringBuffer s = new StringBuffer();
		for (int j = 0; classes != null && j < classes.length; j++) {
			s.append(classes[j].getName());
			if ( j < classes.length - 1)
				s.append(", ");
		}
		return (s);
	}

	private static StringBuffer getParametersAndExceptions(Class[] parameters, Class[] exceptions) {
		StringBuffer s = new StringBuffer();

		s.append("(");
		s.append(getClassString(parameters));
		s.append(")");

		if (exceptions != null && exceptions.length > 0) {
			s.append(" throws ");
			s.append(getClassString(exceptions));
		}
		return (s);
	}

	// compiles a String containing the class details in HTML format
	public static String createClassDetails(Class c) {
		StringBuffer text = new StringBuffer();

		Class sc = c.getSuperclass();
		String name = c.getName();

		text.append("class <b>");
		text.append(name);
		text.append("</b>");
		text.append(" extends ");
		text.append(sc.getName());
		text.append("<br>");

		Class[] interfaces = c.getInterfaces();
		if (interfaces != null && interfaces.length > 0) {
			text.append("<br>All Implemented Interfaces:<br>");
			for (int i = 0; i < interfaces.length; i++) {
				text.append(indent());
				text.append(interfaces[i].getName());
				text.append("<br>");
			}
		}

		Constructor[] constructors = c.getConstructors();
		text.append("<br>All Public Constructors:<br>");
		for (int i = 0; constructors != null && i < constructors.length; i++) {
			text.append(indent());
			text.append("<b>");
			text.append(constructors[i].getName());
			text.append("</b>");
			text.append(getParametersAndExceptions(
				constructors[i].getParameterTypes(),
				constructors[i].getExceptionTypes()
			));
			text.append("<br>");
		}

		Method[] methods = c.getMethods();
		text.append("<br>All Public Methods:<br>");
		for (int i = 0; methods != null && i < methods.length; i++) {
			text.append(indent());
			text.append(methods[i].getReturnType().getName());
			text.append(" <b>");
			text.append(methods[i].getName());
			text.append("</b>");
			text.append(getParametersAndExceptions(
				methods[i].getParameterTypes(),
				methods[i].getExceptionTypes()
			));
			text.append("<br>");
		}

		Field[] fields = c.getFields();
		text.append("<br>All Public Fields:<br>");
		for (int i = 0; fields != null && i < fields.length; i++) {
			text.append(indent());
			text.append(fields[i].getType().getName());
			text.append(" ");
			text.append(fields[i].getName());
			text.append("<br>");
		}

		return (new String(text));
	}
}
