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

import com.workoss.boot.plugin.mybatis.provider.BaseProvider;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;

import java.util.List;
import java.util.Map;

/**
 * baseDao
 *
 * @author workoss
 */
@SuppressWarnings("ALL")
public interface BaseDao<T, ID> {

	/**
	 * 执行查询
	 * @param sql sql
	 * @return 执行结果
	 */
	@SelectProvider(type = BaseProvider.class, method = "executeQuery")
	List<Map<String, Object>> executeQuery(@Param("sql") String sql);

	/**
	 * 执行修改
	 * @param sql sql语句
	 * @return 执行成功行数
	 */
	@UpdateProvider(type = BaseProvider.class, method = "executeUpdate")
	int executeUpdate(@Param("sql") String sql);

}
