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
package com.workoss.boot.storage.service.token;

import com.workoss.boot.annotation.lang.Nullable;
import com.workoss.boot.storage.model.ThirdPlatformType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * TokenHandlerFactory
 *
 * @author workoss
 */
@Slf4j
@Component
public class TokenHandlerFactory implements ApplicationContextAware, InitializingBean {

	private static Map<ThirdPlatformType, TokenHandler> TOKEN_HANDLER_CACHE = new ConcurrentHashMap<>();

	private ApplicationContext applicationContext;

	public TokenHandler getHandler(ThirdPlatformType type) {
		if (type == null || !TOKEN_HANDLER_CACHE.containsKey(type)) {
			throw new RuntimeException("type:" + type + "还没有支持");
		}
		return TOKEN_HANDLER_CACHE.get(type);
	}

	@Override
	public void afterPropertiesSet() {
		applicationContext.getBeansOfType(TokenHandler.class).values().forEach(tokenHandler -> {
			TOKEN_HANDLER_CACHE.put(tokenHandler.getName(), tokenHandler);
			log.info("[TOKEN_HANDLER] 新增handler {}: {}", tokenHandler.getName(), tokenHandler);
		});
	}

	@Override
	public void setApplicationContext(@Nullable ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

}
