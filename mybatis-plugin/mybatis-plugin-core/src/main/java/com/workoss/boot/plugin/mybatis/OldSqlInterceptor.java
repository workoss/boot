/*
 * #%L
 * %%
 * Copyright (C) 2019 Workoss Software, Inc.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *      http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package com.workoss.boot.plugin.mybatis;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.alibaba.fastsql.DbType;
import com.alibaba.fastsql.sql.PagerUtils;
import com.alibaba.fastsql.sql.builder.SQLBuilderFactory;
import com.alibaba.fastsql.sql.builder.SQLSelectBuilder;
import com.workoss.boot.util.reflect.ReflectUtils;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.jdbc.ConnectionLogger;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.MappedStatement.Builder;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.scripting.defaults.DefaultParameterHandler;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * mybatis 拦截器
 *
 * @author : luanfeng
 * @date: 2017/8/11 8:10
 * @version: 1.0.0
 */
@SuppressWarnings({"unchecked", "rawtypes"})
@Intercepts({
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class,
		        RowBounds.class, ResultHandler.class}),
		@Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class,
                RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}),
})
public class OldSqlInterceptor implements Interceptor {
	public static final Logger log = LoggerFactory.getLogger(SqlInterceptor.class);
	private static DbType dbType;
	private static String pageSqlId;

	private final String ORDER_QUERY_MAIN = "order";
	private final String ORDER_QUERY_BY = "by";
	private final String ORDER_BY_SEPERATE = ",";
	private final String PAGE_SQL_ID = "Page";
	private final String PAGE_PARAM = "page";
	private final String MYBATIS_METAPARAMETERS = "metaParameters";

	private final String MYBATIS_ADDITIONALPARAMTERS = "additionalParameters";


	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		Executor executor = (Executor) invocation.getTarget();
        Object[] args = invocation.getArgs();
		MappedStatement mappedStatement = (MappedStatement) args[0];
		Object param = args[1];
		BoundSql boundSql;

		if (args.length==4){
            boundSql = mappedStatement.getBoundSql(param);
        }else{
            boundSql = (BoundSql) args[5];
        }


		SqlParam sqlParam = SqlHelper.getLocalSqlParam();
		if (sqlParam == null) {
			if (mappedStatement.getId().contains(pageSqlId)) {
				sqlParam = initPage(param);
			}
		}
		SqlHelper.clearSqlParam();
		if (sqlParam == null) {
			return invocation.proceed();
		}
		// 排序查询
		String sql = boundSql.getSql();
		boolean changeSql = false;
		if (sql.toLowerCase().contains(ORDER_QUERY_MAIN)
                && sql.toLowerCase().contains(ORDER_QUERY_BY)) {
			log.debug("sql have order by ，ignore page.orderBy");
		}else {
			String orderBy = sqlParam.getSortBy();
			if (!(orderBy == null || orderBy.length() == 0)) {
				SQLSelectBuilder builder = SQLBuilderFactory.createSelectSQLBuilder(sql, dbType);
				builder.orderBy(orderBy.split(ORDER_BY_SEPERATE));
				sql = builder.toString();
				changeSql = true;
			}
		}
		PageResult pageResult = null;
		if (sqlParam.getShouldPage()) {
			pageResult = new PageResult();
			pageResult.setOffset(sqlParam.getOffset());
			pageResult.setLimit(sqlParam.getLimit());
			pageResult.setSortBy(sqlParam.getSortBy());
			if (sqlParam.getShouldCount()) {
				Connection connection = executor.getTransaction().getConnection();
				initDbType(connection);
				Log statementLog = mappedStatement.getStatementLog();
				if (statementLog.isDebugEnabled()) {
					connection = ConnectionLogger
							.newInstance(connection, statementLog, 0);
				}

				String countSql = PagerUtils.count(boundSql.getSql(), dbType);
				int count = getPageTotal(mappedStatement, connection, countSql, boundSql);
				pageResult.setCount(count);
				if (count <= 0) {
					return pageResult;
				}
			}
			sql = PagerUtils.limit(sql, dbType, sqlParam.getOffset(), sqlParam.getLimit());
			changeSql = true;
		}


		if (!changeSql) {
			return invocation.proceed();
		}

