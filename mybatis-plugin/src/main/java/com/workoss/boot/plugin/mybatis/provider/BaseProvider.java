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
package com.workoss.boot.plugin.mybatis.provider;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import com.workoss.boot.plugin.mybatis.CrudDao;
import com.workoss.boot.util.StringUtils;
import com.workoss.boot.util.collection.CollectionUtils;
import org.apache.ibatis.builder.annotation.ProviderContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class BaseProvider {
    private static final Logger log = LoggerFactory.getLogger(BaseProvider.class);

    private static final Map<String, String> SQL_MAP = new ConcurrentHashMap<>();


    public String executeSql(ProviderContext context, SqlConsumer sqlConsumer) {
        String key = getSqlKey(context);
        String sql = SQL_MAP.get(key);
        if (sql != null) {
            return sql;
        }
        sql = sqlConsumer.sqlCommand(getTableColumnInfo(context));
        if (StringUtils.isEmpty(sql)) {
            throw new RuntimeException(key + " 获取sql失败");
        }
        SQL_MAP.put(key, sql);
        log.debug("mybatis dao:{} 生成sql:{}", key, sql);
        return sql;
    }


    private TableColumnInfo getTableColumnInfo(ProviderContext context) {
        Class clazz = entityType(context);
        TableColumnInfo tableColumnInfo = new TableColumnInfo();
        tableColumnInfo.setTableName(getTableName(clazz));
        Field[] fields = clazz.getDeclaredFields();
        Stream.of(fields)
                .filter(field -> !field.isAnnotationPresent(Transient.class))
                .forEach(field -> {
                    String columnName = StringUtils.underscoreName(field.getName());
                    Annotation[] annotations = field.getAnnotations();
                    for (Annotation annotation : annotations) {
                        if (annotation instanceof Column) {
                            //自定义列名称
                            Column column = (Column) annotation;
                            if (StringUtils.isNotBlank(column.name())) {
                                columnName = column.name();
                            }
                        }
                        if (annotation instanceof Id) {
                            tableColumnInfo.setIdPropertyName(field.getName());
                            tableColumnInfo.setIdColumnName(columnName);
                        }
                    }
                    tableColumnInfo.addColumnName(columnName);
                    tableColumnInfo.addPropertyName(field.getName());
                    tableColumnInfo.addPropertyType(field.getType());
                });
        return tableColumnInfo;
    }

    protected StringBuilder getSelectColumn(TableColumnInfo tableColumnInfo) {
        StringBuilder sqlBuilder = new StringBuilder();
        List<String> columnNames = tableColumnInfo.getColumnNames();
        for (int i = 0, j = columnNames.size(); i < j; i++) {
            sqlBuilder.append(columnNames.get(i) + " as " + tableColumnInfo.getPropertyNames().get(i));
            if (i != j - 1) {
                sqlBuilder.append(",");
            }
        }
        return sqlBuilder;
    }

    protected StringBuilder getWhereSelectColumn(TableColumnInfo tableColumnInfo) {
        StringBuilder sqlBuilder = new StringBuilder();
        List<String> columnNames = tableColumnInfo.getColumnNames();
        if (CollectionUtils.isEmpty(columnNames)) {
            return sqlBuilder;
        }
        sqlBuilder.append(" <where> ");
        for (int i = 0, j = columnNames.size(); i < j; i++) {
            sqlBuilder.append(" <if test=\"record." + tableColumnInfo.getPropertyNames().get(i) + "!=null\"> ");
            sqlBuilder.append(" and ");
            sqlBuilder.append(tableColumnInfo.getColumnNames().get(i));
            sqlBuilder.append("=");
            sqlBuilder.append(bindParameter("record." + tableColumnInfo.getPropertyNames().get(i)));
            sqlBuilder.append(" </if> ");
        }
        sqlBuilder.append(" </where> ");
        return sqlBuilder;
    }

    protected String bindParameter(String property) {
        return "#{" + property + "}";
    }


    private String getTableName(Class clazz) {
        Table table = (Table) clazz.getAnnotation(Table.class);
        if (table != null) {
            if (StringUtils.isNotBlank(table.name())) {
                return table.name();
            }
        }
        return StringUtils.underscoreName(clazz.getSimpleName().replaceAll("Entity",""));
    }


    private String getSqlKey(ProviderContext context) {
        return context.getMapperType().getName() + "." + context.getMapperMethod().getName();
    }

    /**
     * 获取BaseMapper接口中的泛型类型
     *
     * @param context
     * @return
     */
    protected Class<?> entityType(ProviderContext context) {
        return Stream.of(context.getMapperType().getGenericInterfaces())
                .filter(ParameterizedType.class::isInstance)
                .map(ParameterizedType.class::cast)
                .filter(type -> type.getRawType() == CrudDao.class)
                .findFirst()
                .map(type -> type.getActualTypeArguments()[0])
                .filter(Class.class::isInstance).map(Class.class::cast)
                .orElseThrow(() -> new IllegalStateException("未找到BaseMapper的泛型类 " + context.getMapperType().getName() + "."));
    }


}
