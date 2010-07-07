package com.stabilit.timer;

import java.util.Timer;
import java.util.TimerTask;

class Task extends TimerTask {
	private String text;

	public Task(String text) {
		this.text = text;
	}

	public void run() {
		System.out.println(this.text);
	}
}

public class TimerTaskDemo {
	public static void main(String args[]) {
		Timer timer = new Timer();

		// in 2s einmalig
		Task singleTask = new Task("single");
		timer.schedule(singleTask, 2000);
		singleTask.cancel();

		// nach 2 Sek geht’s los
		timer.schedule(new Task("hans"), 0, 2000);

		// nach 1 Sek geht’s los und dann alle 5 Sekunden
		timer.schedule(new Task("peter"), 1000, 5000);
		
		// nach 0 Sek geht’s los und dann alle 5 Sekunden
		timer.schedule(new Task("claudia"), 1000, 10000);

		Timer timer2 = new Timer();
		// nach 2 Sek geht’s los
		timer2.schedule(new Task("hans"), 0, 2000);

		// nach 1 Sek geht’s los und dann alle 5 Sekunden
		timer2.schedule(new Task("peter"), 1000, 5000);
		
		// nach 0 Sek geht’s los und dann alle 5 Sekunden
		timer2.schedule(new Task("claudia"), 1000, 10000);
	}
}
