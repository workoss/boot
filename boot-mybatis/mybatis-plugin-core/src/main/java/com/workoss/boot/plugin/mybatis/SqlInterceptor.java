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
package com.workoss.boot.plugin.mybatis;

import com.alibaba.druid.DbType;
import com.workoss.boot.plugin.mybatis.context.SqlContext;
import com.workoss.boot.plugin.mybatis.query.PageQuerySqlHandler;
import com.workoss.boot.plugin.mybatis.query.SortQuerySqlHandler;
import com.workoss.boot.plugin.mybatis.util.ProviderUtil;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Properties;

/**
 * mybatis 拦截器
 *
 * @author workoss
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
@Intercepts({ @Signature(type = Executor.class, method = "update", args = { MappedStatement.class, Object.class }),
		@Signature(type = Executor.class, method = "query",
				args = { MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class }),
		@Signature(type = Executor.class, method = "query", args = { MappedStatement.class, Object.class,
				RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class }), })
public class SqlInterceptor implements Interceptor {

	private static final Logger log = LoggerFactory.getLogger(SqlInterceptor.class);

	private Properties properties;

	public SqlInterceptor() {
		SelectSqlActionExecutor.INSTANCE.addAfter(new SortQuerySqlHandler()).addAfter(new PageQuerySqlHandler());
	}

	public SqlInterceptor addQueryHandlerBefore(SqlHandler sqlHandler) {
		SelectSqlActionExecutor.INSTANCE.addBefore(sqlHandler);
		return this;
	}

	public SqlInterceptor addQueryHandlerAfter(SqlHandler sqlHandler) {
		SelectSqlActionExecutor.INSTANCE.addAfter(sqlHandler);
		return this;
	}

	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		Executor executor = (Executor) invocation.getTarget();
		Object[] args = invocation.getArgs();
		MappedStatement mappedStatement = (MappedStatement) args[0];
		Object parameter = args[1];
		DbType dbType = MybatisUtil.getDbType(executor.getTransaction().getConnection());
		if (dbType != null) {
			ProviderUtil.setDbType(dbType.name());
			if (parameter instanceof Map) {
				((Map) parameter).put("_dbType", dbType.name());
			}
		}
		SqlContext context = new SqlContext();
		context.putInput("dbType", dbType);
		if (properties != null) {
			context.putInput("properties", properties);
		}
		SqlCommandType sqlCommandType = mappedStatement.getSqlCommandType();
		Object result = null;
		try {
			switch (sqlCommandType) {
			case SELECT:
				context.putInput("sqlParam", SqlHelper.getLocalSqlParam());
				result = SelectSqlActionExecutor.INSTANCE.execute(invocation, context);
				break;
			case UPDATE:
				result = UpdateSqlActionExecutor.INSTANCE.execute(invocation, context);
				break;
			default:
				result = invocation.proceed();
				break;
			}
			return result;
		}
		finally {
			SqlHelper.clearSqlParam();
			ProviderUtil.setDbType(null);
		}
	}

	/**
	 * 只拦截Execuate
	 * @param target 插件对象
	 * @return object
	 */
	@Override
	public Object plugin(Object target) {
		if (target instanceof Executor) {
			return Plugin.wrap(target, this);
		}
		else {
			return target;
		}
	}

	@Override
	public void setProperties(Properties properties) {
		this.properties = properties;
	}

}
