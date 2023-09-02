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
package com.workoss.boot.web.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Getter;

/**
 * 分页入参
 *
 * @author workoss
 */
@Getter
@Schema(name = "分页参数", description = "基础分页入参")
public abstract class AbstractPageParam {

	/**
	 * 偏移量
	 */
	@Schema(name = "offset", description = "分页偏移量 优先", example = "0")
	private Long offset;

	/**
	 * 每页大小
	 */
	@NotNull(message = "分页limit不能为空")
	@Min(value = 1, message = "分页大小为1-500")
	@Max(value = 500, message = "分页大小为1-500")
	@Schema(name = "limit", description = "分页每页大小", example = "10")
	private Integer limit = 10;

	/**
	 * 第几页
	 */
	@Schema(name = "pageNo", description = "第几页 跟offset二选一", example = "1")
	private Long pageNo = 1L;

	/**
	 * 排序参数
	 */
	@Schema(name = "sortBy", description = "分页排序", example = "modifyTime desc,id desc")
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
