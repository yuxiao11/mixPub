package com.ifeng.recom.mixrecall.prerank.executor;

public class FeatureConfigException extends Exception{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6355379331157716460L;

	public FeatureConfigException(String message) {
        super(message);
    }

    public FeatureConfigException(String message, Throwable e) {
        super(message, e);
    }

}
