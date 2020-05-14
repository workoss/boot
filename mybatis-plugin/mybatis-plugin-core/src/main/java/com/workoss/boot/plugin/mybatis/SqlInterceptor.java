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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import com.alibaba.fastsql.DbType;
import com.alibaba.fastsql.sql.PagerUtils;
import com.alibaba.fastsql.sql.builder.SQLBuilderFactory;
import com.alibaba.fastsql.sql.builder.SQLSelectBuilder;
import com.workoss.boot.util.StringUtils;
import com.workoss.boot.util.reflect.ReflectUtils;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.plugin.*;
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
public class SqlInterceptor implements Interceptor {
    public static final Logger log = LoggerFactory.getLogger(SqlInterceptor.class);
    protected static final Map<String, MappedStatement> COUNT_MS_MAPPEDSTATEMENT_CACHE = new ConcurrentHashMap<>();
    private static final List<ResultMapping> EMPTY_RESULTMAPPING = new ArrayList<ResultMapping>(0);
    private static DbType dbType;
    private static String pageSqlId;
    private final String ORDER_QUERY_MAIN = "order";
    private final String ORDER_QUERY_BY = "by";
    private final String ORDER_BY_SEPERATE = ",";
    private final String PAGE_SQL_ID = "Page";
    private final String PAGE_PARAM = "page";
    private final String MYBATIS_ADDITIONALPARAMTERS = "additionalParameters";
    private String countSuffix = "_COUNT";


    @Override
    public Object intercept(Invocation invocation) throws Throwable {
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
        } else {
            cacheKey = (CacheKey) args[4];
            boundSql = (BoundSql) args[5];
        }

        SqlParam sqlParam = SqlHelper.getLocalSqlParam();
        if (sqlParam == null && mappedStatement.getId().contains(pageSqlId)) {
            sqlParam = initPage(parameter);
        }
        SqlHelper.clearSqlParam();
        if (sqlParam == null) {
            return invocation.proceed();
        }
        initDbType(executor.getTransaction().getConnection());
        // 排序查询
        String sql = boundSql.getSql();
        boolean changeSql = false;
        if (sql.toLowerCase().contains(ORDER_QUERY_MAIN) && sql.toLowerCase().contains(ORDER_QUERY_BY)) {
            log.debug("sql have order by ，ignore page.orderBy");
        } else {
            String orderBy = sqlParam.getSortBy();
            if (StringUtils.isNotBlank(orderBy)) {
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
                Long count = count(executor, mappedStatement, parameter, rowBounds, resultHandler, boundSql);
                pageResult.setCount(count.intValue());
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
        Object additionalParamters = ReflectUtils.getFieldValue(boundSql, MYBATIS_ADDITIONALPARAMTERS);
        if (additionalParamters != null) {
            ReflectUtils.setFieldValue(newBoundSql, MYBATIS_ADDITIONALPARAMTERS,
                    (Map<String, Object>) additionalParamters);
        }
        List<Object> list = executor.query(mappedStatement, parameter, RowBounds.DEFAULT, resultHandler, cacheKey,
                boundSql);

        if (sqlParam.getShouldPage()) {
            pageResult.addAll(list);
            return pageResult;
        }
        return list;
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
        } else {
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
            try {
                String url = connection.getMetaData().getURL();
                dbType = JdbcUtil.getDbType(url);
            } catch (SQLException e) {
                log.error("根据数据库连接url:{} 获取不到dbType,请在插件中手动配置,错误 ", e);
            }
        }
    }

    private SqlParam initPage(Object parameterObject) {
        SqlParam page = null;
        if (parameterObject instanceof SqlParam) {
            page = (SqlParam) parameterObject;
        } else if (parameterObject instanceof Map) {
            Map<String, Object> paraMap = (Map<String, Object>) parameterObject;
            if (paraMap.containsKey(PAGE_PARAM)) {
                page = (SqlParam) paraMap.get(PAGE_PARAM);
            } else {
                Optional<SqlParam> optional = paraMap.entrySet().stream()
                        .filter(entry -> entry.getValue() instanceof SqlParam)
                        .map(entry -> (SqlParam) entry.getValue())
                        .findFirst();
                page = optional.isPresent() ? optional.get() : null;
            }
        } else {
            log.warn("入参没有分页相关数据");
        }
        return page;
    }

    private Long count(Executor executor, MappedStatement mappedStatement, Object parameter,
                       RowBounds rowBounds, ResultHandler resultHandler,
                       BoundSql boundSql) throws SQLException {
        String countMsId = mappedStatement.getId() + countSuffix;
        //判断是否存在count
        MappedStatement countMappedStatement = mappedStatement.getConfiguration()
                .getMappedStatement(countMsId, false);
        CacheKey countKey = executor.createCacheKey(mappedStatement, parameter, rowBounds,
                boundSql);
        BoundSql countBoundSql;
        //生成 count
        if (countMappedStatement == null) {
            countMappedStatement = COUNT_MS_MAPPEDSTATEMENT_CACHE.get(countMsId);
            if (countMappedStatement == null) {
                countMappedStatement = newCountMappedStatement(mappedStatement, countMsId);
                COUNT_MS_MAPPEDSTATEMENT_CACHE.put(countMsId, mappedStatement);
            }

            String countSql = PagerUtils.count(boundSql.getSql(), dbType);
            countBoundSql = new BoundSql(countMappedStatement.getConfiguration(),
                    countSql, boundSql.getParameterMappings(), parameter);
            //当使用动态 SQL 时，可能会产生临时的参数，这些参数需要手动设置到新的 BoundSql 中
            Object additionalParamters = ReflectUtils
                    .getFieldValue(boundSql, MYBATIS_ADDITIONALPARAMTERS);
            if (additionalParamters != null) {
                ReflectUtils.setFieldValue(countBoundSql, MYBATIS_ADDITIONALPARAMTERS,
                        (Map<String, Object>) additionalParamters);
            }
        } else {
            countBoundSql = countMappedStatement.getBoundSql(parameter);
        }
        List list = executor.query(countMappedStatement, parameter, RowBounds.DEFAULT,
                resultHandler, countKey, countBoundSql);
        if (!(list != null && list.size() > 0)) {
            return 0L;
        }
        return (Long) list.get(0);
    }


    private MappedStatement newCountMappedStatement(MappedStatement ms, String newMsId) {
        MappedStatement.Builder builder = new MappedStatement.Builder(ms
                .getConfiguration(), newMsId, ms.getSqlSource(), ms.getSqlCommandType());
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
        //count查询返回值int
        List<ResultMap> resultMaps = new ArrayList<ResultMap>();
        ResultMap resultMap = new ResultMap.Builder(ms.getConfiguration(), ms
                .getId(), Long.class, EMPTY_RESULTMAPPING).build();
        resultMaps.add(resultMap);
        builder.resultMaps(resultMaps);
        builder.resultSetType(ms.getResultSetType());
        builder.cache(ms.getCache());
        builder.flushCacheRequired(ms.isFlushCacheRequired());
        builder.useCache(ms.isUseCache());

        return builder.build();
    }

}
