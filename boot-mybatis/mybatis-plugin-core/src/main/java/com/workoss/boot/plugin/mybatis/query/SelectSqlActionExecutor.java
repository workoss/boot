package com.workoss.boot.plugin.mybatis.query;

import com.workoss.boot.plugin.mybatis.PageResult;
import com.workoss.boot.plugin.mybatis.SqlHandler;
import com.workoss.boot.plugin.mybatis.SqlActionExecutor;
import com.workoss.boot.plugin.mybatis.SqlParam;
import com.workoss.boot.plugin.mybatis.context.SqlContext;
import com.workoss.boot.util.reflect.BeanCopierUtil;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class SelectSqlActionExecutor implements SqlActionExecutor {

	private static final Logger log = LoggerFactory.getLogger(SelectSqlActionExecutor.class);

	public static final SelectSqlActionExecutor INSTANCE = new SelectSqlActionExecutor();

	private final String PAGE_SQL_ID = "Page";

	private final String PAGE_PARAM = "page";

	private Map<String, SqlHandler> sqlHandlerMap = new LinkedHashMap<>();

	public SelectSqlActionExecutor add(SqlHandler sqlHandler) {
		String key = sqlHandler.getClass().getSimpleName();
		if (sqlHandlerMap.containsKey(key)) {
			log.warn("[MYBATIS] QuerySqlHandler:{} 新增已经存在，不能重复增加", sqlHandler.getClass().getName());
		}
		else {
			sqlHandlerMap.put(key, sqlHandler);
		}
		return this;
	}

	@Override
	public Object execute(Invocation invocation, SqlContext context) throws Throwable {
		Executor executor = (Executor) invocation.getTarget();
		Object[] args = invocation.getArgs();
		MappedStatement mappedStatement = (MappedStatement) args[0];
		Object parameter = args[1];
		RowBounds rowBounds = (RowBounds) args[2];
		ResultHandler resultHandler = (ResultHandler) args[3];
		CacheKey cacheKey;
		BoundSql boundSql;
		if (args.length == 4) {
			boundSql = mappedStatement.getBoundSql(parameter);
			cacheKey = executor.createCacheKey(mappedStatement, parameter, rowBounds, boundSql);
		}
		else {
			cacheKey = (CacheKey) args[4];
			boundSql = (BoundSql) args[5];
		}
		SqlParam sqlParam = (SqlParam) context.getInput("sqlParam");
		if (sqlParam == null && mappedStatement.getId().contains(PAGE_SQL_ID)) {
			sqlParam = initSqlParam(parameter);
		}
		if (sqlParam == null) {
			return invocation.proceed();
		}
		context.putInput("executor", executor);
		context.putInput("mappedStatement", mappedStatement);
		context.putInput("parameter", parameter);
		context.putInput("rowBounds", rowBounds);
		context.putInput("resultHandler", resultHandler);
		context.putInput("cacheKey", cacheKey);
		context.putInput("boundSql", boundSql);

		sqlHandlerMap.entrySet().stream().forEach(stringQuerySqlHandlerEntry -> {
			stringQuerySqlHandlerEntry.getValue().handler(context);
		});

		Boolean change = (Boolean) context.getOutput("change");
		if (!(change != null && Boolean.TRUE.compareTo(change) == 0)) {
			return invocation.proceed();
		}
		Object result = context.getOutput("result");
		List<Object> list = executor.query((MappedStatement) context.getOutputOrInput("mappedStatement"),
				context.getOutputOrInput("parameter"), (RowBounds) context.getOutputOrInput("rowBounds"),
				(ResultHandler) context.getOutputOrInput("resultHandler"),
				(CacheKey) context.getOutputOrInput("cacheKey"), (BoundSql) context.getOutputOrInput("boundSql"));
		if (result instanceof PageResult) {
			PageResult pageResult = (PageResult) result;
			pageResult.addAll(list);
			return pageResult;
		}
		return list;
	}

	private SqlParam initSqlParam(Object parameterObject) {
		if (parameterObject instanceof SqlParam) {
			return (SqlParam) parameterObject;
		}
		if (parameterObject instanceof Map) {
			Map<String, Object> paraMap = (Map<String, Object>) parameterObject;
			if (paraMap.containsKey(PAGE_PARAM)) {
				Object paramObj = paraMap.get(PAGE_PARAM);
				if (paramObj instanceof SqlParam) {
					return (SqlParam) parameterObject;
				}
				return BeanCopierUtil.copy(paramObj, SqlParam.class);
			}
			Optional<SqlParam> optional = paraMap.entrySet().stream()
					.filter(entry -> entry.getValue() instanceof SqlParam).map(entry -> (SqlParam) entry.getValue())
					.findFirst();
			return optional.isPresent() ? optional.get() : null;
		}
		return null;
	}

}
