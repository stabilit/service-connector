package com.stabilit.pattern.visitor;

public class Body implements CarElement {
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}
}
