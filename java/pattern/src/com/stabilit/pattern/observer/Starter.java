package com.stabilit.pattern.observer;

public class Starter {
	public static void main(String[] args) {
		Ding d = new Ding();
		Beobachter b = new Beobachter();
		d.addObserver(b);
		d.setValue("Observer Test");
	}
}
