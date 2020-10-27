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

import java.util.List;
import java.util.Map;

import org.apache.ibatis.builder.annotation.ProviderContext;

/**
 * CrudUpdateProvider
 *
 * @author workoss
 */
public class CrudUpdateProvider extends BaseProvider {

	public CharSequence updateById(Map<String, Object> params, ProviderContext context) {
		return executeSql(context, (tableColumnInfo -> {
			StringBuilder sqlBuilder = new StringBuilder("<script> update ");
			sqlBuilder.append(tableColumnInfo.getTableName());
			sqlBuilder.append(" <set> ");
			List<String> columns = tableColumnInfo.getColumnNames();
			for (int i = 0, j = columns.size(); i < j; i++) {
				if (tableColumnInfo.getIdColumnName().equalsIgnoreCase(tableColumnInfo.getColumnNames().get(i))) {
					continue;
				}
				sqlBuilder.append(" <if test=\"record." + tableColumnInfo.getPropertyNames().get(i) + "!=null\"> ");
				sqlBuilder.append(tableColumnInfo.getColumnNames().get(i));
				sqlBuilder.append("=");
				sqlBuilder.append(bindParameter("record." + tableColumnInfo.getPropertyNames().get(i)));
				sqlBuilder.append(",");
				sqlBuilder.append(" </if> ");
			}
			sqlBuilder.append(" </set> where ");
			sqlBuilder.append(tableColumnInfo.getIdColumnName());
			sqlBuilder.append("=");
			sqlBuilder.append(bindParameter("id"));
			sqlBuilder.append("</script>");
			return sqlBuilder.toString();

		}));
	}

}
