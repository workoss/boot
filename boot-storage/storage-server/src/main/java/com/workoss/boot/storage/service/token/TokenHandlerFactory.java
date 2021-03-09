package com.workoss.boot.storage.service.token;

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

	private static Map<ThirdPlatformType, TokenHandler> TOKEN_HANDLER_CACHE = new ConcurrentHashMap();

	private ApplicationContext applicationContext;

	public TokenHandler getHandler(ThirdPlatformType type) {
		if (type == null || !TOKEN_HANDLER_CACHE.containsKey(type)) {
			throw new RuntimeException("type:" + type + "还没有支持");
		}
		return TOKEN_HANDLER_CACHE.get(type);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		applicationContext.getBeansOfType(TokenHandler.class).values().forEach(tokenHandler -> {
			TOKEN_HANDLER_CACHE.put(tokenHandler.getName(), tokenHandler);
			log.info("[TOKEN_HANDLER] 新增handler {}: {}", tokenHandler.getName(), tokenHandler.toString());
		});

	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

}
