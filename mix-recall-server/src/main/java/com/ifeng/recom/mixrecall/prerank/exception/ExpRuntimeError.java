package com.ifeng.recom.mixrecall.prerank.exception;

/**
 * 
 *
 */
public class ExpRuntimeError extends Exception {

	private static final long serialVersionUID = 7453506118271795640L;

	public ExpRuntimeError(final String message) {
		super(message);
	}
	
	public ExpRuntimeError(Throwable e) {
		super(e);
	}
	
	public ExpRuntimeError(String message, Throwable e) {
		super(message, e);
	}

}
