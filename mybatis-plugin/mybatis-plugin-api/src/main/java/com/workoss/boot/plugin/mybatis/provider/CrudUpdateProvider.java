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

import java.util.List;
import java.util.Map;

import org.apache.ibatis.builder.annotation.ProviderContext;

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
