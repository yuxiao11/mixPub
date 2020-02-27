package com.ifeng.recom.mixrecall.prerank.entity;

/**
 * Flag的数据类型，按照Java简单类型标准
 *
 */
public enum FlagType {

	STRING(String.class.getSimpleName()),
	INT(int.class.getSimpleName()),
	BOOLEAN(boolean.class.getSimpleName()),
	LONG(long.class.getSimpleName()),
	FLOAT(float.class.getSimpleName()),
	DOUBLE(double.class.getSimpleName());
	
	private String typeName;
	
	private FlagType(String typeName) {
		this.typeName = typeName;
	}

	public String getTypeName() {
		return this.typeName;
	}

}
