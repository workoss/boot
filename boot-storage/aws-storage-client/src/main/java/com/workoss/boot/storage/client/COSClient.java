/*
 * Copyright 2019-2024 workoss (https://www.workoss.com)
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

import com.amazonaws.services.s3.AmazonS3;
import com.workoss.boot.storage.config.StorageClientConfig;
import com.workoss.boot.storage.exception.StorageException;
import com.workoss.boot.storage.model.StorageSignature;
import com.workoss.boot.storage.model.StorageStsToken;
import com.workoss.boot.storage.model.StorageType;

/**
 * 腾讯云对象存储
 *
 * @author workoss
 */
public class COSClient extends AbstractS3Client {

	@Override
	public StorageType type() {
		return StorageType.COS;
	}

	@Override
	protected void initConfig(StorageClientConfig config) {

	}

	@Override
	protected AmazonS3 createClient(StorageClientConfig config, StorageStsToken stsToken) {
		return createS3Client(config, config.getEndpoint(), stsToken);
	}

	@Override
	protected StorageStsToken getStsToken(StorageClientConfig config, String key, String action) {
		throw new StorageException("暂未实现");
	}

	@Override
	protected StorageSignature generateSignagure(StorageClientConfig config, String key, String mimeType,
			String successActionStatus) {
		throw new StorageException("暂未实现");
	}

}
