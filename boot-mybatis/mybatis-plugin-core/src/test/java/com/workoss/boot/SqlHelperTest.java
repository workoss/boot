package com.workoss.boot;

import com.workoss.boot.plugin.mybatis.SqlHelper;

import java.util.List;

public class SqlHelperTest {

	public static void main(String[] args) {

		DemoDao demoDao = new DemoDaoImpl();
		DemoMapper demoMapper = new DemoMapperImpl();
		DemoEntity demoEntity = new DemoEntity();

		SqlHelper.page(1, 10).start();
		List<DemoEntity> demoEntities = demoDao.selectList(demoEntity);

		List<DemoEntity> entityList = SqlHelper.page(1, 10).execute(sqlParam -> demoDao.selectList(demoEntity));

		List<DemoModel> modelList = SqlHelper.page(1, 10).executeAndMapper(sqlParam -> demoDao.selectList(demoEntity))
				.mapper(demoMapper::toTargetList);
	}

}
