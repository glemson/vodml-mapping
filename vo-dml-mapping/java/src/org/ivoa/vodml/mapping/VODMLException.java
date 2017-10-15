package org.ivoa.vodml.mapping;

public class VODMLException extends Exception {

	public static final int ILLEGAL_VALUE = 1;
	public static final int ILLEGAL_IDENTIFICATION = 2;
	public static final int ILLEGAL_CONCATENATION = 4;
	
	public VODMLException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public VODMLException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public VODMLException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public VODMLException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

}
