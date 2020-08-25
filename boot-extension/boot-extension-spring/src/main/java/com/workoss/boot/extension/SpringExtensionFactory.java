package com.workoss.boot.extension;

import com.workoss.boot.util.concurrent.ConcurrentHashSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Set;

/**
 * spring 扩展
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
		if (!tClass.isInterface()){
			throw new ExtensionException("class "+tClass+" must be interface");
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

	// currently for test purpose
	public static void clearContexts() {
		CONTEXTS.clear();
	}

}
