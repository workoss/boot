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
package com.workoss.boot.modulith.events.mybatis;

import com.workoss.boot.plugin.mybatis.DynamicDao;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.jdbc.DatabaseDriver;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ResourceLoader;
import org.springframework.modulith.events.config.EventPublicationAutoConfiguration;
import org.springframework.modulith.events.config.EventPublicationConfigurationExtension;
import org.springframework.modulith.events.core.EventSerializer;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * @author workoss
 */
@AutoConfiguration
@AutoConfigureBefore({ EventPublicationAutoConfiguration.class })
class MybatisEventPublicationAutoConfiguration implements EventPublicationConfigurationExtension {

	@ConditionalOnMissingBean
	@Bean
	public DynamicDao dynamicDao(SqlSessionTemplate sqlSessionTemplate) {
		try {
			return sqlSessionTemplate.getMapper(DynamicDao.class);
		}
		catch (Exception e) {
			sqlSessionTemplate.getConfiguration().addMapper(DynamicDao.class);
		}
		return sqlSessionTemplate.getMapper(DynamicDao.class);
	}

	@Bean
	DatabaseType databaseType(DataSource dataSource) throws SQLException {
		String url = dataSource.getConnection().getMetaData().getURL();
		return DatabaseType.from(DatabaseDriver.fromJdbcUrl(url));
	}

	@Bean
	MybatisEventPublicationRepository jdbcEventPublicationRepository(EventSerializer serializer, DynamicDao dynamicDao,
			DatabaseType databaseType) {
		return new MybatisEventPublicationRepository(serializer, dynamicDao, databaseType);
	}

	@Bean
	@ConditionalOnProperty(name = { "spring.modulith.events.jdbc.schema-initialization.enabled" }, havingValue = "true")
	DatabaseSchemaInitializer databaseSchemaInitializer(DynamicDao dynamicDao, ResourceLoader resourceLoader,
			DatabaseType databaseType) {
		return new DatabaseSchemaInitializer(databaseType, resourceLoader, dynamicDao);
	}

}
