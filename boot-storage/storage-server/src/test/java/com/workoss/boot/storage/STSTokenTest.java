package com.workoss.boot.storage;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.endpoint.DefaultEndpointResolver;
import com.aliyuncs.endpoint.ResolveEndpointRequest;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.sts.model.v20150401.AssumeRoleRequest;
import com.aliyuncs.sts.model.v20150401.AssumeRoleResponse;
import com.google.gson.Gson;
import com.huaweicloud.sdk.core.auth.GlobalCredentials;
import com.huaweicloud.sdk.core.auth.ICredential;
import com.huaweicloud.sdk.core.exception.ConnectionException;
import com.huaweicloud.sdk.core.exception.RequestTimeoutException;
import com.huaweicloud.sdk.core.exception.ServiceResponseException;
import com.huaweicloud.sdk.iam.v3.IamClient;
import com.huaweicloud.sdk.iam.v3.model.*;
import com.huaweicloud.sdk.iam.v3.region.IamRegion;
import com.workoss.boot.storage.model.STSToken;
import com.yifengx.popeye.util.date.DateUtils;
import com.yifengx.popeye.util.json.JsonMapper;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class STSTokenTest {

	@Test
	void testRegion() {
		ResolveEndpointRequest request = new ResolveEndpointRequest("cn-south-1", "oss", "oss",
				ResolveEndpointRequest.ENDPOINT_TYPE_OPEN);
		String domain = DefaultEndpointResolver.predefinedEndpointResolver.resolve(request);
		System.out.println(domain);
	}

	@Test
	void testHuawei() {
		String ak = "8ACZFOMKJUAAY1XQWKXR";
		String sk = "Ftt34DyBSiGJ4GX82eans8CMxKaZBBilQBC9oC64";

		ICredential auth = new GlobalCredentials().withAk(ak).withSk(sk);

		IamClient client = IamClient.newBuilder().withCredential(auth).withRegion(IamRegion.CN_SOUTH_1).build();
		CreateTemporaryAccessKeyByTokenRequest request = new CreateTemporaryAccessKeyByTokenRequest();
		CreateTemporaryAccessKeyByTokenRequestBody body = new CreateTemporaryAccessKeyByTokenRequestBody();
		// List<String> listStatementResource = new ArrayList<>();
		// listStatementResource.add("obs:*:*:*:*");
		// List<String> listStatementAction = new ArrayList<>();
		// listStatementAction.add("obs:*");
		// List<ServiceStatement> listPolicyStatement = new ArrayList<>();
		// listPolicyStatement.add(new ServiceStatement().withAction(listStatementAction)
		// .withEffect(ServiceStatement.EffectEnum.fromValue("Allow")).withResource(listStatementResource));
		// ServicePolicy policyIdentity = new ServicePolicy();
		// policyIdentity.withVersion("1.1").withStatement(listPolicyStatement);

		String policy = "{\"Version\":\"1.1\",\"Statement\":[{\"Action\":[\"obs:*\"],\"Effect\":\"Allow\",\"Resource\":[\"obs:*:*:*:*\"]}]}";
		ServicePolicy policyIdentity = JsonMapper.parseObject(policy, ServicePolicy.class);

		List<TokenAuthIdentity.MethodsEnum> listIdentityMethods = new ArrayList<>();
		listIdentityMethods.add(TokenAuthIdentity.MethodsEnum.fromValue("token"));
		TokenAuthIdentity identityAuth = new TokenAuthIdentity();
		identityAuth.withMethods(listIdentityMethods).withPolicy(policyIdentity);

		TokenAuth authbody = new TokenAuth();
		authbody.withIdentity(identityAuth);
		body.withAuth(authbody);
		request.withBody(body);
		System.out.println(request.toString());
		try {
			CreateTemporaryAccessKeyByTokenResponse response = client.createTemporaryAccessKeyByToken(request);
			System.out.println(response.toString());
			test(response.getCredential().getAccess(), response.getCredential().getSecret(),
					response.getCredential().getSecuritytoken(), "obs", "https://obs.cn-south-1.myhuaweicloud.com",
					"img-test1");
		}
		catch (ConnectionException e) {
			e.printStackTrace();
		}
		catch (RequestTimeoutException e) {
			e.printStackTrace();
		}
		catch (ServiceResponseException e) {
			e.printStackTrace();
			System.out.println(e.getHttpStatusCode());
			System.out.println(e.getErrorCode());
			System.out.println(e.getErrorMsg());
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	void testHuawei2() {
		String ak = "ZZXXUWJBPM5JS9DDM6ZR";
		String sk = "pTCBOVf8NbdUCzOE2w06hQz6XkURz4648MCALAbM";

		ICredential auth = new GlobalCredentials().withAk(ak).withSk(sk);
		IamClient client = IamClient.newBuilder().withCredential(auth).withRegion(IamRegion.CN_SOUTH_1).build();
		CreateTemporaryAccessKeyByAgencyRequest agencyRequest = new CreateTemporaryAccessKeyByAgencyRequest();

		String reqBody = "{\"auth\":{\"identity\":{\"methods\":[\"assume_role\"],\"assume_role\":{\"agency_name\":\"obs_token\",\"domain_name\":\"workoss\",\"duration_seconds\":1200},\"policy\":{\"Version\":\"1.1\",\"Statement\":[{\"Action\":[\"obs:object:*\"],\"Effect\":\"Allow\",\"Resource\":[\"obs:*:*:object:workoss/*\"]}]}}}}";

		// CreateTemporaryAccessKeyByAgencyRequestBody agencyRequestBody = new
		// CreateTemporaryAccessKeyByAgencyRequestBody();
		// String policy =
		// "{\"Version\":\"1.1\",\"Statement\":[{\"Action\":[\"obs:*\"],\"Effect\":\"Allow\",\"Resource\":[\"obs:*:*:*:*\"]}]}";
		// ServicePolicy policyIdentity =
		// JsonMapper.parseObject(policy,ServicePolicy.class);
		// AgencyAuth agencyAuth = new AgencyAuth().withIdentity(new AgencyAuthIdentity()
		// .addMethodsItem(AgencyAuthIdentity.MethodsEnum.ASSUME_ROLE)
		// .withPolicy(policyIdentity)
		// .withAssumeRole(new
		// IdentityAssumerole().withDurationSeconds(900).withAgencyName("obs_token").withDomainName("workoss"))
		// );
		// agencyRequestBody.setAuth(agencyAuth);

		CreateTemporaryAccessKeyByAgencyRequestBody agencyRequestBody = JsonMapper.parseObject(reqBody,
				CreateTemporaryAccessKeyByAgencyRequestBody.class);

		agencyRequest.setBody(agencyRequestBody);

		System.out.println(JsonMapper.toJSONString(agencyRequest));

		try {
			CreateTemporaryAccessKeyByAgencyResponse response = client.createTemporaryAccessKeyByAgency(agencyRequest);
			System.out.println(response.toString());
			test(response.getCredential().getAccess(), response.getCredential().getSecret(),
					response.getCredential().getSecuritytoken(), "obs", "https://obs.cn-south-1.myhuaweicloud.com",
					"workoss");
			Credential credential = response.getCredential();
			STSToken stsToken = new STSToken();
			stsToken.setStsToken(credential.getSecuritytoken());
			stsToken.setAccessKey(credential.getAccess());
			stsToken.setSecretKey(credential.getSecret());
			stsToken.setExpiration(
					DateUtils.parse(credential.getExpiresAt(), "yyyy-MM-dd'T'HH:mm:ss.SSSSSSS'Z'").plusHours(8));
			System.out.println(stsToken.toString());
		}
		catch (ConnectionException e) {
			e.printStackTrace();
		}
		catch (RequestTimeoutException e) {
			e.printStackTrace();
		}
		catch (ServiceResponseException e) {
			e.printStackTrace();
			System.out.println(e.getHttpStatusCode());
			System.out.println(e.getErrorCode());
			System.out.println(e.getErrorMsg());
		}
		catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Test
	void testOSS() {
		String accessKeyId = "LTAI4G3Apnzprof18S52qTs7";
		String accessKeySecret = "n7nLNV7MgmE7OxONClNfVb6qNb9F2I";
		String roleArn = "acs:ram::1238831582674451:role/aliyunosstokengeneratorrole";
		String roleSessionName = "alice";

		String policy = "{\n" + "    \"Version\": \"1\",\n" + "    \"Statement\": [\n" + "     {\n"
				+ "           \"Effect\": \"Allow\",\n" + "           \"Action\": [\n" + "             \"oss:*\"\n"
				+ "           ],\n" + "           \"Resource\": [\n" + "             \"acs:oss:*:*:*\""
				+ "           ]\n" + "     }\n" + "    ]\n" + "}";

		DefaultProfile profile = DefaultProfile.getProfile("cn-shenzhen", accessKeyId, accessKeySecret);
		IAcsClient client = new DefaultAcsClient(profile);

		AssumeRoleRequest request = new AssumeRoleRequest();
		request.setDurationSeconds(1200L);
		request.setPolicy(policy);
		request.setRoleArn(roleArn);
		request.setRoleSessionName(roleSessionName);

		String reqString = JsonMapper.toJSONString(request);

		System.out.println(reqString);

		// request = JsonMapper.parseObject(reqString,AssumeRoleRequest.class);

		try {
			AssumeRoleResponse response = client.getAcsResponse(request);
			System.out.println(new Gson().toJson(response));
			test(response.getCredentials().getAccessKeyId(), response.getCredentials().getAccessKeySecret(),
					response.getCredentials().getSecurityToken(), "oss", "https://oss-cn-shenzhen.aliyuncs.com",
					"workoss");
			AssumeRoleResponse.Credentials credentials = response.getCredentials();
			STSToken stsToken = new STSToken();
			stsToken.setStsToken(credentials.getSecurityToken());
			stsToken.setAccessKey(credentials.getAccessKeyId());
			stsToken.setSecretKey(credentials.getAccessKeySecret());
			stsToken.setExpiration(
					DateUtils.parse(credentials.getExpiration(), "yyyy-MM-dd'T'HH:mm:ss'Z'").plusHours(8));
		}
		catch (ServerException e) {
			e.printStackTrace();
		}
		catch (ClientException e) {
			System.out.println("ErrCode:" + e.getErrCode());
			System.out.println("ErrMsg:" + e.getErrMsg());
			System.out.println("RequestId:" + e.getRequestId());
		}
	}

	void test(String ak, String sk, String stsToken, String storageType, String endpoint, String bucket) {
		// AwsCredentials awsCredentials = AwsSessionCredentials.create(ak, sk, stsToken);
		//
		// software.amazon.awssdk.regions.Region region =
		// software.amazon.awssdk.regions.Region.of(storageType);
		// S3Client s3 = S3Client.builder()
		// .region(region)
		// .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
		// .endpointOverride(URI.create(endpoint))
		// .build();
		// ListObjectsRequest listObjectsRequest =
		// ListObjectsRequest.builder().bucket(bucket).build();
		// s3.listObjects(listObjectsRequest).contents().stream().forEach(s3Object -> {
		// System.out.println(s3Object.toString());
		// });
		// File file = new File("/home/workoss/IDE/release.yaml");
		//
		// UploadPartRequest uploadPartRequest = UploadPartRequest.builder()
		// .bucket("img-test1")
		// .key("demo1.yaml")
		// .build();
		//
		//
		// UploadPartResponse uploadPartResponse = s3.uploadPart(uploadPartRequest,
		// Paths.get("/home/workoss/IDE/release.yaml"));
		// System.out.println(uploadPartResponse.toString());
	}

}
