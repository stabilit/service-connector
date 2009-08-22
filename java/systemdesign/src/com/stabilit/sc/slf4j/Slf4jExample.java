package com.stabilit.sc.slf4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Slf4jExample {

	public static void main(final String[] args) {
		final Logger logger = LoggerFactory.getLogger(Slf4jExample.class);

		logger.info("Hello World!");
	}

	private Slf4jExample() {
	}

}