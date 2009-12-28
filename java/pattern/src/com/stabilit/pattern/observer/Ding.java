package com.stabilit.pattern.observer;

import java.util.Observable;

public class Ding extends Observable {
	private String value;

	public void setValue(String s) {
		value = s;
		setChanged(); // set changed flag
		notifyObservers(value); // do notification
	}
}
