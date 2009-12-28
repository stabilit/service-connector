package com.stabilit.pattern.facade;

public class Starter {
	public static void Main(String[] args) {
		MortgageApp mortgage = new MortgageApp(125000);
		mortgage.IsEligible(new Customer("Gabrielle McKinsey"));
	}
}
