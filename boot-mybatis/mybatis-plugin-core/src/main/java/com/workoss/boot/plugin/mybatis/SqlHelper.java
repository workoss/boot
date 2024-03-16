/*
 * Copyright 2019-2024 workoss (https://www.workoss.com)
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

import com.workoss.boot.model.Sqlable;
import com.workoss.boot.util.Assert;
import com.workoss.boot.util.StringUtils;
import com.workoss.boot.util.concurrent.fast.FastThreadLocal;
import com.workoss.boot.util.reflect.ReflectUtils;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * SqlHelper
 *
 * @author workoss
 */
@SuppressWarnings("ALL")
public class SqlHelper {

	protected static final FastThreadLocal<Sqlable> LOCAL_SQL_PARAM = new FastThreadLocal<>();

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
		if (pageObject instanceof Sqlable sqlParam) {
			pageBuilder = new PageBuilder(sqlParam);
			return pageBuilder;
		}
		pageBuilder = new PageBuilder();
		Object offset = ReflectUtils.getPropertyByInvokeMethod(pageObject, "offset");
		Object limit = ReflectUtils.getPropertyByInvokeMethod(pageObject, "limit");
		pageBuilder.sort((String) ReflectUtils.getPropertyByInvokeMethod(pageObject, "sortBy"));
		if (limit != null) {
			int limitNum = Integer.parseInt(limit.toString());
			Long offsetNum = null;
			if (offset == null) {
				Object pageNo = ReflectUtils.getPropertyByInvokeMethod(pageObject, "pageNo");
				offsetNum = (pageNo == null ? 0L : ((Integer.parseInt(pageNo.toString()) - 1) * limitNum));
			}
			else {
				offsetNum = Long.parseLong(offset.toString());
			}
			pageBuilder.page(offsetNum, limitNum);
		}
		return pageBuilder;
	}

	public static PageBuilder page(long pageNo, int limit, boolean shouldCount) {
		return new PageBuilder().page((pageNo - 1) * limit, limit).shouldCount(shouldCount);
	}

	public static SortBuilder sort(String sortBy) {
		Assert.hasLength(sortBy, "排序不能为空");
		return new SortBuilder().sort(sortBy);
	}

	public static Sqlable getLocalSqlParam() {
		return LOCAL_SQL_PARAM.get();
	}

	public static void clearSqlParam() {
		LOCAL_SQL_PARAM.set(null);
	}

	protected static void start(Sqlable sqlRequest) {
		LOCAL_SQL_PARAM.set(sqlRequest);
	}

	public static class PageBuilder {

		private SqlParamBuilder sqlParamBuilder;

		public PageBuilder() {
			this.sqlParamBuilder = new SqlParamBuilder();
		}

		public PageBuilder(Sqlable sqlable) {
			this.sqlParamBuilder = new SqlParamBuilder(sqlable);
		}

		public PageBuilder page(Long offset, int limit) {
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
			execute((Function<Sqlable, ? extends Object>) null);
		}

		public <S> S execute(Function<Sqlable, S> mapper) {
			Sqlable sqlRequest = sqlParamBuilder.build();
			if (sqlRequest.getLimit() <= 0 || sqlRequest.getOffset() < 0) {
				throw new RuntimeException("分页参数 limit >0 offset>=0");
			}
			SqlHelper.start(sqlRequest);
			if (mapper == null) {
				return null;
			}
			return mapper.apply(sqlRequest);
		}

		public <S> S execute(Supplier<S> mapper) {
			Sqlable sqlRequest = sqlParamBuilder.build();
			if (sqlRequest.getLimit() <= 0 || sqlRequest.getOffset() < 0) {
				throw new RuntimeException("分页参数 limit >0 offset>=0");
			}
			SqlHelper.start(sqlRequest);
			if (mapper == null) {
				return null;
			}
			return mapper.get();
		}

		public <S, T> BeanMapperBuilder<S, T> executeAndMapper(Function<Sqlable, S> daoMapper) {
			return new BeanMapperBuilder(execute(daoMapper));
		}

		public <S, T> BeanMapperBuilder<S, T> executeAndMapper(Supplier<S> daoMapper) {
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
			execute((Function<Sqlable, ? extends Object>) null);
		}

		public <T> T execute(Function<Sqlable, T> mapper) {
			Sqlable sqlRequest = sqlParamBuilder.build();
			SqlHelper.start(sqlRequest);
			if (mapper == null) {
				return null;
			}
			return mapper.apply(sqlRequest);
		}

		public <T> T execute(Supplier<T> mapper) {
			SqlHelper.start(sqlParamBuilder.build());
			if (mapper == null) {
				return null;
			}
			return mapper.get();
		}

		public <S, T> BeanMapperBuilder<S, T> executeAndMapper(Function<Sqlable, S> daoMapper) {
			return new BeanMapperBuilder(execute(daoMapper));
		}

		public <S, T> BeanMapperBuilder<S, T> executeAndMapper(Supplier<S> daoMapper) {
			return new BeanMapperBuilder(execute(daoMapper));
		}

	}

	static class SqlParamBuilder {

		private SqlRequest sqlRequest;

		public SqlParamBuilder() {
			if (sqlRequest == null) {
				this.sqlRequest = SqlRequest.of();
			}
		}

		public SqlParamBuilder(Sqlable sqlable) {
			if (sqlRequest == null) {
				this.sqlRequest = SqlRequest.of();
			}
			if (sqlable == null) {
				return;
			}
			sqlRequest.setLimit(sqlable.getLimit());
			sqlRequest.setOffset(sqlable.getOffset());
			sqlRequest.setSortBy(sqlable.getSortBy());
			sqlRequest.setShouldCount(sqlable.getShouldCount());
			sqlRequest.setShouldPage(sqlable.getShouldPage());
		}

		public SqlParamBuilder page(Long offset, Integer limit) {
			sqlRequest.setOffset(offset);
			sqlRequest.setLimit(limit);
			sqlRequest.setShouldPage(true);
			return this;
		}

		public SqlParamBuilder offset(Long offset) {
			sqlRequest.setOffset(offset);
			return this;
		}

		public SqlParamBuilder limit(Integer limit) {
			sqlRequest.setLimit(limit);
			return this;
		}

		public SqlParamBuilder shouldCount(boolean shouldCount) {
			sqlRequest.setShouldCount(shouldCount);
			if (shouldCount) {
				this.sqlRequest.setShouldPage(true);
			}
			return this;
		}

		public SqlParamBuilder shouldPage(boolean shouldPage) {
			sqlRequest.setShouldPage(shouldPage);
			return this;
		}

		public SqlParamBuilder sort(String sortBy) {
			sqlRequest.setSortBy(sortBy);
			return this;
		}

		public SqlRequest build() {
			if (StringUtils.isNotBlank(sqlRequest.getSortBy())) {
				sqlRequest.setSortBy(StringUtils.underscoreName(sqlRequest.getSortBy()));
			}
			return sqlRequest;
		}

	}

}
