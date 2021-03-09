package com.workoss.boot.storage.service.token;

import com.workoss.boot.storage.context.Context;
import com.workoss.boot.storage.model.STSToken;
import com.workoss.boot.storage.model.UploadSign;
import com.workoss.boot.storage.util.MustacheTemplateUtil;
import com.yifengx.popeye.util.StringUtils;
import com.yifengx.popeye.util.date.DateUtils;
import com.yifengx.popeye.util.security.CryptoUtil;
import com.yifengx.popeye.util.text.BaseEncodeUtil;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractTokenHandler implements TokenHandler {

	public static final String ALLOW_ALL = "*";

	protected abstract String getAction(String bucketName, String action);

	protected abstract String getResource(String bucketName, String action, String key);

	protected abstract String getDomain(final Context<String, String> context, String bucketName);

	protected String renderSecurityTokenPolicy(final Context<String, String> context, String bucketName, String key,
			String action) {
		Map<String, String> policyContext = new HashMap<>(2);
		policyContext.put("resource", getResource(bucketName, action, key));
		policyContext.put("action", getAction(bucketName, action));
		String policy = MustacheTemplateUtil.render(context.get("policy"), policyContext);
		return policy;
	}

	protected UploadSign generateWebSign(String policyTemplate, Context<String, String> context, STSToken stsToken,
			String bucketName, String key, String mimeType, String successActionStatus) {
		Long durationSeconds = Long.parseLong(context.get("token_duration_seconds", "1200"));
		LocalDateTime expireTime = DateUtils.plusSeconds(DateUtils.getCurrentDateTime(), durationSeconds - 5 * 60);

		Map<String, String> policyContext = new HashMap<>(8);
		policyContext.put("expiration", DateUtils.format(expireTime, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
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
			if (stToken.length() > 50) {
				stToken = stToken.substring(0, 50);
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
		uploadSign.setExpire(DateUtils.getMillis(expireTime));
		return uploadSign;
	}

}
