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

import com.workoss.boot.storage.model.STSToken;
import com.workoss.boot.storage.model.ThirdPlatformType;
import com.workoss.boot.storage.model.UploadSign;
import com.workoss.boot.storage.service.token.AbstractTokenHandler;
import com.workoss.boot.util.context.Context;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class COSTokenHandler extends AbstractTokenHandler {

	@Override
	public ThirdPlatformType getName() {
		return ThirdPlatformType.COS;
	}

	@Override
	protected String getAction(String bucketName, String action) {
		return null;
	}

	@Override
	protected String getResource(String bucketName, String action, String key) {
		return null;
	}

	@Override
	protected String getDomain(Context<String, String> context, String bucketName) {
		return null;
	}

	@Override
	public Mono<UploadSign> generateUploadSign(Context<String, String> context, String bucketName, String key,
			String mimeType, String successActionStatus) {
		return null;
	}

	@Override
	public Mono<UploadSign> generateUploadStsSign(Context<String, String> context, String bucketName, String key,
			String mimeType, String successActionStatus) {
		return null;
	}

	@Override
	public Mono<STSToken> generateStsToken(Context<String, String> context, String bucketName, String key,
			String action) {
		return null;
	}

}
