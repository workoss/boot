/*
 * Copyright © 2020-2021 workoss (WORKOSS)
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

import java.util.ArrayList;
import java.util.List;

/**
 * TableColumnInfo
 *
 * @author workoss
 */
public class TableColumnInfo {

	private String tableName;

	private String idPropertyName = "id";

	private String idColumnName = "id";

	private List<Class> propertyTypes;

	private List<String> propertyNames;

	private List<String> columnNames;

	public TableColumnInfo addPropertyName(String propertyName) {
		if (propertyNames == null) {
			this.propertyNames = new ArrayList<>();
		}
		this.propertyNames.add(propertyName);
		return this;
	}

	public TableColumnInfo addPropertyType(Class propertyType) {
		if (propertyTypes == null) {
			this.propertyTypes = new ArrayList<>();
		}
		this.propertyTypes.add(propertyType);
		return this;
	}

	public TableColumnInfo addColumnName(String columnName) {
		if (columnNames == null) {
			this.columnNames = new ArrayList<>();
		}
		this.columnNames.add(columnName);
		return this;
	}

	String getTableName() {
		return tableName;
	}

	void setTableName(String tableName) {
		this.tableName = tableName;
	}

	String getIdPropertyName() {
		return idPropertyName;
	}

	void setIdPropertyName(String idPropertyName) {
		this.idPropertyName = idPropertyName;
	}

	String getIdColumnName() {
		return idColumnName;
	}

	void setIdColumnName(String idColumnName) {
		this.idColumnName = idColumnName;
	}

	List<Class> getPropertyTypes() {
		return propertyTypes;
	}

	void setPropertyTypes(List<Class> propertyTypes) {
		this.propertyTypes = propertyTypes;
	}

	List<String> getPropertyNames() {
		return propertyNames;
	}

	void setPropertyNames(List<String> propertyNames) {
		this.propertyNames = propertyNames;
	}

	List<String> getColumnNames() {
		return columnNames;
	}

	void setColumnNames(List<String> columnNames) {
		this.columnNames = columnNames;
	}

}
