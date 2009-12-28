package com.stabilit.pattern.visitor;


public class Wheel implements CarElement {
	private String name;

	Wheel(String name) {
		this.name = name;
	}

	String getName() {
		return this.name;
	}

	public void accept(Visitor visitor) {
		visitor.visit(this);
	}
}