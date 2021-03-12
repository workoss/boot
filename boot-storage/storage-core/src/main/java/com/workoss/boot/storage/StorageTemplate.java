/*
 * Copyright 2019-2021 workoss (https://www.workoss.com)
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
package com.workoss.boot.storage;

import com.workoss.boot.annotation.lang.NonNull;
import com.workoss.boot.storage.client.StorageClient;
import com.workoss.boot.storage.config.MultiStorageClientConfig;

import java.util.Map;

public interface StorageTemplate {

	void setMultiStorageClientConfig(MultiStorageClientConfig multiStorageClientConfig);

	StorageClient client();

	StorageClient client(@NonNull String key);

	Map<String, StorageClient> allClients();

	void destroy() throws Exception;

	void afterPropertiesSet() throws Exception;

}
