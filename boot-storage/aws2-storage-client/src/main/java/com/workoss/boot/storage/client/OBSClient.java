/*
 * Copyright 2019-2022 workoss (https://www.workoss.com)
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
package com.workoss.boot.storage.client;

import com.workoss.boot.storage.config.StorageClientConfig;
import com.workoss.boot.storage.model.StorageSignature;
import com.workoss.boot.storage.model.StorageStsToken;
import com.workoss.boot.storage.model.StorageType;
import software.amazon.awssdk.services.s3.S3AsyncClient;

/**
 * 华为云对象存储
 *
 * @author workoss
 */
public class OBSClient extends AbstractS3Client {

	@Override
	public StorageType type() {
		return StorageType.OBS;
	}

	@Override
	protected void initConfig(StorageClientConfig config) {

	}

	@Override
	protected S3AsyncClient createClient(StorageClientConfig config, StorageStsToken stsToken) {
		return createS3Client(config, config.getEndpoint(), stsToken);
	}

	@Override
	protected StorageStsToken getStsToken(StorageClientConfig config, String key, String action) {
		return requestSTSToken(config, key, action);
	}

	@Override
	protected StorageSignature generateSignagure(StorageClientConfig config, String key, String mimeType,
			String successActionStatus) {
		return requestSign(config, key, mimeType, successActionStatus);
	}

}
