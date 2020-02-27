package com.ifeng.recom.mixrecall.prerank.exception;

/**
 * 
 */
public class ExpException extends RuntimeException {

	private static final long serialVersionUID = -5596748957218943902L;

	public ExpException(String message) {
		super(message);
	}
	
	public ExpException(Throwable e) {
		super(e);
	}
	
	public ExpException(String message, Throwable e) {
		super(message, e);
	}
}
