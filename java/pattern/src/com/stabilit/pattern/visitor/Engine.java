package com.stabilit.pattern.visitor;

public class Engine implements CarElement {
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}
}
