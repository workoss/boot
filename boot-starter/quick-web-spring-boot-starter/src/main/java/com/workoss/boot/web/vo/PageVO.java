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
package com.workoss.boot.web.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 分页返回VO
 *
 * @author workoss
 */
@Schema(name = "pageVO", description = "分页返回")
@Data
public class PageVO<E> {

	/**
	 * 分页偏移量
	 */
	@Schema(name = "offset", description = "分页偏移量")
	private Long offset;

	/**
	 * 每页大小
	 */
	@Schema(name = "limit", description = "每页大小")
	private Integer limit;

	/**
	 * 列表数据
	 */
	@Schema(name = "list", description = "列表数据")
	private List<E> list = new ArrayList<>();

	/**
	 * 分页偏移量
	 */
	@Schema(name = "offset", description = "分页偏移量")
	private String sortBy;

	/**
	 * 数据量大小
	 */
	@Schema(name = "count", description = "数据量大小")
	private Long count;

	public static <E> PageVO<E> empty() {
		return new PageVO<>();
	}

}
