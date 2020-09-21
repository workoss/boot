package com.workoss.boot.util.plugin;

import java.util.Iterator;

/**
 * 插件管理器
 *
 * @author workoss
 */
public interface PluginManager {
	/**
	 * 插件加载
	 * @param service 接口
	 * @param <P> 类
	 * @return Service.loader()
	 */
	<P> Iterator<P> load(Class<P> service);

}
