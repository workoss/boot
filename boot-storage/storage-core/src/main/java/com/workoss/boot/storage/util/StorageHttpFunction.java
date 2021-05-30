package com.workoss.boot.storage.util;

import com.workoss.boot.annotation.lang.NonNull;
import com.workoss.boot.annotation.lang.Nullable;

import java.util.Map;

@FunctionalInterface
public interface StorageHttpFunction {

	String apply(@NonNull String url, @Nullable String jsonParam,
				 @Nullable Map<String, String> headers);
}
