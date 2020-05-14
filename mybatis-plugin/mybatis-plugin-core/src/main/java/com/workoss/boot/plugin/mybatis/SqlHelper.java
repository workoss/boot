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


import com.workoss.boot.util.StringUtils;
import com.workoss.boot.util.concurrent.fast.FastThreadLocal;
import com.workoss.boot.util.reflect.ReflectUtils;

public class SqlHelper {

    protected static final FastThreadLocal<SqlParam> LOCAL_SQL_PARAM = new FastThreadLocal<>();

    static void setLocalPage(SqlParam sqlParam) {
        LOCAL_SQL_PARAM.set(sqlParam);
    }

    public static SqlParam getLocalSqlParam() {
        return LOCAL_SQL_PARAM.get();
    }

    public static void clearSqlParam() {
        LOCAL_SQL_PARAM.set(null);
    }


    public static SqlParamBuild page(int offset, int limit) {
        return new SqlParamBuild(offset, limit, null, true, true);
    }

    public static SqlParamBuild page(int offset, int limit, boolean shouldCount) {
        return new SqlParamBuild(offset, limit, null, true, shouldCount);
    }


    public static SqlParamBuild sortOnly(String sortBy) {
        return new SqlParamBuild(sortBy, false, false);
    }

    public static SqlParamBuild startPage() {
        return new SqlParamBuild(true);
    }

    public static SqlParamBuild startPage(Object pageParam) {
        if (pageParam == null) {
            return new SqlParamBuild(true);
        }
        String clazName = pageParam.getClass().getName();
        if (clazName.startsWith("java.lang.") || clazName.startsWith("java.math.")) {
            throw new RuntimeException("please input object");
        }

        return new SqlParamBuild(pageParam);
    }


    public static class SqlParamBuild {

        private SqlParam sqlParam;

        public SqlParamBuild(Object sqlParam) {
            if (sqlParam instanceof SqlParam) {
                this.sqlParam = (SqlParam) sqlParam;
            } else if (sqlParam instanceof Integer) {

            } else {
                instanceSqlParam();
                //反射获取是否有属性  offset limit  shouldCount  sortBy 字段
                Object offset = ReflectUtils.getPropertyByInvokeMethod(sqlParam, "offset");
                Object limit = ReflectUtils.getPropertyByInvokeMethod(sqlParam, "limit");
                if (limit != null) {
                    this.sqlParam.setOffset(offset == null ? 0 : Integer.parseInt(offset.toString()));
                    this.sqlParam.setLimit(limit == null ? 10 : Integer.parseInt(limit.toString()));
                }
                this.sqlParam.setSortBy((String) ReflectUtils.getPropertyByInvokeMethod(sqlParam, "sortBy"));
            }
        }

        public SqlParamBuild(String sortBy, boolean shouldPage, boolean shouldCount) {
            instanceSqlParam();
            if (StringUtils.isNotBlank(sortBy)) {
                this.sqlParam.setSortBy(sortBy);
            }
            this.sqlParam.setShouldCount(shouldCount);
            if (shouldCount) {
                this.sqlParam.setShouldPage(true);
            } else {
                this.sqlParam.setShouldPage(shouldPage);
            }
        }

        public SqlParamBuild(int offset, int limit, String sortBy, boolean shouldPage, boolean shouldCount) {
            instanceSqlParam();
            this.sqlParam.setOffset(offset);
            this.sqlParam.setLimit(limit);
            this.sqlParam.setSortBy(sortBy);
            this.sqlParam.setShouldCount(shouldCount);
            if (shouldCount) {
                this.sqlParam.setShouldPage(true);
            } else {
                this.sqlParam.setShouldPage(shouldPage);
            }
        }


        public SqlParamBuild sortBy(String sortBy) {
            instanceSqlParam();
            this.sqlParam.setSortBy(sortBy);
            return this;
        }


        public SqlParamBuild page(int offset, int limit) {
            instanceSqlParam();
            this.sqlParam.setOffset(offset);
            this.sqlParam.setLimit(limit);
            return this;
        }

        public SqlParamBuild shouldPage(boolean shouldPage) {
            instanceSqlParam();
            this.sqlParam.setShouldPage(shouldPage);
            return this;
        }

        public SqlParamBuild shouldCount(boolean shouldCount) {
            instanceSqlParam();
            if (shouldCount) {
                this.sqlParam.setShouldPage(true);
            }
            this.sqlParam.setShouldCount(shouldCount);
            return this;
        }

        private SqlParam instanceSqlParam() {
            if (sqlParam == null) {
                sqlParam = new SqlParam();
            }
            return sqlParam;
        }

        public SqlParam build() {
            SqlParam sqlParam = instanceSqlParam();
            if (StringUtils.isNotBlank(sqlParam.getSortBy())) {
                sqlParam.setSortBy(StringUtils.underscoreName(sqlParam.getSortBy()));
            }
            if (sqlParam.getLimit() <= 0 || sqlParam.getOffset() < 0) {
                throw new RuntimeException("分页参数 limit >0 offset>=0");
            }
            setLocalPage(sqlParam);
            return sqlParam;
        }
    }
}
