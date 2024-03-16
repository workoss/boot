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
package com.workoss.boot.plugin.mybatis;

import com.workoss.boot.plugin.mybatis.provider.BaseProvider;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * crudDao
 *
 * @author workoss
 */
@SuppressWarnings("ALL")
public interface CrudDao<T, ID> {

	/**
	 * 根据id查询
	 * @param id 主键
	 * @return 对象
	 */
	@SelectProvider(type = BaseProvider.class, method = "dynamicSql")
	T selectById(@Param("id") ID id);

	/**
	 * 根据id批量查询
	 * @param ids 主键
	 * @return 对象列表
	 */
	@SelectProvider(type = BaseProvider.class, method = "dynamicSql")
	List<T> selectByIds(@Param("ids") List<ID> ids);

	/**
	 * 多条件查询
	 * @param record 对象
	 * @return 对象列表
	 */
	@SelectProvider(type = BaseProvider.class, method = "dynamicSql")
	List<T> selectSelective(@Param("record") T record);

	/**
	 * 分页查询 需要结合拦截器使用
	 * @param record 对象
	 * @return page
	 */
	@SelectProvider(type = BaseProvider.class, method = "dynamicSql")
	PageResult<T> selectPageSelective(@Param("record") T record);

	/**
	 * 多条件count
	 * @param record 对象
	 * @return count
	 */
	@SelectProvider(type = BaseProvider.class, method = "dynamicSql")
	int selectCountSelective(@Param("record") T record);

	/**
	 * 插入
	 * @param record 对象
	 * @return 数目
	 */
	@InsertProvider(type = BaseProvider.class, method = "dynamicSql")
	int insert(@Param("record") T record);

	/**
	 * 可选插入
	 * @param record 对象
	 * @return 插入数目
	 */
	@InsertProvider(type = BaseProvider.class, method = "dynamicSql")
	int insertSelective(@Param("record") T record);

	/**
	 * 批量插入 sqlServer注意不能太大（参数太多）
	 * @param list 列表
	 * @return 插入数量
	 */
	@InsertProvider(type = BaseProvider.class, method = "dynamicSql")
	int insertBatch(@Param("list") List<T> list);

	/**
	 * 根据id 修改
	 * @param record 对象
	 * @param id 主键
	 * @return 修改数量
	 */
	@UpdateProvider(type = BaseProvider.class, method = "dynamicSql")
	int updateById(@Param("record") T record, @Param("id") ID id);

	/**
	 * 根据id删除
	 * @param id 主键
	 * @return 删除数目
	 */
	@DeleteProvider(type = BaseProvider.class, method = "dynamicSql")
	int deleteById(@Param("id") ID id);

	/**
	 * 根据ids 批量删除
	 * @param ids 主键
	 * @return 删除数目
	 */
	@DeleteProvider(type = BaseProvider.class, method = "dynamicSql")
	int deleteByIds(@Param("ids") List<ID> ids);

	/**
	 * 条件删除
	 * @param t 对象
	 * @return 删除数目
	 */
	@DeleteProvider(type = BaseProvider.class, method = "dynamicSql")
	int deleteSelective(@Param("record") T t);

}
