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
 * CrudInsertProvider
 *
 * @author workoss
 */
public class CrudInsertProvider extends BaseProvider {

	public CharSequence insert(Map<String, Object> params, ProviderContext context) {
		return executeSql(context, (tableColumnInfo -> {
			StringBuilder sqlBuilder = new StringBuilder("insert into ");
			sqlBuilder.append(tableColumnInfo.getTableName());
			sqlBuilder.append(" ( ");
			List<String> columns = tableColumnInfo.getColumnNames();
			for (int i = 0, j = columns.size(); i < j; i++) {
				sqlBuilder.append(columns.get(i));
				if (i != columns.size() - 1) {
					sqlBuilder.append(",");
				}
			}
			sqlBuilder.append(" ) values ( ");
			List<String> propertyNames = tableColumnInfo.getPropertyNames();
			for (int i = 0, j = propertyNames.size(); i < j; i++) {
				sqlBuilder.append(bindParameter("record." + propertyNames.get(i)));
				if (i != propertyNames.size() - 1) {
					sqlBuilder.append(",");
				}
			}
			sqlBuilder.append(" ) ");
			return sqlBuilder.toString();
		}));
	}

	public CharSequence insertSelective(Map<String, Object> params, ProviderContext context) {
		return executeSql(context, (tableColumnInfo -> {
			StringBuilder sqlBuilder = new StringBuilder("<script> insert into ");
			sqlBuilder.append(tableColumnInfo.getTableName());

			StringBuilder columnBuilder = new StringBuilder();
			StringBuilder valueBuilder = new StringBuilder();
			List<String> columns = tableColumnInfo.getColumnNames();
			List<String> propertys = tableColumnInfo.getPropertyNames();
			for (int i = 0, j = propertys.size(); i < j; i++) {
				columnBuilder.append("<if test=\"record." + propertys.get(i) + "!=null\">");
				columnBuilder.append(columns.get(i));
				columnBuilder.append(",");
				columnBuilder.append("</if>");
				valueBuilder.append(" <if test=\"record." + propertys.get(i) + "!=null\">");
				valueBuilder.append(bindParameter("record." + propertys.get(i)));
				valueBuilder.append(",");
				valueBuilder.append(" </if> ");
			}
			return sqlBuilder.append(" <trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">").append(columnBuilder)
					.append(" </trim> ").append(" <trim prefix=\"values (\" suffix=\")\" suffixOverrides=\",\">")
					.append(valueBuilder).append(" </trim></script>").toString();
		}));
	}

	public CharSequence insertBatch(Map<String, Object> params, ProviderContext context) {
		return executeSql(context, (tableColumnInfo -> {
			StringBuilder sqlBuilder = new StringBuilder("<script> insert into ").append(tableColumnInfo.getTableName())
					.append(" ( ");
			List<String> columns = tableColumnInfo.getColumnNames();
			List<String> propertys = tableColumnInfo.getPropertyNames();
			StringBuilder valueBuilder = new StringBuilder();
			valueBuilder.append("(");
			for (int i = 0, j = columns.size(); i < j; i++) {
				sqlBuilder.append(columns.get(i));
				valueBuilder.append(bindParameter("item." + propertys.get(i)));
				if (i != columns.size() - 1) {
					sqlBuilder.append(",");
					valueBuilder.append(",");
				}
			}
			valueBuilder.append(")");
			return sqlBuilder.append(" ) values ").append(
					"<foreach collection=\"list\" index=\"index\" item=\"item\" open=\"\" separator=\",\" close=\"\">")
					.append(valueBuilder).append("</foreach> </script>").toString();
		}));
	}

	public CharSequence insertOracleBatch(Map<String, Object> params, ProviderContext context) {
		return executeSql(context, (tableColumnInfo -> {
			StringBuilder singleSqlBuilder = new StringBuilder(" insert into ").append(tableColumnInfo.getTableName())
					.append(" ( ");
			List<String> columns = tableColumnInfo.getColumnNames();
			List<String> propertys = tableColumnInfo.getPropertyNames();
			StringBuilder valueBuilder = new StringBuilder();
			for (int i = 0, j = columns.size(); i < j; i++) {
				singleSqlBuilder.append(columns.get(i));
				valueBuilder.append(bindParameter("item." + propertys.get(i)));
				if (i != columns.size() - 1) {
					singleSqlBuilder.append(",");
					valueBuilder.append(",");
				}
			}
			singleSqlBuilder.append(" ) values ( ").append(valueBuilder).append(" ) ");

			return new StringBuilder("<script> begin; ").append(
					"<foreach collection=\"list\" index=\"index\" item=\"item\" open=\"\" separator=\",\" close=\"\">")
					.append(singleSqlBuilder).append("</foreach>; end </script>").toString();
		}));
	}

}
