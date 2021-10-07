/*
 * Copyright 2019-2021 workoss (https://www.workoss.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.workoss.boot.plugin.mybatis.provider;

/**
 * 列信息
 *
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
