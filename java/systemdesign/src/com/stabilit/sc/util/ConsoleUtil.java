package com.stabilit.sc.util;

public class ConsoleUtil {

	public static String getArg(String[] args, String key) {
		if (args.length <= 0 || key == null) {
			return null;
		}
		for (int i = 0; i < args.length; i++) {
			if (key.equals(args[i])) {
				if (i < args.length - 1) {
					return args[i + 1];
				}
				return null;
			}
		}
		return null;
	}
}
