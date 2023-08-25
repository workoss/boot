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
package com.workoss.boot.service;

import com.workoss.boot.util.StringUtils;
import com.workoss.boot.util.collection.CollectionUtils;
import com.workoss.boot.util.json.JsonMapper;
import io.lettuce.core.SetArgs;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.cluster.api.async.RedisAdvancedClusterAsyncCommands;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 分布式服务
 *
 * @author workoss
 */
@SuppressWarnings("ALL")
@Slf4j
public class DistributedTemplate {

	private final RedisTemplate redisTemplate;

	private static Map<String, DefaultRedisScript> SCRIPT_MAP = new ConcurrentHashMap<>();

	public DistributedTemplate(RedisTemplate redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	public boolean lock(String requestId, String key, long expire, TimeUnit timeUnit) {
		Object result = null;
		Expiration expiration = Expiration.seconds(timeUnit.toSeconds(expire));
		final String lockKey = getLockKey(key);
		try {
			result = redisTemplate.execute((RedisCallback) redisConnection -> {
				Object nativeConnection = redisConnection.getNativeConnection();
				String status = null;
				RedisSerializer<String> stringRedisSerializer = (RedisSerializer<String>) redisTemplate
					.getKeySerializer();
				byte[] keyByte = stringRedisSerializer.serialize(lockKey);
				byte[] valueByte = stringRedisSerializer.serialize(requestId);
				// lettuce 集群模式 ex 秒 px 毫秒
				if (nativeConnection instanceof RedisAdvancedClusterAsyncCommands) {
					RedisAdvancedClusterAsyncCommands clusterAsyncCommands = (RedisAdvancedClusterAsyncCommands) nativeConnection;
					log.debug("【DISTRIBUTED】lettuce Cluster:---setKey:{} value:{} timeout:{}", lockKey, requestId,
							expiration.getExpirationTimeInSeconds());
					status = clusterAsyncCommands.getStatefulConnection()
						.sync()
						.set(keyByte, valueByte, SetArgs.Builder.nx().ex(expiration.getExpirationTimeInSeconds()));
					log.debug("【DISTRIBUTED】lettuce Cluster:---status:{}", status);
				}
				if (nativeConnection instanceof RedisAsyncCommands) {
					RedisAsyncCommands commands = (RedisAsyncCommands) nativeConnection;
					log.debug("【DISTRIBUTED】lettuce single:---setKey:{} value:{} timeout:{}", lockKey, requestId,
							expiration.getExpirationTimeInSeconds());
					status = commands.getStatefulConnection()
						.sync()
						.set(keyByte, valueByte, SetArgs.Builder.nx().ex(expire));
					log.debug("【DISTRIBUTED】lettuce single:---status:{}", status);
				}
				return status;
			});
		}
		catch (Exception e) {
			throw new RuntimeException("【DISTRIBUTED】获取锁失败");
		}
		if (result == null) {
			return false;
		}
		return "OK".equalsIgnoreCase(result.toString());
	}

	private String getLockKey(String key) {
		return String.format("LOCK:%s", key);
	}

	public boolean unlock(String requestId, String key) {
		final String lockKey = getLockKey(key);
		return redisTemplate.delete(lockKey);
	}

	public List<String> pollZSet(String key, float min, float max, int offset, int count) {
		RedisScript script = getScript("POLL_QUEUE");
		try {
			Object object = redisTemplate.execute(script, Collections.singletonList(key), min, max, offset, count);
			if (object == null) {
				return null;
			}
			String jsonString = JsonMapper.toJSONString(object);
			if ("[null]".equalsIgnoreCase(jsonString)) {
				return null;
			}
			List<String> strings = JsonMapper.parseArray(jsonString, String.class);
			if (CollectionUtils.isNotEmpty(strings)) {
				return strings.stream().filter(s -> StringUtils.isNotBlank(s)).collect(Collectors.toList());
			}
			return null;
		}
		catch (Exception e) {
			throw new RuntimeException("【DISTRIBUTED】pollZSet error:" + e.getMessage());
		}
	}

	private DefaultRedisScript getScript(String scriptName) {
		if (SCRIPT_MAP.containsKey(scriptName)) {
			return SCRIPT_MAP.get(scriptName);
		}
		DefaultRedisScript redisScript = new DefaultRedisScript();
		redisScript.setLocation(new ClassPathResource(String.format("lua/%s.lua", scriptName)));
		redisScript.setResultType(List.class);
		SCRIPT_MAP.put(scriptName, redisScript);
		return redisScript;
	}

}
