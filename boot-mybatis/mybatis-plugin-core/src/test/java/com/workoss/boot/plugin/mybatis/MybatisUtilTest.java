package com.workoss.boot.plugin.mybatis;

import com.alibaba.druid.DbType;


import java.sql.SQLException;

class MybatisUtilTest {


	public static void main(String[] args) throws SQLException {
//		String jdbcUrl = "r2dbc:mariadb://139.196.88.110:3307/o2o-grab?serverTimezone=Asia/Shanghai";
		String jdbcUrl = "jdbc:es://http://es-cn-n6w1vnyya000gjkfc.elasticsearch.aliyuncs.com:9200/?timezone=UTC&page.size=250";
		DbType dbType = MybatisUtil.getDbType(jdbcUrl);
		System.out.println(dbType);
	}

}
