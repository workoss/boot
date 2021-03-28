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
