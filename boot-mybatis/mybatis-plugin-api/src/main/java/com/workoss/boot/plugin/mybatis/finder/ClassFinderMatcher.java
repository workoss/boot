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
package com.workoss.boot.plugin.mybatis.finder;

/**
 * @author workoss
 */
public interface ClassFinderMatcher {

	/**
	 * 排序
	 * @return
	 */
	int order();

	/**
	 * 匹配 过滤查找一级
	 * @return 是否使用
	 */
	boolean match();

	/**
	 * 初始化
	 * @return EntityClassFinder
	 */
	EntityClassFinder instance();

}
