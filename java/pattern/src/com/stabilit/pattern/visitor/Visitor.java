package com.stabilit.pattern.visitor;

public interface Visitor {
	void visit(Wheel wheel);

	void visit(Engine engine);

	void visit(Body body);

	void visitCar(Car car);
}
