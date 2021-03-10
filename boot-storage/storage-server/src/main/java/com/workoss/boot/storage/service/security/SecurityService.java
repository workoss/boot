/*
 * Copyright © 2020-2021 workoss (WORKOSS)
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