		BoundSql newBoundSql = new BoundSql(mappedStatement.getConfiguration(), sql,
				boundSql.getParameterMappings(), boundSql.getParameterObject());
		// 解决MyBatis 分页foreach 参数失效 start
		Object metaObject = ReflectUtils.getFieldValue(boundSql, MYBATIS_METAPARAMETERS);
		if (metaObject != null) {
			ReflectUtils.setFieldValue(newBoundSql, MYBATIS_METAPARAMETERS,
					(MetaObject) metaObject);
		}
		//解决MyBatis 分页foreach 参数失效 end
		Object additionalParamters = ReflectUtils
				.getFieldValue(boundSql, MYBATIS_ADDITIONALPARAMTERS);
		if (additionalParamters != null) {
			ReflectUtils.setFieldValue(newBoundSql, MYBATIS_ADDITIONALPARAMTERS,
                    (Map<String, Object>) additionalParamters);
		}
		MappedStatement newMs = copyFromMappedStatement(mappedStatement,
                new BoundSqlSqlSource(newBoundSql));
		invocation.getArgs()[0] = newMs;


		if (sqlParam.getShouldPage()) {
			invocation.getArgs()[2] = new RowBounds(RowBounds.NO_ROW_OFFSET, RowBounds.NO_ROW_LIMIT);
			Object result = invocation.proceed();
			pageResult.addAll((List) result);
			return pageResult;
		}

		return invocation.proceed();

	}

	/**
	 * 只拦截Execuate
	 *
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
		if (dbType == null) {
			dbType = DbType.valueOf(properties.getProperty("dbType"));
		}
		if (pageSqlId == null) {
			pageSqlId = properties.getProperty("pageSqlId", PAGE_SQL_ID);
		}
	}

	private void initDbType(Connection connection) {
		if (dbType == null) {
			String url;
			try {
				url = connection.getMetaData().getURL();
				dbType = JdbcUtil.getDbType(url);

			}
			catch (SQLException e) {
				log.error("根据数据库连接url:{} 获取不到dbType,请在插件中手动配置,错误 ", e);
			}
		}
	}

	private SqlParam initPage(Object parameterObject) {
		SqlParam page = null;
		if (parameterObject instanceof SqlParam) {
			page = (SqlParam) parameterObject;
		}
		else if (parameterObject instanceof Map) {
			Map<String, Object> paraMap = (Map<String, Object>) parameterObject;
			for (String key : paraMap.keySet()) {
				if (PAGE_PARAM.equals(key)) {
					page = (SqlParam) paraMap.get(key);
					break;
				}
				else {
					if (paraMap.get(key) instanceof SqlParam) {
						page = (SqlParam) paraMap.get(key);
						break;
					}
				}
			}
		}
		else {
			log.error("入参没有分页相关数据");
		}
		return page;
	}

	private int getPageTotal(MappedStatement mappedStatement, Connection connection, String countSql,
			BoundSql boundSql) throws SQLException {
		int count = 0;
		PreparedStatement preparedStatement = connection.prepareStatement(countSql);
		DefaultParameterHandler handler = new DefaultParameterHandler(mappedStatement, boundSql
				.getParameterObject(),
				boundSql);
		handler.setParameters(preparedStatement);
		ResultSet rs = preparedStatement.executeQuery();
		if (rs.next()) {
			count = rs.getInt(1);
		}
		rs.close();
		preparedStatement.close();
		return count;
	}

	/**
	 * 复制MappedStatement对象
	 */
	private MappedStatement copyFromMappedStatement(MappedStatement ms, SqlSource newSqlSource) {
		Builder builder = new Builder(ms.getConfiguration(), ms.getId(), newSqlSource, ms
				.getSqlCommandType());
		builder.resource(ms.getResource());
		builder.fetchSize(ms.getFetchSize());
		builder.statementType(ms.getStatementType());
		builder.keyGenerator(ms.getKeyGenerator());
		if (ms.getKeyProperties() != null) {
			for (String keyProperty : ms.getKeyProperties()) {
				builder.keyProperty(keyProperty);
			}
		}
		builder.timeout(ms.getTimeout());
		builder.parameterMap(ms.getParameterMap());
		builder.resultMaps(ms.getResultMaps());
		builder.resultSetType(ms.getResultSetType());
		builder.useCache(ms.isUseCache());
		builder.cache(ms.getCache());
		builder.flushCacheRequired(ms.isFlushCacheRequired());

		return builder.build();
	}

	public static class BoundSqlSqlSource implements SqlSource {
		BoundSql boundSql;

		BoundSqlSqlSource(BoundSql boundSql) {
			this.boundSql = boundSql;
		}

		@Override
		public BoundSql getBoundSql(Object parameterObject) {
			return boundSql;
		}
	}

}
