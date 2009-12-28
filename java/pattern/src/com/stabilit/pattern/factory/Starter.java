package com.stabilit.pattern.factory;

public class Starter {
	public static void main(String args[]) {
		for (PizzaFactory.PizzaType pizzaType : PizzaFactory.PizzaType.values()) {
			System.out.println("Price of " + pizzaType + " is "
					+ PizzaFactory.createPizza(pizzaType).getPrice());
		}
	}

}
