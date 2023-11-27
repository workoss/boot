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
package com.workoss.boot.model.page;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

/**
 * 分页入参
 *
 * @author workoss
 */
@Getter
public abstract class AbstractPageParam {

	/**
	 * 偏移量
	 */
	private Long offset = 0L;

	/**
	 * 每页大小
	 */
	@NotNull(message = "分页limit不能为空")
	@Min(value = 1, message = "分页大小为1-500")
	@Max(value = 500, message = "分页大小为1-500")
	private Integer limit = 10;

	/**
	 * 第几页
	 */
	private Long pageNo = 1L;

	/**
	 * 排序参数
	 */
	private String sortBy;

	public void setOffset(Long offset) {
		this.offset = offset;
	}

	public void setLimit(Integer limit) {
		this.limit = limit;
	}

	public void setPageNo(Long pageNo) {
		this.pageNo = pageNo;
		if (pageNo != null && this.offset == null) {
			this.offset = (pageNo - 1) * this.limit;
		}
	}

	public void setSortBy(String sortBy) {
		this.sortBy = sortBy;
	}

}
