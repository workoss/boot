/*
 * The MIT License
 * Copyright © 2020-2021 workoss
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
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
 * ExtendsionApplicationContextListener
 *
 * @author workoss
 */
public class ExtendsionApplicationContextListener implements ApplicationListener<ApplicationContextEvent>, Ordered {

	private static final Logger log = LoggerFactory.getLogger(ExtendsionApplicationContextListener.class);

	@Override
	public void onApplicationEvent(ApplicationContextEvent applicationContextEvent) {
		ApplicationContext applicationContext = applicationContextEvent.getApplicationContext();
		if (applicationContextEvent instanceof ContextRefreshedEvent && applicationContext.getParent() != null) {
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
