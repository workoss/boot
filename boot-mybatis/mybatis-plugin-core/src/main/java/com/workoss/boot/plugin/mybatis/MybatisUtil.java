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
package com.workoss.boot.plugin.mybatis;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.PagerUtils;
import com.alibaba.druid.util.JdbcUtils;
import com.workoss.boot.util.json.JsonMapper;
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
            return getDbType(url);
        } catch (SQLException e) {
            log.warn("根据数据库连接url:{} 获取不到dbType,请在插件中手动配置,错误 {}:{} ", url, e.getErrorCode(), e.getMessage());
        }
        return DbType.mysql;
    }

    public static DbType getDbType(String rawUrl) {
        if (rawUrl.startsWith("r2dbc:")) {
            rawUrl = rawUrl.replaceFirst("r2dbc:", "jdbc:");
        }
        DbType dbType = JdbcUtils.getDbTypeRaw(rawUrl, null);
        if (dbType != null) {
            return dbType;
        }
        if (rawUrl.startsWith("jdbc:es:") || rawUrl.startsWith("jdbc:opensearch:")) {
            return DbType.elastic_search;
        }
        return DbType.mysql;
    }

    public static BoundSql newBoundSql(MappedStatement mappedStatement, String sql, BoundSql boundSql) {
        BoundSql newBoundSql = new BoundSql(mappedStatement.getConfiguration(), sql, boundSql.getParameterMappings(),
                boundSql.getParameterObject());
        boundSql.getAdditionalParameters().forEach(newBoundSql::setAdditionalParameter);
        return newBoundSql;
    }

    public static Long count(DbType dbType, Executor executor, MappedStatement mappedStatement, Object parameter,
                             RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) {
        String countMsId = mappedStatement.getId() + countSuffix;
        // 判断是否存在count
        MappedStatement countMappedStatement = null;
        try {
            countMappedStatement = mappedStatement.getConfiguration().getMappedStatement(countMsId, false);
        } catch (Exception e) {

        }
        BoundSql countBoundSql;

        if (countMappedStatement != null) {
            countBoundSql = countMappedStatement.getBoundSql(parameter);
        } else {
            countMappedStatement = COUNT_MS_MAPPEDSTATEMENT_CACHE.get(countMsId);
            if (countMappedStatement == null) {
                countMappedStatement = newCountMappedStatement(mappedStatement, countMsId);
                COUNT_MS_MAPPEDSTATEMENT_CACHE.put(countMsId, countMappedStatement);
            }
            String countSql = PagerUtils.count(boundSql.getSql(), dbType);
            countBoundSql = new BoundSql(countMappedStatement.getConfiguration(), countSql,
                    boundSql.getParameterMappings(), parameter);
            boundSql.getAdditionalParameters().forEach(countBoundSql::setAdditionalParameter);
        }

        CacheKey countKey = executor.createCacheKey(countMappedStatement, parameter, rowBounds, countBoundSql);
        try {
            List list = executor.query(countMappedStatement, parameter, RowBounds.DEFAULT, resultHandler, countKey,
                    countBoundSql);
            if (!(list != null && list.size() > 0)) {
                return 0L;
            }
            return (Long) list.get(0);
        } catch (SQLException sqlException) {
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
