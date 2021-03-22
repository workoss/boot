package com.workoss.boot;

import com.workoss.boot.plugin.mybatis.PageResult;

import java.util.ArrayList;
import java.util.List;

public class DemoDaoImpl implements DemoDao {

	@Override
	public PageResult<DemoEntity> selectPage(DemoEntity demoEntity) {
		PageResult<DemoEntity> pageResult = new PageResult<>();
		return pageResult;
	}

	@Override
	public List<DemoEntity> selectList(DemoEntity demoEntity) {
		List<DemoEntity> demoEntities = new ArrayList<>();
		return demoEntities;
	}

}
