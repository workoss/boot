/*
 * Copyright © 2020-2021 workoss (WORKOSS)
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
package com.workoss.boot.storage.health;

import com.workoss.boot.storage.StorageTemplate;
import com.workoss.boot.storage.client.StorageClient;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;

public class StorageHealthIndicator extends AbstractHealthIndicator {

	private final StorageTemplate storageTemplate;

	public StorageHealthIndicator(StorageTemplate storageTemplate) {
		this.storageTemplate = storageTemplate;
	}

	@Override
	protected void doHealthCheck(Health.Builder builder) throws Exception {
		if (this.storageTemplate == null) {
			builder.up().withDetail("storage", Status.UNKNOWN.getCode());
			return;
		}
		builder.up().withDetail("storage", "StorageClient");
		this.storageTemplate.allClients().forEach((key, storageClient) -> {
			builder.withDetail(key, doClientCheck(storageClient));
		});

	}

	protected String doClientCheck(StorageClient storageClient) {
		try {
			return storageClient.listBuckets() != null ? Status.UP.getCode() : Status.OUT_OF_SERVICE.getCode();
		}
		catch (Exception e) {
			return Status.DOWN.getCode();
		}
	}

}
