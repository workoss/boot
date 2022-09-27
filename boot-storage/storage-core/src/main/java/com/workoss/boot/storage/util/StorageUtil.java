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
package com.workoss.boot.storage.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.workoss.boot.storage.config.StorageClientConfig;
import com.workoss.boot.storage.exception.StorageException;
import com.workoss.boot.storage.model.StorageSignature;
import com.workoss.boot.storage.model.StorageStsToken;
import com.workoss.boot.util.Assert;
import com.workoss.boot.util.StringUtils;
import com.workoss.boot.util.json.JsonMapper;
import com.workoss.boot.util.web.MediaTypeFactory;

import java.io.File;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * 对象存储工具类
 *
 * @author workoss
 */
@SuppressWarnings("unused")
public class StorageUtil {

	public static final String DOT = ".";

	public static final String SLASH = "/";

	public static final String DOUBLE_SLASH = "//";

	private StorageUtil() {
	}

	public static String getMimeType(File file) {
		String path = null;
		if (file != null) {
			path = file.toPath().getFileName().toString();
		}
		return getMimeType(path);
	}

	public static String getMimeType(String path) {
		return MediaTypeFactory.getMediaType(path, "application/octet-stream");
	}

	public static String replaceEndSlash(String url) {
		if (StringUtils.isBlank(url)) {
			return url;
		}
		url = url.trim();
		if (!url.endsWith(StorageUtil.SLASH)) {
			return url;
		}
		return url.substring(0, url.length() - 1);
	}

	public static String replaceStartSlash(String url) {
		if (StringUtils.isBlank(url)) {
			return url;
		}
		url = url.trim();
		if (!url.startsWith(StorageUtil.SLASH)) {
			return url;
		}
		return url.replaceFirst(StorageUtil.SLASH, StringUtils.EMPTY);
	}

	public static String replaceStartEndSlash(String url) {
		url = replaceEndSlash(url);
		return replaceStartSlash(url);
	}

	public static String formatHost(StorageClientConfig conf) {
		if (StringUtils.isNotBlank(conf.getDomain())) {
			return conf.getDomain();
		}
		String endpoint = conf.getEndpoint();
		URI uri = URI.create(endpoint);
		return String.format("%s://%s.%s%s", uri.getScheme(), conf.getBucketName(), uri.getHost(), uri.getPath());
	}

	public static String formatKey(String basePath, String key, boolean isDir) {
		if (key == null) {
			key = StringUtils.EMPTY;
		}
		URI uri = URI.create(key);
		// 去掉域名host
		key = uri.getPath().replaceAll(StorageUtil.DOUBLE_SLASH, StorageUtil.SLASH);
		// 开始/去掉
		key = StorageUtil.replaceStartEndSlash(key);
		// 传入完整url 并且path以 basePath 开头， 需要去除
		if (StringUtils.isNotBlank(uri.getHost())) {
			StringBuilder stringBuilder = new StringBuilder(key);
			if (isDir) {
				stringBuilder.append(StorageUtil.SLASH);
			}
			return stringBuilder.toString();
		}
		if (StringUtils.isBlank(basePath)) {
			return key;
		}
		StringBuilder stringBuilder = new StringBuilder(basePath).append(StorageUtil.SLASH).append(key);
		if (isDir) {
			stringBuilder.append(StorageUtil.SLASH);
		}
		return stringBuilder.toString();
	}

	public static String generateCacheKey(String action, String key) {
		Assert.hasLength(action, "action不能为空");
		return String.format("%s:%s", action, key);
	}

	public static StorageStsToken requestSTSToken(StorageHttpFunction httpFunc, StorageClientConfig config, String key,
												  String action) {
		String url = formatTokenUrl(config.getTokenUrl()) + "/security/ststoken";
		String paramJson = StorageUtil.buildStsTokenParam(config, key, action);
		return request(url, paramJson, httpFunc, jsonNode -> {
			JsonNode dataNode = jsonNode.get("data");
			StorageStsToken stsToken = JsonMapper.convertValue(dataNode, StorageStsToken.class);
			boolean check = StringUtils.isNotBlank(stsToken.getAccessKey())
					&& StringUtils.isNotBlank(stsToken.getSecretKey()) && StringUtils.isNotBlank(stsToken.getStsToken())
					&& stsToken.getExpiration() != null;
			if (!check) {
				throw new StorageException("00001", "返回结果不正常");
			}
			return stsToken;
		});

	}

