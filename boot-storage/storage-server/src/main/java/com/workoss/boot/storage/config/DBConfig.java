package com.workoss.boot.storage.config;

import com.workoss.boot.storage.repository.IdEntity;
import com.workoss.boot.storage.repository.TenantEntity;
import io.r2dbc.spi.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;
import org.springframework.data.r2dbc.mapping.event.AfterConvertCallback;
import org.springframework.data.r2dbc.mapping.event.BeforeConvertCallback;
import org.springframework.data.r2dbc.mapping.event.BeforeSaveCallback;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer;
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import reactor.core.publisher.Mono;

/**
 * db 配置
 *
 * @author workoss
 */
@EnableTransactionManagement
@EnableR2dbcAuditing
@EnableR2dbcRepositories
@Configuration
public class DBConfig {

	@Bean
	ConnectionFactoryInitializer initializer(ConnectionFactory connectionFactory) {
		ConnectionFactoryInitializer initializer = new ConnectionFactoryInitializer();
		initializer.setConnectionFactory(connectionFactory);
		initializer.setDatabasePopulator(new ResourceDatabasePopulator(new ClassPathResource("db/schema.sql")));
		return initializer;
	}

	@Bean
	BeforeConvertCallback<? extends IdEntity> beforeConvertIDCallback(DatabaseClient databaseClient) {
		return (idEntity, sqlIdentifier) -> {
			if (idEntity.getId() == null) {
				idEntity.setId(System.currentTimeMillis());
				// return databaseClient.sql("SELECT primary_key.nextval") //
				// .map(row -> row.get(0, Long.class)) //
				// .first() //
				// .map(customer::withId);
			}
			return Mono.just(idEntity);
		};
	}

	@Bean
	BeforeConvertCallback<? extends TenantEntity> beforeConvertCallback(DatabaseClient databaseClient) {
		return (tenantEntity, sqlIdentifier) -> {
			if (tenantEntity.getTenantId() == null) {
				// TODO 放入tenantId
			}
			return Mono.just(tenantEntity);
		};
	}

	@Bean
	AfterConvertCallback<? extends TenantEntity> afterConvertCallback(DatabaseClient databaseClient) {
		return (storageAccountEntity, sqlIdentifier) -> {
			return Mono.just(storageAccountEntity);
		};
	}

	@Bean
	BeforeSaveCallback<? extends TenantEntity> beforeSaveCallback(DatabaseClient databaseClient) {
		return (entity, row, table) -> {
			return Mono.just(entity);
		};
	}

}
