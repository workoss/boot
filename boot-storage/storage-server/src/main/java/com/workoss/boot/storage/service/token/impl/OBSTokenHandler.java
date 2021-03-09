package com.workoss.boot.storage.service.token.impl;

import com.huaweicloud.sdk.core.auth.GlobalCredentials;
import com.huaweicloud.sdk.core.auth.ICredential;
import com.huaweicloud.sdk.core.exception.ServiceResponseException;
import com.huaweicloud.sdk.iam.v3.IamClient;
import com.huaweicloud.sdk.iam.v3.model.*;
import com.huaweicloud.sdk.iam.v3.region.IamRegion;
import com.workoss.boot.storage.context.Context;
import com.workoss.boot.storage.exception.StorageException;
import com.workoss.boot.storage.model.STSToken;
import com.workoss.boot.storage.model.ThirdPlatformType;
import com.workoss.boot.storage.model.UploadSign;
import com.workoss.boot.storage.service.token.AbstractTokenHandler;
import com.yifengx.popeye.util.StringUtils;
import com.yifengx.popeye.util.date.DateUtils;
import com.yifengx.popeye.util.json.JsonMapper;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 华为OBS 处理
 *
 * @author workoss
 */
@Component
public class OBSTokenHandler extends AbstractTokenHandler {

	private static final Map<String, String> ACTION_CACHE = new ConcurrentHashMap<>();

	public OBSTokenHandler() {
		ACTION_CACHE.put("listBuckets", "bucket:ListBucket");
		ACTION_CACHE.put("doesBucketExist", "bucket:HeadBucket");
		ACTION_CACHE.put("listObjects", "*:*");
		ACTION_CACHE.put("getObject", "object:GetObject");
		ACTION_CACHE.put("doesObjectExist", "object:GetObject");
		ACTION_CACHE.put("putObject", "object:PutObject");
		ACTION_CACHE.put("downloadObject", "object:GetObject");
		ACTION_CACHE.put("deleteObject", "object:DeleteObject");
	}

	@Override
	public ThirdPlatformType getName() {
		return ThirdPlatformType.OBS;
	}

	/**
	 * 生成web签名 policy
	 * {"expiration":"2021-02-24T07:08:34.148Z","conditions":[{"x-obs-acl":"public-read"},{"bucket":"workoss"},{"key":"22.txt"},["content-length-range",
	 * 0, MAX_UPLOAD_SIZE]]}
	 * @param context
	 * @param bucketName
	 * @param key
	 * @return
	 */
	@Override
	public Mono<UploadSign> generateUploadSign(Context<String, String> context, String bucketName, String key,
			String mimeType, String successActionStatus) {
		String policyTemplate = "{\n" + "    \"expiration\":\"{{expiration}}\",\n"
				+ "    \"conditions\":[{\"bucket\":\"{{bucketName}}\"},\n" + "    {\"key\":\"{{key}}\"},\n"
				+ "    {{#mimeType}}{\"content-type\":\"{{mimeType}}\"},{{/mimeType}}\n"
				+ "    {{#successActionStatus}}{\"success_action_status\":\"{{successActionStatus}}\"},{{/successActionStatus}}\n"
				+ "    [\"content-length-range\", 0, {{maxUploadSize}}]]\n" + "}";
		return Mono
				.just(generateWebSign(policyTemplate, context, null, bucketName, key, mimeType, successActionStatus));
	}

	@Override
	public Mono<UploadSign> generateUploadStsSign(Context<String, String> context, String bucketName, String key,
			String mimeType, String successActionStatus) {
		return generateStsToken(context, bucketName, key, "putObject").flatMap(stsToken -> {
			String policyTemplate = "{\n" + "    \"expiration\":\"{{expiration}}\",\n"
					+ "    \"conditions\":[{\"bucket\":\"{{bucketName}}\"},\n" + "    {\"key\":\"{{key}}\"},\n"
					+ "    {{#mimeType}}{\"content-type\":\"{{mimeType}}\"},{{/mimeType}}\n"
					+ "    {{#successActionStatus}}{\"success_action_status\":\"{{successActionStatus}}\"},{{/successActionStatus}}\n"
					+ "    {{#stsToken}}[\"starts-with\", \"$x-obs-security-token\", \"{{stsToken}}\"],{{/stsToken}}\n"
					+ "    [\"content-length-range\", 0, {{maxUploadSize}}]]\n" + "}";
			return Mono.just(
					generateWebSign(policyTemplate, context, stsToken, bucketName, key, mimeType, successActionStatus));
		});
	}

	@Override
	public Mono<STSToken> generateStsToken(final Context<String, String> context, String bucketName, final String key,
			final String action) {
		String policy = renderSecurityTokenPolicy(context, bucketName, key, action);
		ICredential auth = new GlobalCredentials().withAk(context.get("access_key")).withSk(context.get("secret_key"));
		IamClient client = IamClient.newBuilder().withCredential(auth)
				.withRegion(IamRegion.valueOf(context.get("region"))).build();
		CreateTemporaryAccessKeyByAgencyRequest request = new CreateTemporaryAccessKeyByAgencyRequest();
		CreateTemporaryAccessKeyByAgencyRequestBody body = new CreateTemporaryAccessKeyByAgencyRequestBody();
		ServicePolicy policyIdentity = JsonMapper.parseObject(policy, ServicePolicy.class);
		AssumeroleSessionuser sessionUserAssumeRole = new AssumeroleSessionuser();
		sessionUserAssumeRole.withName(context.get("session_name", "popeye"));
		IdentityAssumerole assumeRoleIdentity = new IdentityAssumerole();
		assumeRoleIdentity.withAgencyName(context.get("agency_name")).withDomainName(context.get("domain_name"))
				.withDurationSeconds(Integer.parseInt(context.get("token_duration_seconds", "1200")))
				.withSessionUser(sessionUserAssumeRole);
		List<AgencyAuthIdentity.MethodsEnum> listIdentityMethods = new ArrayList<>();
		listIdentityMethods.add(AgencyAuthIdentity.MethodsEnum.fromValue("assume_role"));
		AgencyAuthIdentity identityAuth = new AgencyAuthIdentity();
		identityAuth.withMethods(listIdentityMethods).withAssumeRole(assumeRoleIdentity).withPolicy(policyIdentity);
		AgencyAuth authbody = new AgencyAuth();
		authbody.withIdentity(identityAuth);
		body.withAuth(authbody);
		request.withBody(body);
		try {
			CreateTemporaryAccessKeyByAgencyResponse response = client.createTemporaryAccessKeyByAgency(request);
			Credential credential = response.getCredential();
			STSToken stsToken = new STSToken();
			stsToken.setStsToken(credential.getSecuritytoken());
			stsToken.setAccessKey(credential.getAccess());
			stsToken.setSecretKey(credential.getSecret());
			stsToken.setExpiration(
					DateUtils.parse(credential.getExpiresAt(), "yyyy-MM-dd'T'HH:mm:ss.SSSSSSS'Z'").plusHours(8));
			String endpoint = getDomain(context, bucketName);
			stsToken.setEndpoint(endpoint);
			return Mono.just(stsToken);
		}
		catch (ServiceResponseException e) {
			return Mono.error(new StorageException("10001", e.toString()));
		}
	}

	@Override
	protected String getAction(String bucketName, String action) {
		return ACTION_CACHE.getOrDefault(action, "*:*");
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
	protected String getDomain(Context<String, String> context, String bucketName) {
		return IamRegion.valueOf(context.get("region")).getEndpoint().replaceAll("iam", bucketName + ".obs");
	}

}
