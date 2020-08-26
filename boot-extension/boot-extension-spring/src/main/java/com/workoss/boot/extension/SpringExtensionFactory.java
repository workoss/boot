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

import com.workoss.boot.util.concurrent.ConcurrentHashSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Set;

/**
 * spring 扩展
 *
 * @author workoss
 */
@Extension(value = "spring", order = 1, override = true)
public class SpringExtensionFactory implements ExtensionFactory {

	private static final Logger log = LoggerFactory.getLogger(SpringExtensionFactory.class);

	private static final Set<ApplicationContext> CONTEXTS = new ConcurrentHashSet<ApplicationContext>();

	@Override
	public <T> T getExtension(Class<T> tClass, String alias) {
		return getExtension(tClass, alias, null);
	}

	@Override
	public <T> T getExtension(Class<T> tClass, String alias, ExtensionLoaderListener<T> listener) {
		if (!tClass.isInterface()) {
			throw new ExtensionException("class " + tClass + " must be interface");
		}
		for (ApplicationContext context : CONTEXTS) {
			T bean = context.getBean(alias, tClass);
			if (bean != null) {
				return bean;
			}
			bean = (T) context.getBean(alias);
			if (bean != null) {
				return bean;
			}
			bean = (T) context.getBean(tClass);
			if (bean != null) {
				return bean;
			}
		}
		return null;
	}

	public static void addApplicationContext(ApplicationContext context) {
		CONTEXTS.add(context);
		if (context instanceof ConfigurableApplicationContext) {
			((ConfigurableApplicationContext) context).registerShutdownHook();
		}
	}

	public static void removeApplicationContext(ApplicationContext context) {
		CONTEXTS.remove(context);
	}

	public static Set<ApplicationContext> getContexts() {
		return CONTEXTS;
	}

	/**
	 * currently for test purpose
	 */
	public static void clearContexts() {
		CONTEXTS.clear();
	}

}
