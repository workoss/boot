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

import java.util.List;

/**
 * @author workoss
 */
public class Page<E> {

	private static final Page EMPTY = new Page<>();

	/**
	 * 分页偏移量
	 */
	private Long offset;

	/**
	 * 每页大小
	 */
	private Integer limit;

	/**
	 * 列表数据
	 */
	private List<E> list;

	/**
	 * 分页偏移量
	 */
	private String sortBy;

	/**
	 * 数据量大小
	 */
	private Long count;

	public Page() {
	}

	public static <E> Page<E> empty() {
		return EMPTY;
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

	public List<E> getList() {
		return list;
	}

	public void setList(List<E> list) {
		this.list = list;
	}

	public String getSortBy() {
		return sortBy;
	}

	public void setSortBy(String sortBy) {
		this.sortBy = sortBy;
	}

	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}

}
