package com.stabilit.pattern.facade;

public class MortgageApp {
	int amount;
	private Bank bank = new Bank();
	private Credit credit = new Credit();

	public MortgageApp(int amount) {
		this.amount = amount;
	}

	public boolean IsEligible(Customer c) {
		// Check creditworthyness of applicant
		if (!bank.sufficientSavings(c))
			return false;
		if (!credit.goodCredit(amount, c))
			return false;
		return true;
	}
}
