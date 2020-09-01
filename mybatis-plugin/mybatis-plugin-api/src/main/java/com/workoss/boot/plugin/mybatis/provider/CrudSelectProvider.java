/*
 * The MIT License
 * Copyright © 2020-2021 workoss
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.workoss.boot.plugin.mybatis.provider;

import java.util.Map;

import org.apache.ibatis.builder.annotation.ProviderContext;

/**
 * CrudSelectProvider
 *
 * @author workoss
 */
public class CrudSelectProvider extends BaseProvider {

	public CharSequence selectById(Map<String, Object> parameter, ProviderContext context) {
		return executeSql(context, (tableColumnInfo -> {
			StringBuilder sqlBuilder = new StringBuilder(" select ");
			sqlBuilder.append(getSelectColumn(tableColumnInfo));
			sqlBuilder.append(" from ");
			sqlBuilder.append(tableColumnInfo.getTableName());
			sqlBuilder.append(" where ");
			sqlBuilder.append(tableColumnInfo.getIdColumnName());
			sqlBuilder.append("=");
			sqlBuilder.append(bindParameter("id"));
			return sqlBuilder.toString();
		}));
	}

	public CharSequence selectByIds(Map<String, Object> parameter, ProviderContext context) {
		return executeSql(context, (tableColumnInfo -> {
			StringBuilder sqlBuilder = new StringBuilder("<script> select ");
			sqlBuilder.append(getSelectColumn(tableColumnInfo));
			sqlBuilder.append(" from ");
			sqlBuilder.append(tableColumnInfo.getTableName());
			sqlBuilder.append(" where ");
			sqlBuilder.append(tableColumnInfo.getIdColumnName());
			sqlBuilder.append(" in (");

			sqlBuilder.append(
					"<foreach collection=\"ids\" index=\"index\" item=\"id\" open=\"\" separator=\",\" close=\"\">");
			sqlBuilder.append(bindParameter("id"));
			sqlBuilder.append("</foreach>");

			sqlBuilder.append(") </script>");
			return sqlBuilder.toString();
		}));
	}

	public CharSequence selectSelective(Map<String, Object> parameter, ProviderContext context) {
		return executeSql(context, (tableColumnInfo -> {
			StringBuilder sqlBuilder = new StringBuilder("<script> select ");
			sqlBuilder.append(getSelectColumn(tableColumnInfo));
			sqlBuilder.append(" from ");
			sqlBuilder.append(tableColumnInfo.getTableName());
			sqlBuilder.append(getWhereSelectColumn(tableColumnInfo));
			sqlBuilder.append(" </script>");
			return sqlBuilder.toString();
		}));
	}

	public CharSequence selectCountSelective(Map<String, Object> parameter, ProviderContext context) {
		return executeSql(context, (tableColumnInfo -> {
			StringBuilder sqlBuilder = new StringBuilder("<script> select ");
			sqlBuilder.append(" count( ");
			sqlBuilder.append(tableColumnInfo.getIdColumnName());
			sqlBuilder.append(" ) ");
			sqlBuilder.append(" from ");
			sqlBuilder.append(tableColumnInfo.getTableName());
			sqlBuilder.append(getWhereSelectColumn(tableColumnInfo));
			sqlBuilder.append(" </script>");
			return sqlBuilder.toString();
		}));
	}

}
