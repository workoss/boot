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
package com.workoss.boot.lock.autoconfigure;

import com.workoss.boot.lock.DistributedTemplate;
import io.lettuce.core.api.async.RedisAsyncCommands;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisReactiveAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @author workoss
 */
@AutoConfiguration
public class LockConfig {

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
