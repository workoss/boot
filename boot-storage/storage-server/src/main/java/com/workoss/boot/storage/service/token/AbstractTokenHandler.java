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
package com.workoss.boot.storage.service.token;

import com.workoss.boot.storage.model.STSToken;
import com.workoss.boot.storage.model.UploadSign;
import com.workoss.boot.storage.util.MustacheTemplateUtil;

import com.workoss.boot.util.DateUtil;
import com.workoss.boot.util.StringUtils;
import com.workoss.boot.util.context.Context;
import com.workoss.boot.util.security.CryptoUtil;
import com.workoss.boot.util.text.BaseEncodeUtil;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * token AbstractTokenHandler
 *
 * @author workoss
 */
public abstract class AbstractTokenHandler implements TokenHandler {

	public static final String ALLOW_ALL = "*";

	private static final int SIGN_SIZE = 50;

	/**
	 * 获取权限action
	 * @param bucketName 存储桶
	 * @param action 操作
	 * @return action字符串
	 */
	protected abstract String getAction(String bucketName, String action);

	/**
	 * 获取权限resource
	 * @param bucketName 存储桶
	 * @param action 操作
	 * @param key 文件key
	 * @return resource 字符串
	 */
	protected abstract String getResource(String bucketName, String action, String key);

	/**
	 * 获取存储桶域名
	 * @param context 上下文
	 * @param bucketName 存储桶
	 * @return 域名
	 */
	protected abstract String getDomain(final Context<String, String> context, String bucketName);

	protected String renderSecurityTokenPolicy(final Context<String, String> context, String bucketName, String key,
			String action) {
		Map<String, String> policyContext = new HashMap<>(2);
		policyContext.put("resource", getResource(bucketName, action, key));
		policyContext.put("action", getAction(bucketName, action));
		return MustacheTemplateUtil.render(context.get("policy"), policyContext);
	}

	protected UploadSign generateWebSign(String policyTemplate, Context<String, String> context, STSToken stsToken,
			String bucketName, String key, String mimeType, String successActionStatus) {
		long durationSeconds = Long.parseLong(context.get("token_duration_seconds", "1200"));
		LocalDateTime expireTime = DateUtil.plusSeconds(DateUtil.getCurrentDateTime(), durationSeconds - 5 * 60);

		Map<String, String> policyContext = new HashMap<>(8);
		policyContext.put("expiration", DateUtil.format(expireTime, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
		policyContext.put("bucketName", bucketName);
		policyContext.put("key", key);
		policyContext.put("maxUploadSize", context.get("max_upload_size", "10485760000"));
		if (StringUtils.isNotBlank(mimeType)) {
			policyContext.put("mimeType", mimeType);
		}
		if (StringUtils.isNotBlank(successActionStatus)) {
			policyContext.put("successActionStatus", successActionStatus);
		}
		if (stsToken != null) {
			String stToken = stsToken.getStsToken();
			if (stToken.length() > SIGN_SIZE) {
				stToken = stToken.substring(0, SIGN_SIZE);
			}
			policyContext.put("stsToken", stToken);
		}
		String policyText = MustacheTemplateUtil.render(policyTemplate, policyContext);
		String policyBase64 = BaseEncodeUtil.encodeBase64(policyText.getBytes(StandardCharsets.UTF_8));

		UploadSign uploadSign = new UploadSign();
		String accessKey = context.get("access_key");
		String secretKey = context.get("secret_key");
		if (stsToken != null) {
			accessKey = stsToken.getAccessKey();
			secretKey = stsToken.getSecretKey();
			uploadSign.setStsToken(stsToken.getStsToken());
		}

		byte[] hmacSha1 = CryptoUtil.hmacSha1(policyBase64.getBytes(StandardCharsets.UTF_8),
				secretKey.getBytes(StandardCharsets.UTF_8));
		String signature = BaseEncodeUtil.encodeBase64(hmacSha1);
		uploadSign.setAccessKey(accessKey);
		uploadSign.setHost(getDomain(context, bucketName));
		uploadSign.setKey(key);
		uploadSign.setPolicy(policyBase64);
		uploadSign.setSignature(signature);
		uploadSign.setExpire(DateUtil.getMillis(expireTime));
		return uploadSign;
	}

}
