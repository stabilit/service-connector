package com.stabilit.timer;

import java.util.Timer;
import java.util.TimerTask;

class Task extends TimerTask {
	private Runnable target;

	public Task() {
		this.target = null;
	}

	public Task(Runnable target) {
		this.target = target;
	}

	public void run() {
		if (target != null) {
			target.run();
			return;
		}
		System.out.println("Task.run()");
	}
}

class TimerTaskRun implements Runnable {
	private String text;

	public TimerTaskRun(String text) {
		this.text = text;
	}

	public void run() {
		System.out.println("TimerTaskRun.run() for " + text);
	}
}

public class TimerTaskDemo {
	public static void main(String args[]) {
		Timer timer = new Timer();

		// in 2s einmalig
		TimerTaskRun singleTaskRun = new TimerTaskRun("single");
		Task singleTask = new Task(singleTaskRun);
		System.out.println("schedule single 2000");
		timer.schedule(singleTask, 2000);
		System.out.println("cancel single");
		singleTask.cancel();
		singleTask = new Task(singleTaskRun);
		System.out.println("schedule single 2000");
		timer.schedule(singleTask, 2000);

<<<<<<< .mine
//		// nach 2 Sek geht’s los
//		timer.schedule(new Task("hans"), 0, 2000);
//
//		// nach 1 Sek geht’s los und dann alle 5 Sekunden
//		timer.schedule(new Task("peter"), 1000, 5000);
//
//		// nach 0 Sek geht’s los und dann alle 5 Sekunden
//		timer.schedule(new Task("claudia"), 1000, 10000);
//		Timer timer2 = new Timer();
//
//		// nach 2 Sek geht’s los
//		timer2.schedule(new Task("hans"), 0, 2000);
//
//		// nach 1 Sek geht’s los und dann alle 5 Sekunden
//		timer2.schedule(new Task("peter"), 1000, 5000);
//
//		// nach 0 Sek geht’s los und dann alle 5 Sekunden
//		timer2.schedule(new Task("claudia"), 1000, 10000);
=======
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
>>>>>>> .r1124
	}
}
