/*
 * The MIT License
 * Copyright © 2020-2021 workoss
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.workoss.boot.plugin.mybatis;

import java.util.List;

import com.workoss.boot.plugin.mybatis.provider.CrudDeleteProvider;
import com.workoss.boot.plugin.mybatis.provider.CrudInsertProvider;
import com.workoss.boot.plugin.mybatis.provider.CrudSelectProvider;
import com.workoss.boot.plugin.mybatis.provider.CrudUpdateProvider;
import org.apache.ibatis.annotations.*;

/**
 * @author workoss
 */
public interface CrudDao<T, ID> {

	/**
	 * 根据id查询
	 * @param id
	 * @return
	 */
	@SelectProvider(type = CrudSelectProvider.class, method = "selectById")
	T selectById(@Param("id") ID id);

	/**
	 * 根据id批量查询
	 * @param ids
	 * @return
	 */
	@SelectProvider(type = CrudSelectProvider.class, method = "selectByIds")
	List<T> selectByIds(@Param("ids") List<ID> ids);

	/**
	 * 多条件查询
	 * @param record
	 * @return
	 */
	@SelectProvider(type = CrudSelectProvider.class, method = "selectSelective")
	List<T> selectSelective(@Param("record") T record);

	/**
	 * 分页查询
	 * @param record
	 * @return
	 */
	@SelectProvider(type = CrudSelectProvider.class, method = "selectSelective")
	PageResult<T> selectPageSelective(@Param("record") T record);

	/**
	 * 多条件count
	 * @param record
	 * @return
	 */
	@SelectProvider(type = CrudSelectProvider.class, method = "selectCountSelective")
	int selectCountSelective(@Param("record") T record);

	/**
	 * 插入
	 * @param record
	 * @return
	 */
	@InsertProvider(type = CrudInsertProvider.class, method = "insert")
	int insert(@Param("record") T record);

	/**
	 * 可选插入
	 * @param record
	 * @return
	 */
	@InsertProvider(type = CrudInsertProvider.class, method = "insertSelective")
	int insertSelective(@Param("record") T record);

	/**
	 * 批量插入
	 * @param list
	 * @return
	 */
	@InsertProvider(type = CrudInsertProvider.class, method = "insertBatch")
	int insertBatch(@Param("list") List<T> list);

	/**
	 * 根据id 修改
	 * @param record
	 * @param id
	 * @return
	 */
	@UpdateProvider(type = CrudUpdateProvider.class, method = "updateById")
	int updateById(@Param("record") T record, @Param("id") ID id);

	/**
	 * 根据id删除
	 * @param id
	 * @return
	 */
	@DeleteProvider(type = CrudDeleteProvider.class, method = "deleteById")
	int deleteById(@Param("id") ID id);

	/**
	 * 根据ids 批量删除
	 * @param ids
	 * @return
	 */
	@DeleteProvider(type = CrudDeleteProvider.class, method = "deleteByIds")
	int deleteByIds(@Param("ids") List<ID> ids);

	/**
	 * 条件删除
	 * @param t
	 * @return
	 */
	@DeleteProvider(type = CrudDeleteProvider.class, method = "deleteSelective")
	int deleteSelective(@Param("record") T t);

}
