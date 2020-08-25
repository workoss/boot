package com.workoss.boot.extension;

@Extensible
public interface ExtensionFactory {

	<T>T getExtension(Class<T> tClass,String alias);

	<T>T getExtension(Class<T> tClass,String alias,ExtensionLoaderListener<T> listener);

}
