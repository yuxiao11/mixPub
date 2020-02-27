package com.ifeng.recom.mixrecall.prerank.exception;

/**
 * 
 *
 */
public class ExpConfigError extends Exception {

	private static final long serialVersionUID = 3302851967787062633L;
	
	public ExpConfigError(final String message) {
		super(message);
	}
	
	public ExpConfigError(Throwable e) {
		super(e);
	}
	
	public ExpConfigError(String message, Throwable e) {
		super(message, e);
	}
}
