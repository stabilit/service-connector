package com.stabilit.environment.perfomancetest;

public class Starter {

	public static void main(String[] args) {
		Controller cont = new Controller(20,250,260);
//		cont.runSingleThread();
		cont.runMultipleThreads();
	}
}
