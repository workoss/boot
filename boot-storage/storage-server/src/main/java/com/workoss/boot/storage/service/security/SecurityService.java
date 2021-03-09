package com.workoss.boot.storage.service.security;

import com.workoss.boot.storage.model.BaseStorageModel;
import com.workoss.boot.storage.model.STSToken;
import com.workoss.boot.storage.model.UploadSign;
import reactor.core.publisher.Mono;

/**
 * 对象存储 tokenservice
 *
 * @author workoss
 */
public interface SecurityService {

	Mono<UploadSign> generateUploadSign(BaseStorageModel storage, String key, String mimeType,
			String successActionStatus);

	Mono<UploadSign> generateUploadStsSign(BaseStorageModel storage, String key, String mimeType,
			String successActionStatus);

	Mono<STSToken> generateStsToken(BaseStorageModel storage, String key, String action);

}
