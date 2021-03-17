package com.workoss.boot;

import com.workoss.boot.plugin.mybatis.PageResult;

import java.util.List;

public interface DemoDao {

	PageResult<DemoEntity> selectPage(DemoEntity demoEntity);

	List<DemoEntity> selectList(DemoEntity demoEntity);
}
