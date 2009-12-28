package com.stabilit.pattern.visitor;

public class Starter {
	public static void main(String[] args) {
		Car car = new Car();
		Visitor printVisitor = new PrintVisitor();
		Visitor doVisitor = new DoVisitor();
		printVisitor.visitCar(car);
		doVisitor.visitCar(car);
	}
}
