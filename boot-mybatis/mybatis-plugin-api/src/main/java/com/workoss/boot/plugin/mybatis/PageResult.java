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

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;

/**
 * 分页工具类page对象
 *
 * @author workoss
 */
@SuppressWarnings("ALL")
public class PageResult<E> extends ArrayList<E> implements Closeable {

	private Long offset = 0L;

	private int limit = 10;

	private Long count = -1L;

	private Long pageNo = 1L;

	private String sortBy;

	private boolean shouldCount = false;

	public Long getOffset() {
		return offset;
	}

	public void setOffset(Long offset) {
		this.offset = offset;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}

	public Long getPageNo() {
		if (this.limit > 0) {
			return this.offset / this.limit + 1;
		}
		return 1L;
	}

	public void setPageNo(Long pageNo) {
		this.pageNo = pageNo;
	}

	public String getSortBy() {
		return sortBy;
	}

	public void setSortBy(String sortBy) {
		this.sortBy = sortBy;
	}

	public boolean getShouldCount() {
		return shouldCount;
	}

	public void setShouldCount(boolean shouldCount) {
		this.shouldCount = shouldCount;
	}

	@Override
	public void close() throws IOException {

	}

}
