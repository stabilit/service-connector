package com.stabilit.pattern.facade;

public class Bank {
	public boolean sufficientSavings(Customer c) {
		System.out.println("Check bank: " + c.getName());
		return true;
	}
}
