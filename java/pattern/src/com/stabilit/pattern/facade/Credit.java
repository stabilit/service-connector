package com.stabilit.pattern.facade;

public class Credit {
	public boolean goodCredit(int amount, Customer c) {
		System.out.println("Check credit: " + c.getName());
		return true;
	}
}
