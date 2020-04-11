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

import com.alibaba.fastsql.DbType;
import com.alibaba.fastsql.util.JdbcConstants;

public class JdbcUtil {

    public static DbType getDbType(String rawUrl) {
        if (rawUrl == null) {
            return null;
        }

        if (rawUrl.startsWith("jdbc:derby:") || rawUrl.startsWith("jdbc:log4jdbc:derby:")) {
            return JdbcConstants.DERBY;
        } else if (rawUrl.startsWith("jdbc:mysql:") || rawUrl.startsWith("jdbc:cobar:")
                || rawUrl.startsWith("jdbc:log4jdbc:mysql:")) {
            return JdbcConstants.MYSQL;
        } else if (rawUrl.startsWith("jdbc:mariadb:")) {
            return JdbcConstants.MARIADB;
        } else if (rawUrl.startsWith("jdbc:oracle:") || rawUrl.startsWith("jdbc:log4jdbc:oracle:")) {
            return JdbcConstants.ORACLE;
        } else if (rawUrl.startsWith("jdbc:alibaba:oracle:")) {
            return JdbcConstants.ALI_ORACLE;
        } else if (rawUrl.startsWith("jdbc:microsoft:") || rawUrl.startsWith("jdbc:log4jdbc:microsoft:")) {
            return JdbcConstants.SQL_SERVER;
        } else if (rawUrl.startsWith("jdbc:sqlserver:") || rawUrl.startsWith("jdbc:log4jdbc:sqlserver:")) {
            return JdbcConstants.SQL_SERVER;
        } else if (rawUrl.startsWith("jdbc:jtds:") || rawUrl.startsWith("jdbc:log4jdbc:jtds:")) {
            return JdbcConstants.JTDS;
        }  else if (rawUrl.startsWith("jdbc:postgresql:") || rawUrl.startsWith("jdbc:log4jdbc:postgresql:")) {
            return JdbcConstants.POSTGRESQL;
        } else if (rawUrl.startsWith("jdbc:edb:")) {
            return JdbcConstants.ENTERPRISEDB;
        } else if (rawUrl.startsWith("jdbc:hsqldb:") || rawUrl.startsWith("jdbc:log4jdbc:hsqldb:")) {
            return JdbcConstants.HSQL;
        } else if (rawUrl.startsWith("jdbc:odps:")) {
            return JdbcConstants.ODPS;
        } else if (rawUrl.startsWith("jdbc:db2:")) {
            return JdbcConstants.DB2;
        } else if (rawUrl.startsWith("jdbc:sqlite:")) {
            return JdbcConstants.SQLITE;
        } else if (rawUrl.startsWith("jdbc:h2:") || rawUrl.startsWith("jdbc:log4jdbc:h2:")) {
            return JdbcConstants.H2;
        }  else if (rawUrl.startsWith("jdbc:dm:")) {
            return JdbcConstants.DM;
        } else if (rawUrl.startsWith("jdbc:kingbase:")) {
            return JdbcConstants.KINGBASE;
        } else if (rawUrl.startsWith("jdbc:gbase:")) {
            return JdbcConstants.GBASE;
        }  else if (rawUrl.startsWith("jdbc:hive:")) {
            return JdbcConstants.HIVE;
        } else if (rawUrl.startsWith("jdbc:hive2:")) {
            return JdbcConstants. HIVE;
        } else if (rawUrl.startsWith("jdbc:phoenix:")) {
            return JdbcConstants.PHOENIX;
        } else if (rawUrl.startsWith("jdbc:elastic:")) {
            return JdbcConstants.ELASTIC_SEARCH;
        } else if (rawUrl.startsWith("jdbc:clickhouse:")) {
            return JdbcConstants.CLICKHOUSE;
        }else if (rawUrl.startsWith("jdbc:presto:")) {
            return JdbcConstants.PRESTO;
        } else {
            return null;
        }
    }
}
