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
package com.workoss.boot.extension;

import com.workoss.boot.util.StringUtils;
import com.workoss.boot.util.concurrent.ConcurrentHashSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.ObjectUtils;

import java.util.Set;

/**
 * spring 扩展
 *
 * @author workoss
 */
@Extension(value = "spring", order = 1, override = true)
public class SpringExtensionFactory implements ExtensionFactory {

	private static final Logger log = LoggerFactory.getLogger(SpringExtensionFactory.class);

	private static final Set<ApplicationContext> CONTEXTS = new ConcurrentHashSet();

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
			T tBean = getBean(context, tClass, alias);
			if (tBean == null) {
				continue;
			}
			return tBean;
		}
		return null;
	}

	protected <T> T getBean(ListableBeanFactory beanFactory, Class<T> beanType, String beanName) {
		if (StringUtils.isBlank(beanName)) {
			return beanFactory.getBean(beanType);
		}
		String[] allBeanNames = BeanFactoryUtils.beanNamesForTypeIncludingAncestors(beanFactory, beanType, true, false);
		if (ObjectUtils.containsElement(allBeanNames, beanName)) {
			return beanFactory.getBean(beanName, beanType);
		}
		return null;
	}

	public static void addApplicationContext(ApplicationContext context) {
		if (!CONTEXTS.contains(context)) {
			CONTEXTS.add(context);
		}
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
