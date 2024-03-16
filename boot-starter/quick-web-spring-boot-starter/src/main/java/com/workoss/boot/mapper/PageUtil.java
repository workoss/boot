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
package com.workoss.boot.mapper;

import com.workoss.boot.model.Page;
import com.workoss.boot.plugin.mybatis.PageResult;
import com.workoss.boot.web.vo.PageVO;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

/**
 * @author workoss
 */
public class PageUtil {

	private PageUtil() {
	}

	public static <S, T> Page<T> buildPage(PageResult<S> pageResult, Function<List<S>, List<T>> toPageMapper) {
		if (pageResult == null) {
			return Page.empty();
		}
		Page<T> page = new Page<>();
		page.setOffset(pageResult.getOffset());
		page.setLimit(pageResult.getLimit());
		page.setCount(pageResult.getCount());
		page.setSortBy(pageResult.getSortBy());
		if (toPageMapper == null) {
			// copy
			return page;
		}
		page.setList(toPageMapper.apply(pageResult));
		return page;
	}

	public static <S, T> Page<T> toPage(Page<S> page, Function<List<S>, List<T>> toPageMapper) {
		if (page == null) {
			return Page.empty();
		}
		Page<T> topage = new Page<>();
		topage.setOffset(page.getOffset());
		topage.setLimit(page.getLimit());
		topage.setCount(page.getCount());
		topage.setSortBy(page.getSortBy());
		if (toPageMapper == null || page.getList() == null) {
			// copy
			topage.setList(Collections.emptyList());
			return topage;
		}
		topage.setList(toPageMapper.apply(page.getList()));
		return topage;
	}

	public static <S, T> PageVO<T> toPageVO(Page<S> page, Function<List<S>, List<T>> toPageMapper) {
		if (page == null) {
			return PageVO.empty();
		}
		PageVO<T> pageVO = new PageVO<>();
		pageVO.setOffset(page.getOffset());
		pageVO.setLimit(page.getLimit());
		pageVO.setCount(page.getCount());
		pageVO.setSortBy(page.getSortBy());
		if (toPageMapper == null || page.getList() == null) {
			pageVO.setList(Collections.emptyList());
			return pageVO;
		}
		pageVO.setList(toPageMapper.apply(page.getList()));
		return pageVO;
	}

}
