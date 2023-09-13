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
package com.workoss.boot.storage.client;

import com.workoss.boot.storage.config.StorageClientConfig;
import com.workoss.boot.storage.model.StorageSignature;
import com.workoss.boot.storage.model.StorageStsToken;
import com.workoss.boot.storage.model.StorageType;
import com.workoss.boot.util.DateUtils;
import com.workoss.boot.util.StringUtils;
import com.workoss.boot.util.json.JsonMapper;
import com.workoss.boot.util.security.CryptoUtil;
import com.workoss.boot.util.text.BaseEncodeUtil;
import com.workoss.boot.util.web.MediaTypeFactory;
import software.amazon.awssdk.services.s3.S3AsyncClient;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

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
		if (StringUtils.isNotBlank(config.getTokenUrl())) {
			return requestSign(config, key, mimeType, successActionStatus);
		}
		// {"expiration":"2021-02-24T07:08:34.148Z","conditions":[{"x-obs-acl":"public-read"},{"bucket":"workoss"},{"key":"22.txt"},["content-length-range",0,
		// MAX_UPLOAD_SIZE]]}
		// 本地生成签名
		LocalDateTime expireTime = DateUtils.plusSeconds(DateUtils.getCurrentDateTime(), 1200 - 5 * 60);

		Map<String, Object> policyContext = new HashMap<>(8);
		policyContext.put("expiration", DateUtils.format(expireTime, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));

		List<Object> conditions = new ArrayList<>();
		conditions.add(Collections.singletonMap("bucket", config.getBucketName()));
		conditions.add(Collections.singletonMap("key", key));

		if (StringUtils.isBlank(mimeType)) {
			mimeType = MediaTypeFactory.getMediaType(key);
		}
		if (StringUtils.isNotBlank(mimeType)) {
			conditions.add(Collections.singletonMap("content-type", mimeType));
		}
		if (StringUtils.isNotBlank(successActionStatus)) {
			conditions.add(Collections.singletonMap("success_action_status", successActionStatus));
		}
		conditions.add(Arrays.asList("content-length-range", 1L, 10485760000L));
		policyContext.put("conditions", conditions);

		String finalMimeType = mimeType;
		return localSign(config, key, policyContext, storageSignature -> {
			storageSignature.setExpire(DateUtils.getMillis(expireTime));
			storageSignature.setMimeType(finalMimeType);
			storageSignature.setSuccessActionStatus(successActionStatus);
		});
	}

}
