/*
 * Copyright © 2020-2021 workoss (workoss@icloud.com)
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
public class PageResult<E> extends ArrayList<E> implements Closeable {

	private int offset = 0;

	private int limit = 10;

	private int count = 0;

	private int pageNo = 1;

	private String sortBy;

	private boolean shouldCount = false;

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getPageNo() {
		if (this.limit > 0) {
			return this.offset / this.limit + 1;
		}
		return 1;
	}

	public void setPageNo(int pageNo) {
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
