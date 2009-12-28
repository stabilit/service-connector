package com.stabilit.pattern.visitor;

public class PrintVisitor implements Visitor {

	public void visit(Wheel wheel) {
		System.out.println("Visiting " + wheel.getName() + " wheel");
	}

	public void visit(Engine engine) {
		System.out.println("Visiting engine");
	}

	public void visit(Body body) {
		System.out.println("Visiting body");
	}

	public void visitCar(Car car) {
		System.out.println("\nVisiting car");
		for (CarElement element : car.getElements()) {
			element.accept(this);
		}
		System.out.println("Visited car");
	}

	public void visit(CarElement carElement) {
	}
}
