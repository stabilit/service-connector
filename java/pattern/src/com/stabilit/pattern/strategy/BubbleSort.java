package com.stabilit.pattern.strategy;

public class BubbleSort implements SortInterface {
	public void sort(double[] list) {
		double temp;
		for (int i = 0; i < list.length; i++) {
			for (int j = 0; j < list.length - i; j++) {
				if (list[i] < list[j]) {
					temp = list[i];
					list[i] = list[j];
					list[j] = temp;
				}
			}
		}
	}
}
