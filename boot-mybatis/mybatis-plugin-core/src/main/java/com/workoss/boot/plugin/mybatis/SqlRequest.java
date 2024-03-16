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

/**
 * SqlParam
 *
 * @author workoss
 */
@SuppressWarnings("ALL")
public class SqlRequest implements Sqlable {

	private Long offset = 0L;

	private Integer limit = 10;

	private String sortBy;

	private boolean shouldCount = false;

	private boolean shouldPage = true;

	public SqlRequest(Long offset, Integer limit, String sortBy, boolean shoudlPage, boolean shouldCount) {
		this.offset = offset;
		this.limit = limit;
		this.sortBy = sortBy;
		this.shouldPage = shoudlPage;
		this.shouldCount = shouldCount;
	}

	public static SqlRequest of() {
		return of(1L, 10, null, false);
	}

	public static SqlRequest of(Long offset, Integer limit, String sortBy) {
		return of(offset, limit, sortBy, false);
	}

	public static SqlRequest of(Long offset, Integer limit, String sortBy, Boolean shouldCount) {
		return new SqlRequest(offset, limit, sortBy, shouldCount, true);
	}

	public Long getOffset() {
		return offset;
	}

	public void setOffset(Long offset) {
		this.offset = offset;
	}

	public Integer getLimit() {
		return limit;
	}

	public void setLimit(Integer limit) {
		this.limit = limit;
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

	public boolean getShouldPage() {
		return shouldPage;
	}

	public void setShouldPage(boolean shouldPage) {
		this.shouldPage = shouldPage;
	}

}
