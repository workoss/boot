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

import com.alibaba.druid.DbType;

import java.sql.SQLException;

class MybatisUtilTest {

	public static void main(String[] args) throws SQLException {
		// String jdbcUrl =
		// "r2dbc:mariadb://139.196.88.110:3307/o2o-grab?serverTimezone=Asia/Shanghai";
		String jdbcUrl = "jdbc:es://http://es-cn-n6w1vnyya000gjkfc.elasticsearch.aliyuncs.com:9200/?timezone=UTC&page.size=250";
		DbType dbType = MybatisUtil.getDbType(jdbcUrl);
		System.out.println(dbType);
	}

}
