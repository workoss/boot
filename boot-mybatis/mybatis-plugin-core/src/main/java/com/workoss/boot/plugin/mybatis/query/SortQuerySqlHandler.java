/*
 * Copyright 2019-2024 workoss (https://www.workoss.com)
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
package com.workoss.boot.plugin.mybatis.query;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.builder.SQLBuilderFactory;
import com.workoss.boot.plugin.mybatis.MybatisUtil;
import com.workoss.boot.plugin.mybatis.SqlHandler;
import com.workoss.boot.plugin.mybatis.SqlRequest;
import com.workoss.boot.plugin.mybatis.context.SqlContext;
import com.workoss.boot.util.StringUtils;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 排序拦截器
 *
 * @author workoss
 */
public class SortQuerySqlHandler implements SqlHandler {

	private static final Logger log = LoggerFactory.getLogger(SortQuerySqlHandler.class);

	private final String ORDER_QUERY_MAIN = "order";

	private final String ORDER_QUERY_BY = "by";

	private final String ORDER_BY_SEPERATE = ",";

	@Override
	public void handler(SqlContext context) {
		BoundSql boundSql = (BoundSql) context.getOutputOrInput("boundSql");
		String sql = boundSql.getSql();
		if (sql.toLowerCase().contains(ORDER_QUERY_MAIN) && sql.toLowerCase().contains(ORDER_QUERY_BY)) {
			log.debug("sql have order by ，ignore orderBy");
			return;
		}
		SqlRequest sqlRequest = (SqlRequest) context.getInput("sqlParam");
		DbType dbType = (DbType) context.getInput("dbType");
		String orderBy = sqlRequest.getSortBy();
		if (StringUtils.isBlank(orderBy)) {
			return;
		}
		String newSql = SQLBuilderFactory.createSelectSQLBuilder(sql, dbType)
			.orderBy(orderBy.split(ORDER_BY_SEPERATE))
			.toString();
		MappedStatement mappedStatement = (MappedStatement) context.getInput("mappedStatement");
		boundSql = MybatisUtil.newBoundSql(mappedStatement, newSql, boundSql);
		context.putOutput("boundSql", boundSql);
		context.putOutput("change", true);
	}

}
