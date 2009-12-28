package com.stabilit.pattern.visitor;

public class DoVisitor implements Visitor {
	public void visit(Wheel wheel) {
		System.out.println("Steering my wheel");
	}

	public void visit(Engine engine) {
		System.out.println("Starting my engine");
	}

	public void visit(Body body) {
		System.out.println("Moving my body");
	}

	public void visitCar(Car car) {
		System.out.println("\nStarting my car");
		for (CarElement carElement : car.getElements()) {
			carElement.accept(this);
		}
		System.out.println("Started car");
	}
}
