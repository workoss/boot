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
package com.workoss.boot.util.plugin.mybatis.provider;

import java.util.Map;

import org.apache.ibatis.builder.annotation.ProviderContext;

public class CrudDeleteProvider extends BaseProvider {

    public CharSequence deleteById(Map<String, Object> params, ProviderContext context) {
        return executeSql(context,(tableColumnInfo -> {
            StringBuilder sqlBuilder = new StringBuilder(" delete from ");
            sqlBuilder.append(tableColumnInfo.getTableName());
            sqlBuilder.append(" where ");
            sqlBuilder.append(tableColumnInfo.getIdColumnName());
            sqlBuilder.append("=");
            sqlBuilder.append(bindParameter(tableColumnInfo.getIdPropertyName()));
            return sqlBuilder.toString();
        }));
    }

    public CharSequence deleteByIds(Map<String, Object> params, ProviderContext context) {
        return executeSql(context,(tableColumnInfo -> {
            StringBuilder sqlBuilder = new StringBuilder("<script> delete from ");
            sqlBuilder.append(tableColumnInfo.getTableName());
            sqlBuilder.append(" where ");
            sqlBuilder.append(tableColumnInfo.getIdColumnName());
            sqlBuilder.append(" in ( ");
            sqlBuilder.append("<foreach collection=\"ids\" index=\"index\" item=\"item\" open=\"\" separator=\",\" close=\"\">");
            sqlBuilder.append(bindParameter("item"));
            sqlBuilder.append("</foreach>");
            sqlBuilder.append(" ) </script>");
            return sqlBuilder.toString();
        }));
    }

    public CharSequence deleteSelective(Map<String, Object> record,ProviderContext context){
        return executeSql(context,(tableColumnInfo -> {
            StringBuilder sqlBuilder = new StringBuilder("<script> delete from ");
            sqlBuilder.append(tableColumnInfo.getTableName());
            sqlBuilder.append(getWhereSelectColumn(tableColumnInfo));
            sqlBuilder.append(" </script>");
            return sqlBuilder.toString();
        }));
    }
}
