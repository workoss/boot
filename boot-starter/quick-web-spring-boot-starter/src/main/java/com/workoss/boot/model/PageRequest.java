/*
 * Copyright 2019-2023 workoss (https://www.workoss.com)
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
package com.workoss.boot.model;

/**
 * @author workoss
 */
public final class PageRequest implements Sqlable {

	/**
	 * 分页偏移量
	 */
	private Long offset = 0L;

	/**
	 * 每页大小
	 */
	private Integer limit = 10;

	/**
	 * 分页偏移量
	 */
	private String sortBy;

	private Boolean shouldCount = true;

	public PageRequest(Long offset, Integer limit, String sortBy, Boolean shouldCount) {
		this.offset = offset;
		this.limit = limit;
		this.sortBy = sortBy;
		this.shouldCount = shouldCount;
	}

	public static PageRequest newPage(Long pageNo, Integer limit, String sortBy, Boolean shouldCount) {
		return of((pageNo - 1) * limit, limit, sortBy, true);
	}

	public static PageRequest of(Long offset, Integer limit) {
		return of(offset, limit, null, true);
	}

	public static PageRequest of(Long offset, Integer limit, String sortBy) {
		return of(offset, limit, sortBy, true);
	}

	public static PageRequest of(Long offset, Integer limit, String sortBy, Boolean shouldCount) {
		return new PageRequest(offset, limit, sortBy, shouldCount);
	}

	@Override
	public Long getOffset() {
		return this.offset;
	}

	@Override
	public Integer getLimit() {
		return this.limit;
	}

	@Override
	public String getSortBy() {
		return this.sortBy;
	}

	@Override
	public boolean getShouldCount() {
		return this.shouldCount;
	}

	@Override
	public boolean getShouldPage() {
		return true;
	}

}
