/*
 * Copyright 2019-2023 workoss (https://www.workoss.com)
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
import com.alibaba.druid.sql.PagerUtils;
import com.workoss.boot.plugin.mybatis.MybatisUtil;
import com.workoss.boot.plugin.mybatis.PageResult;
import com.workoss.boot.plugin.mybatis.SqlHandler;
import com.workoss.boot.plugin.mybatis.SqlParam;
import com.workoss.boot.plugin.mybatis.context.SqlContext;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 分页拦截器
 *
 * @author workoss
 */
public class PageQuerySqlHandler implements SqlHandler {

	private static final Logger log = LoggerFactory.getLogger(PageQuerySqlHandler.class);

	@Override
	public void handler(SqlContext context) {
		SqlParam sqlParam = (SqlParam) context.getInput("sqlParam");
		DbType dbType = (DbType) context.getInput("dbType");
		if (!sqlParam.getShouldPage()) {
			return;
		}
		PageResult pageResult = new PageResult();
		pageResult.setOffset(sqlParam.getOffset());
		pageResult.setLimit(sqlParam.getLimit());
		pageResult.setSortBy(sqlParam.getSortBy());
		MappedStatement mappedStatement = (MappedStatement) context.getInput("mappedStatement");
		if (sqlParam.getShouldCount()) {
			Executor executor = (Executor) context.getInput("executor");
			Object parameter = context.getOutputOrInput("parameter");
			ResultHandler resultHandler = (ResultHandler) context.getInput("resultHandler");
			RowBounds rowBounds = (RowBounds) context.getOutputOrInput("rowBounds");
			BoundSql originBoundSql = (BoundSql) context.getInput("boundSql");
			Long count = MybatisUtil.count(dbType, executor, mappedStatement, parameter, rowBounds, resultHandler,
					originBoundSql);
			pageResult.setCount(count.intValue());
			context.putOutput("result", pageResult);
			if (count <= 0) {
				return;
			}
		}
		context.putOutput("change", true);
		BoundSql boundSql = (BoundSql) context.getOutputOrInput("boundSql");
		String sql = boundSql.getSql();
		String pageSql = PagerUtils.limit(sql, dbType, sqlParam.getOffset(), sqlParam.getLimit());
		boundSql = MybatisUtil.newBoundSql(mappedStatement, pageSql, boundSql);
		context.putOutput("boundSql", boundSql);
	}

}
