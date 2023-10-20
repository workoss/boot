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
package com.workoss.boot.plugin.mybatis;

import java.util.List;
import java.util.Map;

/**
 * @author workoss
 */
public interface DynamicDaoConvert<S> {

	/**
	 * 参数转换成map
	 * @param s 对象
	 * @return
	 */
	Map<String, Object> convertParam(S s);

	/**
	 * 转换结果
	 * @param result 返回值
	 * @return list对象
	 */
	List<S> convertResult(List<Map<String, Object>> result);

}
