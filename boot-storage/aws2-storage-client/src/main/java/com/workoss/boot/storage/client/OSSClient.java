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
import com.workoss.boot.storage.util.HttpUtil;
import com.workoss.boot.util.DateUtils;
import com.workoss.boot.util.StringUtils;
import com.workoss.boot.util.web.MediaTypeFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3AsyncClientBuilder;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 阿里云OSS 对象存储
 *
 * @author workoss
 */
public class OSSClient extends AbstractS3Client {

	private static final Logger log = LoggerFactory.getLogger(OSSClient.class);

	private static ConcurrentHashMap<String, String> AVAIABLE_ENDPOINT = new ConcurrentHashMap<>();

	@Override
	public StorageType type() {
		return StorageType.OSS;
	}

	@Override
	protected void initConfig(StorageClientConfig config) {
		if (StringUtils.isBlank(config.getEndpoint())) {
			return;
		}
		checkEndpointUrl(config.getEndpoint());
	}

	@Override
	protected S3AsyncClient createClient(StorageClientConfig config, StorageStsToken stsToken) {
		// endpoint 是否可以联通内网
		String endpoint = config.getEndpoint();
		if (!AVAIABLE_ENDPOINT.containsKey(endpoint)) {
			AVAIABLE_ENDPOINT.put(endpoint, endpoint);
			checkEndpointUrl(config.getEndpoint());
		}
		return createS3Client(config, endpoint, stsToken);
	}

	private void checkEndpointUrl(String endpoint) {
		CompletableFuture.runAsync(() -> initAvaiableEndpointUrl(endpoint)).join();
	}

	private void initAvaiableEndpointUrl(String endpoint) {
		// 多线程 异步 填入 endpoint 对应的 内网地址
		String internalUrl = endpoint;
		if (!endpoint.contains("internal.")) {
			// 检测是否通 oss-cn-hangzhou.aliyuncs.com oss-cn-hangzhou-internal.aliyuncs.com
			String host = endpoint.split("\\.")[0];
			internalUrl = endpoint.replace(host, host + "-internal");
		}
		boolean checkValid = HttpUtil.checkUrlIsValid(internalUrl, 500);
		if (checkValid) {
			AVAIABLE_ENDPOINT.put(endpoint, internalUrl);
			log.info("【STORAGE】OSS 地址:{} 内网可达，切换到内网请求", internalUrl);
		}
		else {
			AVAIABLE_ENDPOINT.put(endpoint, endpoint);
			log.info("【STORAGE】OSS 地址:{} 内网不可达，使用配置endpoint", endpoint);
		}
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
		//{"expiration":"2021-02-24T07:08:34.148Z","conditions":[{"x-obs-acl":"public-read"},{"bucket":"workoss"},{"key":"22.txt"},["content-length-range",0, MAX_UPLOAD_SIZE]]}
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
		conditions.add(Arrays.asList("content-length-range",1L, 10485760000L));
		policyContext.put("conditions", conditions);

		String finalMimeType = mimeType;
		return localSign(config, key, policyContext, storageSignature -> {
			storageSignature.setExpire(DateUtils.getMillis(expireTime));
			storageSignature.setMimeType(finalMimeType);
			storageSignature.setSuccessActionStatus(successActionStatus);
		});
	}

}
