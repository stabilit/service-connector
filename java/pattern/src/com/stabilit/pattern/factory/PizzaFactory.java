package com.stabilit.pattern.factory;

class PizzaFactory {
	public enum PizzaType {
		HamMushroom, Deluxe, Hawaiian
	}

	public static Pizza createPizza(PizzaType pizzaType) {
		switch (pizzaType) {
		case HamMushroom:
			return new HamAndMushroomPizza();
		case Deluxe:
			return new DeluxePizza();
		case Hawaiian:
			return new HawaiianPizza();
		}
		throw new IllegalArgumentException("The pizza type " + pizzaType
				+ " is not recognized.");
	}
}
