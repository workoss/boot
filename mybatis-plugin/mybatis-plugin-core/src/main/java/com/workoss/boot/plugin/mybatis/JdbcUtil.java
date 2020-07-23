/*
 * The MIT License
 * Copyright © 2020-2021 workoss
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.workoss.boot.plugin.mybatis;

import com.alibaba.fastsql.DbType;
import com.alibaba.fastsql.util.JdbcConstants;

/**
 * @author workoss
 */
@SuppressWarnings("ALL")
public class JdbcUtil {

	public static DbType getDbType(String rawUrl) {
		if (rawUrl == null) {
			return null;
		}
		if (rawUrl.startsWith("jdbc:derby:") || rawUrl.startsWith("jdbc:log4jdbc:derby:")) {
			return JdbcConstants.DERBY;
		}
		else if (rawUrl.startsWith("jdbc:mysql:") || rawUrl.startsWith("jdbc:cobar:")
				|| rawUrl.startsWith("jdbc:log4jdbc:mysql:")) {
			return JdbcConstants.MYSQL;
		}
		else if (rawUrl.startsWith("jdbc:mariadb:")) {
			return JdbcConstants.MARIADB;
		}
		else if (rawUrl.startsWith("jdbc:oracle:") || rawUrl.startsWith("jdbc:log4jdbc:oracle:")) {
			return JdbcConstants.ORACLE;
		}
		else if (rawUrl.startsWith("jdbc:alibaba:oracle:")) {
			return JdbcConstants.ALI_ORACLE;
		}
		else if (rawUrl.startsWith("jdbc:microsoft:") || rawUrl.startsWith("jdbc:log4jdbc:microsoft:")) {
			return JdbcConstants.SQL_SERVER;
		}
		else if (rawUrl.startsWith("jdbc:sqlserver:") || rawUrl.startsWith("jdbc:log4jdbc:sqlserver:")) {
			return JdbcConstants.SQL_SERVER;
		}
		else if (rawUrl.startsWith("jdbc:jtds:") || rawUrl.startsWith("jdbc:log4jdbc:jtds:")) {
			return JdbcConstants.JTDS;
		}
		else if (rawUrl.startsWith("jdbc:postgresql:") || rawUrl.startsWith("jdbc:log4jdbc:postgresql:")) {
			return JdbcConstants.POSTGRESQL;
		}
		else if (rawUrl.startsWith("jdbc:edb:")) {
			return JdbcConstants.ENTERPRISEDB;
		}
		else if (rawUrl.startsWith("jdbc:hsqldb:") || rawUrl.startsWith("jdbc:log4jdbc:hsqldb:")) {
			return JdbcConstants.HSQL;
		}
		else if (rawUrl.startsWith("jdbc:odps:")) {
			return JdbcConstants.ODPS;
		}
		else if (rawUrl.startsWith("jdbc:db2:")) {
			return JdbcConstants.DB2;
		}
		else if (rawUrl.startsWith("jdbc:sqlite:")) {
			return JdbcConstants.SQLITE;
		}
		else if (rawUrl.startsWith("jdbc:h2:") || rawUrl.startsWith("jdbc:log4jdbc:h2:")) {
			return JdbcConstants.H2;
		}
		else if (rawUrl.startsWith("jdbc:dm:")) {
			return JdbcConstants.DM;
		}
		else if (rawUrl.startsWith("jdbc:kingbase:")) {
			return JdbcConstants.KINGBASE;
		}
		else if (rawUrl.startsWith("jdbc:gbase:")) {
			return JdbcConstants.GBASE;
		}
		else if (rawUrl.startsWith("jdbc:hive:")) {
			return JdbcConstants.HIVE;
		}
		else if (rawUrl.startsWith("jdbc:hive2:")) {
			return JdbcConstants.HIVE;
		}
		else if (rawUrl.startsWith("jdbc:phoenix:")) {
			return JdbcConstants.PHOENIX;
		}
		else if (rawUrl.startsWith("jdbc:elastic:")) {
			return JdbcConstants.ELASTIC_SEARCH;
		}
		else if (rawUrl.startsWith("jdbc:clickhouse:")) {
			return JdbcConstants.CLICKHOUSE;
		}
		else if (rawUrl.startsWith("jdbc:presto:")) {
			return JdbcConstants.PRESTO;
		}
		return null;
	}

}
