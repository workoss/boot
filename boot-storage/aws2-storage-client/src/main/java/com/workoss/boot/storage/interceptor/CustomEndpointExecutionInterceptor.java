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
package com.workoss.boot.storage.interceptor;

import software.amazon.awssdk.auth.signer.AwsSignerExecutionAttribute;
import software.amazon.awssdk.core.interceptor.Context;
import software.amazon.awssdk.core.interceptor.ExecutionAttributes;
import software.amazon.awssdk.core.interceptor.ExecutionInterceptor;
import software.amazon.awssdk.core.interceptor.SdkExecutionAttribute;
import software.amazon.awssdk.http.SdkHttpRequest;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.internal.BucketUtils;
import software.amazon.awssdk.services.s3.internal.endpoints.S3EndpointUtils;

/**
 * 自定义endpoint 拦截器，支持三方hostStyle
 *
 * @author workoss
 */
public final class CustomEndpointExecutionInterceptor implements ExecutionInterceptor {

	@Override
	public SdkHttpRequest modifyHttpRequest(Context.ModifyHttpRequest context,
			ExecutionAttributes executionAttributes) {
		boolean endpointOverridden = Boolean.TRUE
			.equals(executionAttributes.getAttribute(SdkExecutionAttribute.ENDPOINT_OVERRIDDEN));
		String bucketName = context.request().getValueForField("Bucket", String.class).orElse(null);
		S3Configuration serviceConfiguration = (S3Configuration) executionAttributes
			.getAttribute(AwsSignerExecutionAttribute.SERVICE_CONFIG);

		Region region = executionAttributes.getAttribute(AwsSignerExecutionAttribute.SIGNING_REGION);

		SdkHttpRequest.Builder builder = context.httpRequest().toBuilder();
		if (endpointOverridden && canUseVirtualAddressing(serviceConfiguration, bucketName)) {
			changeToDnsEndpoint(region, builder, bucketName);
		}
		return builder.build();
	}

	private static boolean canUseVirtualAddressing(S3Configuration serviceConfiguration, String bucketName) {
		return !S3EndpointUtils.isPathStyleAccessEnabled(serviceConfiguration) && bucketName != null
				&& BucketUtils.isVirtualAddressingCompatibleBucketName(bucketName, false);
	}

	private static void changeToDnsEndpoint(Region region, SdkHttpRequest.Builder mutableRequest, String bucketName) {
		if (region == null || region.id() == null) {
			return;
		}
		if (mutableRequest.host().startsWith(bucketName + ".")) {
			return;
		}
		String newHost = bucketName + "." + mutableRequest.host();
		String newPath = mutableRequest.encodedPath().replaceFirst("/" + bucketName, "");
		mutableRequest.host(newHost).encodedPath(newPath);
	}

}
