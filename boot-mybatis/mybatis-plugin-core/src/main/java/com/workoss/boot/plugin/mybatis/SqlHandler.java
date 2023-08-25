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

import com.workoss.boot.plugin.mybatis.context.SqlContext;

/**
 * 拦截器
 *
 * @author workoss
 */
@FunctionalInterface
public interface SqlHandler {

	/**
	 * 处理查询参数 上下文 包括
	 * Input(sqlParam,executor,mappedStatement,parameter，rowBounds，resultHandler）
	 * Output(change(是否有改动)，rowBounds,result(pageResult) )
	 * @param context 上下文
	 */
	void handler(SqlContext context);

}
