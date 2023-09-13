/*
 * Copyright 2019-2023 workoss (https://www.workoss.com)
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
package com.workoss.boot.autoconfigure;

import com.workoss.boot.mapper.EnumAutoTranslator;
import com.workoss.boot.mapper.EnumTranslator;
import com.workoss.boot.service.DistributedTemplate;
import com.workoss.boot.util.id.SnowflakeUtil;
import io.lettuce.core.api.async.RedisAsyncCommands;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisReactiveAutoConfiguration;
import org.springframework.boot.task.TaskExecutorCustomizer;
import org.springframework.boot.task.TaskSchedulerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * 服务配置
 *
 * @author workoss
 */
@Slf4j
@AutoConfiguration
public class ServiceConfig {

	@Bean
	public TaskExecutorCustomizer taskExecutorCustomizer() {
		return taskExecutor -> {
			taskExecutor.setThreadGroupName("TASK");
			// 如果设置过大 则修改成 小的 根据cpu来设置
			if (taskExecutor.getMaxPoolSize() == Integer.MAX_VALUE) {
				taskExecutor.setMaxPoolSize(256);
			}
			if (taskExecutor.getQueueCapacity() == Integer.MAX_VALUE) {
				taskExecutor.setQueueCapacity(10240);
			}
			log.debug("[TASK] custom threadGroupName:{} poolSize:{} maxPoolSize:{} queueCapacity:{}",
					taskExecutor.getThreadGroup().getName(), taskExecutor.getCorePoolSize(),
					taskExecutor.getMaxPoolSize(), taskExecutor.getQueueCapacity());
		};
	}

	@Bean
	public TaskSchedulerCustomizer taskSchedulerCustomizer() {
		return taskScheduler -> {
			taskScheduler.setThreadGroupName("TASK_SCHEDULER");
			if (taskScheduler.getPoolSize() == Integer.MAX_VALUE) {
				taskScheduler.setPoolSize(16);
			}
			log.debug("[TASK_SCHEDULER] custom threadGroupName:{} poolSize:{}",
					taskScheduler.getThreadGroup().getName(), taskScheduler.getPoolSize());
		};
	}

	@Bean
	public EnumAutoTranslator enumAutoTranslator() {
		return new EnumAutoTranslator();
	}

	@Bean
	public EnumTranslator enumTranslator() {
		return new EnumTranslator();
	}

	@Order(2)
	@Bean
	public CommandLineRunner idGenerateRunner() {
		return args -> SnowflakeUtil.nextId();
	}

	@Configuration
	@ConditionalOnClass({ RedisTemplate.class, RedisAsyncCommands.class, })
	@AutoConfigureAfter({ RedisAutoConfiguration.class, RedisReactiveAutoConfiguration.class })
	public static class DistributedConfig {

		@Bean
		public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
			RedisTemplate<String, Object> template = new RedisTemplate<>();
			template.setConnectionFactory(redisConnectionFactory);
			StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
			template.setKeySerializer(stringRedisSerializer);
			template.setHashKeySerializer(stringRedisSerializer);
			Jackson2JsonRedisSerializer jsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
			template.setValueSerializer(jsonRedisSerializer);
			template.setHashValueSerializer(jsonRedisSerializer);
			return template;
		}

		@Bean
		public DistributedTemplate distributedTemplate(RedisTemplate redisTemplate) {
			return new DistributedTemplate(redisTemplate);
		}

	}

}
