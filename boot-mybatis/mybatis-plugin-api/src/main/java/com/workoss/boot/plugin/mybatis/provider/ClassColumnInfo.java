package com.workoss.boot.plugin.mybatis.provider;

/**
 * 列信息
 * @author workoss
 */
public class ClassColumnInfo {

	/**
	 * 属性类型
	 */
	private Class propertyType;
	/**
	 * 属性名称
	 */
	private String propertyName;
	/**
	 * 列名
	 */
	private String columnName;


	public ClassColumnInfo(Class propertyType, String propertyName, String columnName) {
		this.propertyType = propertyType;
		this.propertyName = propertyName;
		this.columnName = columnName;
	}

	public Class getPropertyType() {
		return propertyType;
	}

	public ClassColumnInfo propertyType(Class propertyType) {
		this.propertyType = propertyType;
		return this;
	}

	public String getPropertyName() {
		return propertyName;
	}

	public ClassColumnInfo propertyName(String propertyName) {
		this.propertyName = propertyName;
		return this;
	}

	public String getColumnName() {
		return columnName;
	}

	public ClassColumnInfo columnName(String columnName) {
		this.columnName = columnName;
		return this;
	}
}
