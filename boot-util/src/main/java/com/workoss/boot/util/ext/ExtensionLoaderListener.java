package com.workoss.boot.util.ext;

/**
 * @author: workoss
 * @date: 2018-12-13 17:12
 * @version:
 */
@FunctionalInterface
public interface ExtensionLoaderListener<T> {
    /**
     * 当扩展点加载时，触发的事件
     *
     * @param extensionClass 扩展点类对象
     */
    void onLoad(ExtensionClass<T> extensionClass);
}
