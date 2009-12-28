package com.stabilit.pattern.observer;

import java.util.Observable;
import java.util.Observer;

public class Beobachter implements Observer {
	public void update(Observable o, Object str) {
		System.out.println("update: " + str.toString());
	}
}
