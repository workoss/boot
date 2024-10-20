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
package com.workoss.boot.extension;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ApplicationContextEvent;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.Ordered;

/**
 * ExtendsionApplicationContextListener order 效果不大
 *
 * @author workoss
 */
public class ExtendsionApplicationContextListener implements ApplicationListener<ApplicationContextEvent>, Ordered {

	private static final Logger log = LoggerFactory.getLogger(ExtendsionApplicationContextListener.class);

	@Override
	public void onApplicationEvent(ApplicationContextEvent applicationContextEvent) {
		ApplicationContext applicationContext = applicationContextEvent.getApplicationContext();
		if (applicationContext.getParent() == null) {
			SpringExtensionFactory.clearContexts();
		}
		if (applicationContext.getParent() != null && applicationContextEvent instanceof ContextRefreshedEvent) {
			log.info("ExtendsionApplicationContextListener add context, {}", applicationContextEvent);
			SpringExtensionFactory.addApplicationContext(applicationContext);
		}
		if (applicationContextEvent instanceof ContextClosedEvent) {
			SpringExtensionFactory.clearContexts();
		}

	}

	@Override
	public int getOrder() {
		return Ordered.LOWEST_PRECEDENCE;
	}

}
