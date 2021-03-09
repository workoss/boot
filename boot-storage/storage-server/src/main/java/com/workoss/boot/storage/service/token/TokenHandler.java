package com.workoss.boot.storage.service.token;

import com.workoss.boot.storage.context.Context;
import com.workoss.boot.storage.model.STSToken;
import com.workoss.boot.storage.model.ThirdPlatformType;
import com.workoss.boot.storage.model.UploadSign;
import com.yifengx.popeye.storage.model.*;
import reactor.core.publisher.Mono;

public interface TokenHandler {

	ThirdPlatformType getName();

	Mono<UploadSign> generateUploadSign(Context<String, String> context, String bucketName, String key, String mimeType,
                                        String successActionStatus);

	Mono<UploadSign> generateUploadStsSign(Context<String, String> context, String bucketName, String key,
			String mimeType, String successActionStatus);

	Mono<STSToken> generateStsToken(final Context<String, String> context, String bucketName, final String key,
                                    final String action);

}
