/*
 * Copyright 2019-2022 workoss (https://www.workoss.com)
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

import com.workoss.boot.util.Assert;
import com.workoss.boot.util.StringUtils;
import com.workoss.boot.util.concurrent.fast.FastThreadLocal;
import com.workoss.boot.util.reflect.ReflectUtils;

import java.util.function.Function;

/**
 * SqlHelper
 *
 * @author workoss
 */
@SuppressWarnings("ALL")
public class SqlHelper {

	protected static final FastThreadLocal<SqlParam> LOCAL_SQL_PARAM = new FastThreadLocal<>();

	public static PageBuilder page() {
		return page(null);
	}

	public static PageBuilder page(Object pageObject) {
		if (pageObject == null) {
			return new PageBuilder();
		}
		String clazName = pageObject.getClass().getName();
		if (clazName.startsWith("java.lang.") || clazName.startsWith("java.math.")) {
			throw new RuntimeException("please input object");
		}
		PageBuilder pageBuilder = null;
		if (pageObject instanceof SqlParam) {
			pageBuilder = new PageBuilder((SqlParam) pageObject);
			return pageBuilder;
		}
		pageBuilder = new PageBuilder();
		Object offset = ReflectUtils.getPropertyByInvokeMethod(pageObject, "offset");
		Object limit = ReflectUtils.getPropertyByInvokeMethod(pageObject, "limit");
		pageBuilder.sort((String) ReflectUtils.getPropertyByInvokeMethod(pageObject, "sortBy"));
		if (limit != null) {
			int limitNum = Integer.parseInt(limit.toString());
			Integer offsetNum = null;
			if (offset == null) {
				Object pageNo = ReflectUtils.getPropertyByInvokeMethod(pageObject, "pageNo");
				offsetNum = (pageNo == null ? 0 : ((Integer.parseInt(pageNo.toString()) - 1) * limitNum));
			}
			else {
				offsetNum = Integer.parseInt(offset.toString());
			}
			pageBuilder.page(offsetNum, limitNum);
		}
		return pageBuilder;
	}

	public static PageBuilder page(int pageNo, int limit, boolean shouldCount) {
		return new PageBuilder().page((pageNo - 1) * limit, limit).shouldCount(shouldCount);
	}

	public static SortBuilder sort(String sortBy) {
		Assert.hasLength(sortBy, "排序不能为空");
		return new SortBuilder().sort(sortBy);
	}

	public static SqlParam getLocalSqlParam() {
		return LOCAL_SQL_PARAM.get();
	}

	public static void clearSqlParam() {
		LOCAL_SQL_PARAM.set(null);
	}

	protected static void start(SqlParam sqlParam) {
		if (StringUtils.isNotBlank(sqlParam.getSortBy())) {
			sqlParam.setSortBy(StringUtils.underscoreName(sqlParam.getSortBy()));
		}
		LOCAL_SQL_PARAM.set(sqlParam);
	}

	public static class PageBuilder {

		private SqlParamBuilder sqlParamBuilder;

		public PageBuilder() {
			this.sqlParamBuilder = new SqlParamBuilder();
		}

		public PageBuilder(SqlParam sqlParam) {
			this.sqlParamBuilder = new SqlParamBuilder(sqlParam);
		}

		public PageBuilder page(int offset, int limit) {
			sqlParamBuilder.page(offset, limit);
			return this;
		}

		public PageBuilder shouldCount(boolean shouldCount) {
			sqlParamBuilder.shouldCount(shouldCount);
			return this;
		}

		public PageBuilder sort(String sortBy) {
			sqlParamBuilder.sort(sortBy);
			return this;
		}

		public void start() {
			execute(null);
		}

		public <S> S execute(Function<SqlParam, S> mapper) {
			SqlParam sqlParam = sqlParamBuilder.build();
			if (sqlParam.getLimit() <= 0 || sqlParam.getOffset() < 0) {
				throw new RuntimeException("分页参数 limit >0 offset>=0");
			}
			SqlHelper.start(sqlParam);
			if (mapper == null) {
				return null;
			}
			return mapper.apply(sqlParam);
		}

		public <S, T> BeanMapperBuilder<S, T> executeAndMapper(Function<SqlParam, S> daoMapper) {
			return new BeanMapperBuilder(execute(daoMapper));
		}

	}

	public static class BeanMapperBuilder<S, T> {

		private S source;

		public BeanMapperBuilder(S source) {
			this.source = source;
		}

		public <T> T mapper(Function<S, T> mapperFunc) {
			if (mapperFunc == null) {
				return (T) source;
			}
			return mapperFunc.apply(source);
		}

	}

	public static class SortBuilder {

		private SqlParamBuilder sqlParamBuilder;

		public SortBuilder() {
			this.sqlParamBuilder = new SqlParamBuilder();
		}

		public SortBuilder sort(String sortBy) {
			sqlParamBuilder.sort(sortBy);
			sqlParamBuilder.shouldCount(false);
			sqlParamBuilder.shouldPage(false);
			return this;
		}

		public void start() {
			execute(null);
		}

		public <T> T execute(Function<SqlParam, T> mapper) {
			SqlParam sqlParam = sqlParamBuilder.build();
			SqlHelper.start(sqlParam);
			if (mapper == null) {
				return null;
			}
			return mapper.apply(sqlParam);
		}

		public <S, T> BeanMapperBuilder<S, T> executeAndMapper(Function<SqlParam, S> daoMapper) {
			return new BeanMapperBuilder(execute(daoMapper));
		}

	}

	static class SqlParamBuilder {

		private SqlParam sqlParam;

		public SqlParamBuilder() {
			if (sqlParam == null) {
				this.sqlParam = new SqlParam();
			}
		}

		public SqlParamBuilder(SqlParam sqlParam) {
			this.sqlParam = sqlParam;
		}

		public SqlParamBuilder param(SqlParam param) {
			this.sqlParam = param;
			return this;
		}

		public SqlParamBuilder page(int offset, int limit) {
			sqlParam.setOffset(offset);
			sqlParam.setLimit(limit);
			sqlParam.setShouldPage(true);
			return this;
		}

		public SqlParamBuilder offset(int offset) {
			sqlParam.setOffset(offset);
			return this;
		}

		public SqlParamBuilder limit(int limit) {
			sqlParam.setLimit(limit);
			return this;
		}

		public SqlParamBuilder shouldCount(boolean shouldCount) {
			sqlParam.setShouldCount(shouldCount);
			if (shouldCount) {
				this.sqlParam.setShouldPage(true);
			}
			return this;
		}

		public SqlParamBuilder shouldPage(boolean shouldPage) {
			sqlParam.setShouldPage(shouldPage);
			return this;
		}

		public SqlParamBuilder sort(String sortBy) {
			sqlParam.setSortBy(sortBy);
			return this;
		}

		public SqlParam build() {
			return sqlParam;
		}

	}

}
