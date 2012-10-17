package com.stabilit.cfpFileTest;

import org.serviceconnector.util.IReversibleEnum;
import org.serviceconnector.util.ReverseEnumMap;

public enum FILE_SERVICE_FUNCTION implements IReversibleEnum<String, FILE_SERVICE_FUNCTION> {

	LIST("list"), UPLOAD("upload"), DOWNLOAD("download"), NOT_VALID("");

	private String value;
	private static final ReverseEnumMap<String, FILE_SERVICE_FUNCTION> REVERSE_MAP = new ReverseEnumMap<String, FILE_SERVICE_FUNCTION>(
			FILE_SERVICE_FUNCTION.class);

	private FILE_SERVICE_FUNCTION(String value) {
		this.value = value;
	}

	public static FILE_SERVICE_FUNCTION getFileServiceFunction(String fileSrvString) {
		FILE_SERVICE_FUNCTION function = REVERSE_MAP.get(fileSrvString);
		if (function == null) {
			// fileSrvString doesn't match to a valid type
			return FILE_SERVICE_FUNCTION.NOT_VALID;
		}
		return function;
	}

	public String getValue() {
		return this.value;
	}

	public FILE_SERVICE_FUNCTION reverse(String fileSrvString) {
		return FILE_SERVICE_FUNCTION.getFileServiceFunction(fileSrvString);
	}
}
