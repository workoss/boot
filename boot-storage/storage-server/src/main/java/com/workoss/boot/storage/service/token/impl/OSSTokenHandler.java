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
package com.workoss.boot.storage.service.token.impl;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.sts.model.v20150401.AssumeRoleRequest;
import com.aliyuncs.sts.model.v20150401.AssumeRoleResponse;
import com.workoss.boot.storage.exception.StorageException;
import com.workoss.boot.storage.model.STSToken;
import com.workoss.boot.storage.model.ThirdPlatformType;
import com.workoss.boot.storage.model.UploadSign;
import com.workoss.boot.storage.service.token.AbstractTokenHandler;
import com.workoss.boot.util.DateUtil;
import com.workoss.boot.util.StringUtils;
import com.workoss.boot.util.context.Context;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.StringJoiner;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 阿里oss 处理 action:
 *
 * @author workoss
 */
@SuppressWarnings("ALL")
@Component
public class OSSTokenHandler extends AbstractTokenHandler {

	private static final Map<String, String> REGION_CACHE = new ConcurrentHashMap<>();

	private static final Map<String, String> ACTION_CACHE = new ConcurrentHashMap<>();

	public OSSTokenHandler() {
		REGION_CACHE.put("cn-qingdao", "oss-cn-qingdao.aliyuncs.com");
		REGION_CACHE.put("cn-beijing", "oss-cn-beijing.aliyuncs.com");
		REGION_CACHE.put("cn-hangzhou", "oss-cn-hangzhou.aliyuncs.com");
		REGION_CACHE.put("cn-shanghai", "oss-cn-shanghai.aliyuncs.com");
		REGION_CACHE.put("cn-hongkong", "oss-cn-hongkong.aliyuncs.com");
		REGION_CACHE.put("cn-shenzhen", "oss-cn-shenzhen.aliyuncs.com");
		REGION_CACHE.put("ap-southeast-1", "oss-ap-southeast-1.aliyuncs.com");
		REGION_CACHE.put("us-west-1", "oss-us-west-1.aliyuncs.com");

		ACTION_CACHE.put("listBuckets", "ListBuckets");
		ACTION_CACHE.put("doesBucketExist", "HeadBucket");
		ACTION_CACHE.put("listObjects", "ListObjects");
		ACTION_CACHE.put("getObject", "GetObject");
		ACTION_CACHE.put("doesObjectExist", "GetObject");
		ACTION_CACHE.put("putObject", "putObject");
		ACTION_CACHE.put("downloadObject", "GetObject");
		ACTION_CACHE.put("deleteObject", "DeleteObject");
	}

	@Override
	public ThirdPlatformType getName() {
		return ThirdPlatformType.OSS;
	}

	@Override
	public Mono<UploadSign> generateUploadSign(Context<String, String> context, String bucketName, String key,
			String mimeType, String successActionStatus) {
		String policyTemplate = "{\"expiration\":\"{{expiration}}\",\"conditions\":[{\"bucket\":\"{{bucketName}}\"},{\"key\":\"{{key}}\"},{{#mimeType}}{\"content-type\":\"{{mimeType}}\"},{{/mimeType}}[\"content-length-range\", 0, {{maxUploadSize}}]]}";
		return Mono
			.just(generateWebSign(policyTemplate, context, null, bucketName, key, mimeType, successActionStatus));
	}

	@Override
	public Mono<UploadSign> generateUploadStsSign(Context<String, String> context, String bucketName, String key,
			String mimeType, String successActionStatus) {
		return generateStsToken(context, bucketName, key, "putObject").flatMap(stsToken -> {
			String policyTemplate = "{\"expiration\":\"{{expiration}}\",\"conditions\":[{\"bucket\":\"{{bucketName}}\"},{\"key\":\"{{key}}\"},{{#mimeType}}{\"content-type\":\"{{mimeType}}\"},{{/mimeType}}[\"content-length-range\", 0, {{maxUploadSize}}]]}";
			return Mono.just(
					generateWebSign(policyTemplate, context, stsToken, bucketName, key, mimeType, successActionStatus));
		});
	}

	@Override
	public Mono<STSToken> generateStsToken(Context<String, String> context, String bucketName, String key,
			String action) {
		String policy = renderSecurityTokenPolicy(context, bucketName, key, action);
		DefaultProfile profile = DefaultProfile.getProfile(context.get("region"), context.get("access_key"),
				context.get("secret_key"));
		IAcsClient client = new DefaultAcsClient(profile);
		AssumeRoleRequest request = new AssumeRoleRequest();
		request.setDurationSeconds(Long.parseLong(context.get("token_duration_seconds", "1200")));
		request.setPolicy(policy);
		request.setRoleArn(context.get("role_arn"));
		request.setRoleSessionName(context.get("session_name", "popeye"));
		try {
			AssumeRoleResponse response = client.getAcsResponse(request);
			AssumeRoleResponse.Credentials credentials = response.getCredentials();
			STSToken stsToken = new STSToken();
			stsToken.setStsToken(credentials.getSecurityToken());
			stsToken.setAccessKey(credentials.getAccessKeyId());
			stsToken.setSecretKey(credentials.getAccessKeySecret());
			stsToken
				.setExpiration(DateUtil.parse(credentials.getExpiration(), "yyyy-MM-dd'T'HH:mm:ss'Z'").plusHours(8));
			// 放入域名
			stsToken.setEndpoint(getDomain(context, bucketName));
			return Mono.just(stsToken);
		}
		catch (ClientException e) {
			return Mono.error(new StorageException("10002", e.toString()));
		}
	}

	@Override
	protected String getAction(String bucketName, String action) {
		return ACTION_CACHE.getOrDefault(action, ALLOW_ALL);
	}

	@Override
	protected String getResource(String bucketName, String action, String key) {
		if ("listBuckets".equalsIgnoreCase(action) || StringUtils.isBlank(bucketName)) {
			return ALLOW_ALL;
		}
		StringJoiner resource = new StringJoiner("/");
		if ("listObjects".equalsIgnoreCase(action) || action.contains("Bucket") || StringUtils.isBlank(key)) {
			return resource.add(bucketName).toString();
		}
		return resource.add(bucketName).add(key).toString();
	}

	@Override
	protected String getDomain(final Context<String, String> context, String bucketName) {
		return String.format("https://%s.%s", bucketName, REGION_CACHE.get(context.get("region")));
	}

}
