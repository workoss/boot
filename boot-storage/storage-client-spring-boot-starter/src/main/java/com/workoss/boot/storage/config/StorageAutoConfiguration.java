/*
 * Copyright 2019-2021 workoss (https://www.workoss.com)
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
package com.workoss.boot.storage.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.workoss.boot.storage.AwsStorageTemplate;
import com.workoss.boot.storage.MinioStorageTemplate;
import com.workoss.boot.storage.StorageTemplate;
import com.workoss.boot.storage.health.StorageClientEndpoint;
import com.workoss.boot.storage.health.StorageHealthIndicator;
import com.workoss.boot.storage.web.filter.StorageServiceFilter;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.http.HttpServletRequest;

/**
 * 对象存储自动化配置
 *
 * @author workoss
 */
@SuppressWarnings("ALL")
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({ StorageTemplate.class })
@EnableConfigurationProperties({ MultiStorageClientConfigProperties.class })
@ConditionalOnProperty(prefix = MultiStorageClientConfig.PREFIX, value = MultiStorageClientConfig.ENABLED,
		matchIfMissing = true)
public class StorageAutoConfiguration {

	@ConditionalOnClass(name = { "com.workoss.boot.storage.AwsStorageTemplate" })
	@Bean
	StorageTemplate storageTemplate(MultiStorageClientConfigProperties configProperties) {
		StorageTemplate storageTemplate = new AwsStorageTemplate();
		storageTemplate.setMultiStorageClientConfig(configProperties);
		return storageTemplate;
	}

	protected

	@ConditionalOnClass(
			name = { "com.workoss.boot.storage.MinioStorageTemplate" }) @Bean StorageTemplate minioStorageTemplate(
					MultiStorageClientConfigProperties configProperties) {
		StorageTemplate storageTemplate = new MinioStorageTemplate();
		storageTemplate.setMultiStorageClientConfig(configProperties);
		return storageTemplate;
	}

	@Configuration(proxyBeanMethods = false)
	@ConditionalOnClass({ HealthIndicator.class })
	@ConditionalOnProperty(prefix = MultiStorageClientConfig.PREFIX, value = MultiStorageClientConfig.HEALTH,
			matchIfMissing = true)
	protected static class EnableStorageHealthAutoConfiguration {

		@Bean
		public StorageClientEndpoint storageClientEndpoint(MultiStorageClientConfigProperties configProperties) {
			return new StorageClientEndpoint(configProperties);
		}

		@Bean
		StorageHealthIndicator storageHealthIndicator(StorageTemplate storageTemplate) {
			return new StorageHealthIndicator(storageTemplate);
		}

	}

	@ConditionalOnClass({ HttpServletRequest.class, ObjectMapper.class })
	@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
	@ConditionalOnMissingBean
	@Bean
	FilterRegistrationBean storageFilterRegistrationBean(StorageTemplate storageTemplate, ObjectMapper objectMapper) {
		FilterRegistrationBean registrationBean = new FilterRegistrationBean();
		registrationBean.addUrlPatterns("/storage/signservice/*");
		registrationBean.addUrlPatterns("/storage/signService/*");
		registrationBean.setName("storageFilter");
		registrationBean.setFilter(new StorageServiceFilter(storageTemplate, objectMapper));
		return registrationBean;
	}

}
