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

import com.workoss.boot.plugin.mybatis.CrudDao;
import com.workoss.boot.plugin.mybatis.provider.ClassTableColumnInfo;
import org.apache.ibatis.builder.annotation.ProviderContext;

import java.lang.reflect.ParameterizedType;
import java.util.Optional;
import java.util.stream.Stream;

public interface EntityClassFinder {

	/**
	 * 循环匹配
	 * @param context mybatis context
	 * @return 是否使用
	 */
	boolean match(ProviderContext context);

	/**
	 * 查找表列信息
	 * @param context
	 * @return
	 */
	Optional<ClassTableColumnInfo> findTableColumnInfo(ProviderContext context);

}
