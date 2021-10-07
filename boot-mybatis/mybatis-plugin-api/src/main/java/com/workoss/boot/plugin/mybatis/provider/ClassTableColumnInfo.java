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

import java.util.ArrayList;
import java.util.List;

/**
 * 类表信息
 *
 * @author workoss
 */
public class ClassTableColumnInfo {

	private String tableName;

	private String catalog;

	private String schema;

	private ClassColumnInfo idColumn;

	private List<ClassColumnInfo> columnInfos;

	public String getTableName() {
		return tableName;
	}

	public ClassTableColumnInfo tableName(String tableName) {
		this.tableName = tableName;
		return this;
	}

	public String getCatalog() {
		return catalog;
	}

	public ClassTableColumnInfo catalog(String catalog) {
		this.catalog = catalog;
		return this;
	}

	public String getSchema() {
		return schema;
	}

	public ClassTableColumnInfo schema(String schema) {
		this.schema = schema;
		return this;
	}

	public ClassColumnInfo getIdColumn() {
		return idColumn;
	}

	public ClassTableColumnInfo idColumn(ClassColumnInfo idColumn) {
		this.idColumn = idColumn;
		return this;
	}

	public List<ClassColumnInfo> getColumnInfos() {
		return columnInfos;
	}

	public ClassTableColumnInfo columnInfos(List<ClassColumnInfo> columnInfos) {
		this.columnInfos = columnInfos;
		return this;
	}

	public ClassTableColumnInfo addColumnInfo(ClassColumnInfo columnInfo) {
		if (this.columnInfos == null) {
			this.columnInfos = new ArrayList<>();
		}
		this.columnInfos.add(columnInfo);
		return this;
	}

}