	public static StorageSignature requestSign(StorageHttpFunction httpFunc, StorageClientConfig config, String key,
											   String mimeType, String successActionStatus) {
		String url = formatTokenUrl(config.getTokenUrl()) + "/security/stssign";
		String paramJson = StorageUtil.buildSignatureParam(config, key, mimeType, successActionStatus);
		return request(url, paramJson, httpFunc, jsonNode -> {
			StorageSignature storageSignature = JsonMapper.convertValue(jsonNode.get("data"), StorageSignature.class);
			boolean check = StringUtils.isNotBlank(storageSignature.getAccessKey())
					&& StringUtils.isNotBlank(storageSignature.getSignature())
					&& StringUtils.isNotBlank(storageSignature.getPolicy());
			if (!check) {
				throw new StorageException("00001", "返回结果不正常");
			}
			// config 设置了domain 覆盖
			if (StringUtils.isNotBlank(config.getDomain())) {
				storageSignature.setHost(config.getDomain());
			}
			return storageSignature;
		});
	}

	public static <T> T request(String url, String body, StorageHttpFunction httpFunc,
								Function<JsonNode, T> resultFun) {
		Map<String, String> header = new HashMap<>();
		header.put("X-SDK-CLIENT", "storage");
		String resp = httpFunc.apply(url, body, header);
		if (StringUtils.isBlank(resp)) {
			throw new StorageException("00001", "请求:" + url + "失败");
		}
		JsonNode jsonNode = JsonMapper.parse(resp);
		if (!jsonNode.has("code") || !jsonNode.has("data")) {
			throw new StorageException(resp);
		}
		String code = jsonNode.get("code").asText();
		if (!"0".equalsIgnoreCase(code)) {
			throw new StorageException(code, jsonNode.has("message") ? jsonNode.get("message").asText() : null);
		}
		return resultFun.apply(jsonNode);
	}

	private static String formatTokenUrl(String tokenUrl) {
		if (tokenUrl.endsWith(SLASH)) {
			tokenUrl = tokenUrl.substring(0, tokenUrl.length() - 1);
		}
		return tokenUrl;
	}

	public static String buildStsTokenParam(StorageClientConfig config, String key, String action) {
		String paramJson = "{\"tenentId\": \"%s\", \"storageType\": \"%s\", \"bucketName\": \"%s\", \"action\": \"%s\", \"key\": \"%s\"}";
		return String.format(paramJson, (config.getTenentId() == null ? StringUtils.EMPTY : config.getTenentId()),
				(config.getStorageType() == null ? StringUtils.EMPTY : config.getStorageType().name()),
				(config.getBucketName() == null ? StringUtils.EMPTY : config.getBucketName()),
				(action == null ? StringUtils.EMPTY : action), (key == null ? StringUtils.EMPTY : key));
	}

	public static String buildSignatureParam(StorageClientConfig config, String key, String mimeType,
											 String successActionStatus) {
		String paramJson = "{\"tenentId\": \"%s\", \"storageType\": \"%s\", \"bucketName\": \"%s\", \"key\": \"%s\", \"mimeType\": \"%s\", \"successActionStatus\": \"%s\"}";
		return String.format(paramJson, (config.getTenentId() == null ? StringUtils.EMPTY : config.getTenentId()),
				(config.getStorageType() == null ? StringUtils.EMPTY : config.getStorageType().name()),
				(config.getBucketName() == null ? StringUtils.EMPTY : config.getBucketName()),
				(key == null ? StringUtils.EMPTY : key), (mimeType == null ? StringUtils.EMPTY : mimeType),
				(successActionStatus == null ? StringUtils.EMPTY : successActionStatus));
	}

}
