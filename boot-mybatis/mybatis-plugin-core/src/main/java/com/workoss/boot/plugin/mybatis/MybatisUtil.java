package com.workoss.boot.plugin.mybatis;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.PagerUtils;
import com.alibaba.druid.util.JdbcUtils;
import com.workoss.boot.util.reflect.ReflectUtils;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * mybatis 常用工具类
 *
 * @author workoss
 */
public class MybatisUtil {

	private static final Logger log = LoggerFactory.getLogger(MybatisUtil.class);

	protected static final Map<String, MappedStatement> COUNT_MS_MAPPEDSTATEMENT_CACHE = new ConcurrentHashMap<>();

	public static final List<ResultMapping> EMPTY_RESULTMAPPING = new ArrayList<ResultMapping>(0);

	public static final String MYBATIS_ADDITIONALPARAMTERS = "additionalParameters";

	private static String countSuffix = "Count";

	public static DbType getDbType(Connection connection) {
		String url = null;
		try {
			url = connection.getMetaData().getURL();
			return JdbcUtils.getDbTypeRaw(url, JdbcUtils.getDriverClassName(url));
		}
		catch (SQLException e) {
			log.warn("根据数据库连接url:{} 获取不到dbType,请在插件中手动配置,错误 {}:{} ", e.getErrorCode(), e.getMessage());
		}
		return null;
	}

	public static BoundSql newBoundSql(MappedStatement mappedStatement, String sql, BoundSql boundSql) {
		BoundSql newBoundSql = new BoundSql(mappedStatement.getConfiguration(), sql, boundSql.getParameterMappings(),
				boundSql.getParameterObject());
		Object additionalParamters = ReflectUtils.getFieldValue(boundSql, MybatisUtil.MYBATIS_ADDITIONALPARAMTERS);
		if (additionalParamters != null) {
			ReflectUtils.setFieldValue(newBoundSql, MybatisUtil.MYBATIS_ADDITIONALPARAMTERS,
					(Map<String, Object>) additionalParamters);
		}
		return newBoundSql;
	}

	public static Long count(DbType dbType, Executor executor, MappedStatement mappedStatement, Object parameter,
			RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) {
		String countMsId = mappedStatement.getId() + countSuffix;
		// 判断是否存在count
		MappedStatement countMappedStatement = null;
		try {
			countMappedStatement = mappedStatement.getConfiguration().getMappedStatement(countMsId, false);
		}
		catch (Exception e) {

		}
		BoundSql countBoundSql;

		if (countMappedStatement != null) {
			countBoundSql = countMappedStatement.getBoundSql(parameter);
		}
		else {
			countMappedStatement = COUNT_MS_MAPPEDSTATEMENT_CACHE.get(countMsId);
			if (countMappedStatement == null) {
				countMappedStatement = newCountMappedStatement(mappedStatement, countMsId);
				COUNT_MS_MAPPEDSTATEMENT_CACHE.put(countMsId, countMappedStatement);
			}
			String countSql = PagerUtils.count(boundSql.getSql(), dbType);
			countBoundSql = new BoundSql(countMappedStatement.getConfiguration(), countSql,
					boundSql.getParameterMappings(), parameter);
			// 当使用动态 SQL 时，可能会产生临时的参数，这些参数需要手动设置到新的 BoundSql 中
			Object additionalParamters = ReflectUtils.getFieldValue(boundSql, MYBATIS_ADDITIONALPARAMTERS);
			if (additionalParamters != null) {
				ReflectUtils.setFieldValue(countBoundSql, MYBATIS_ADDITIONALPARAMTERS,
						(Map<String, Object>) additionalParamters);
			}
		}

		CacheKey countKey = executor.createCacheKey(countMappedStatement, parameter, rowBounds, countBoundSql);
		try {
			List list = executor.query(countMappedStatement, parameter, RowBounds.DEFAULT, resultHandler, countKey,
					countBoundSql);
			if (!(list != null && list.size() > 0)) {
				return 0L;
			}
			return (Long) list.get(0);
		}
		catch (SQLException sqlException) {
			throw new RuntimeException("[MYBATIS]COUNT:" + countBoundSql.getSql() + "查询失败", sqlException);
		}
	}

	public static MappedStatement newCountMappedStatement(MappedStatement ms, String newMsId) {
		MappedStatement.Builder builder = new MappedStatement.Builder(ms.getConfiguration(), newMsId, ms.getSqlSource(),
				ms.getSqlCommandType());
		builder.resource(ms.getResource());
		builder.fetchSize(ms.getFetchSize());
		builder.statementType(ms.getStatementType());
		builder.keyGenerator(ms.getKeyGenerator());
		if (ms.getKeyProperties() != null && ms.getKeyProperties().length != 0) {
			StringBuilder keyProperties = new StringBuilder();
			for (String keyProperty : ms.getKeyProperties()) {
				keyProperties.append(keyProperty).append(",");
			}
			keyProperties.delete(keyProperties.length() - 1, keyProperties.length());
			builder.keyProperty(keyProperties.toString());
		}
		builder.timeout(ms.getTimeout());
		builder.parameterMap(ms.getParameterMap());
		// count查询返回值int
		List<ResultMap> resultMaps = new ArrayList<ResultMap>();
		ResultMap resultMap = new ResultMap.Builder(ms.getConfiguration(), ms.getId(), Long.class, EMPTY_RESULTMAPPING)
				.build();
		resultMaps.add(resultMap);
		builder.resultMaps(resultMaps);
		builder.resultSetType(ms.getResultSetType());
		builder.cache(ms.getCache());
		builder.flushCacheRequired(ms.isFlushCacheRequired());
		builder.useCache(ms.isUseCache());

		return builder.build();
	}

}
