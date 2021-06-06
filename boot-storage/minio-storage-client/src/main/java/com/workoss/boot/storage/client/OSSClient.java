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
package com.workoss.boot.storage.client;

import com.workoss.boot.storage.config.StorageClientConfig;
import com.workoss.boot.storage.model.StorageSignature;
import com.workoss.boot.storage.model.StorageStsToken;
import com.workoss.boot.storage.model.StorageType;
import com.workoss.boot.storage.util.HttpUtil;
import com.workoss.boot.util.StringUtils;
import io.minio.MinioClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 阿里云 OSS 对象存储
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
	protected MinioClient createClient(StorageClientConfig config, StorageStsToken stsToken) {
		// endpoint 是否可以联通内网
		String endpoint = config.getEndpoint();
		if (!AVAIABLE_ENDPOINT.containsKey(endpoint)) {
			AVAIABLE_ENDPOINT.put(endpoint, endpoint);
			checkEndpointUrl(config.getEndpoint());
		}
		return createS3Client(config, endpoint, stsToken);
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

	private void checkEndpointUrl(String endpoint) {
		new Thread(() -> {
			initAvaiableEndpointUrl(endpoint);
		}, String.join("-", "STORAGE", type().name())).start();
	}

	private void initAvaiableEndpointUrl(String endpoint) {
		// 多线程 异步 填入 endpoint 对应的 内网地址
		String internalUrl = endpoint;
		if (!endpoint.contains("internal.")) {
			// 检测是否通 oss-cn-hangzhou.aliyuncs.com
			// oss-cn-hangzhou-internal.aliyuncs.com
			String host = endpoint.split("\\.")[0];
			internalUrl = endpoint.replace(host, host + "-internal");
		}
		boolean checkValid = HttpUtil.checkUrlIsValid(internalUrl, 500);
		if (checkValid) {
			AVAIABLE_ENDPOINT.put(endpoint, internalUrl);
			log.info("【STORAGE】OSS 地址:{} 内网可达，切换到内网请求", internalUrl);
		} else {
			AVAIABLE_ENDPOINT.put(endpoint, endpoint);
			log.info("【STORAGE】OSS 地址:{} 内网不可达，使用配置endpoint", endpoint);
		}
	}

}
