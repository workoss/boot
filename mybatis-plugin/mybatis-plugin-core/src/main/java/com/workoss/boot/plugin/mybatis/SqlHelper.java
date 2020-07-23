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

import com.workoss.boot.util.StringUtils;
import com.workoss.boot.util.concurrent.fast.FastThreadLocal;
import com.workoss.boot.util.reflect.ReflectUtils;

/**
 * @author workoss
 */
@SuppressWarnings("ALL")
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

	public static SqlParamBuild onePage(int pageNo, int limit) {
		return onePage(pageNo, limit, true);
	}

	public static SqlParamBuild onePage(int pageNo, int limit, boolean shouldCount) {
		return page((pageNo - 1) * limit, limit, shouldCount);
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
			}
			else if (sqlParam instanceof Integer) {

			}
			else {
				instanceSqlParam();
				// 反射获取是否有属性 offset limit shouldCount sortBy 字段
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
			}
			else {
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
			}
			else {
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
