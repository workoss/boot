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
package com.workoss.boot.storage.service.token;

import com.workoss.boot.storage.model.STSToken;
import com.workoss.boot.storage.model.ThirdPlatformType;
import com.workoss.boot.storage.model.UploadSign;
import com.workoss.boot.util.context.Context;
import reactor.core.publisher.Mono;

/**
 * token处理
 *
 * @author workoss
 */
public interface TokenHandler {

	/**
	 * 平台名称
	 * @return 三方平台类型
	 */
	ThirdPlatformType getName();

	/**
	 * 生产html5 签名
	 * @param context 上下文
	 * @param bucketName 存储桶
	 * @param key 文件key
	 * @param mimeType 文件类型
	 * @param successActionStatus 上传成功后返回状态
	 * @return 签名
	 */
	Mono<UploadSign> generateUploadSign(Context<String, String> context, String bucketName, String key, String mimeType,
			String successActionStatus);

	/**
	 * 生产html5 签名(临时stsToken方式)
	 * @param context 上下文
	 * @param bucketName 存储桶
	 * @param key 文件key
	 * @param mimeType 文件类型
	 * @param successActionStatus 上传成功后返回状态
	 * @return 签名
	 */
	Mono<UploadSign> generateUploadStsSign(Context<String, String> context, String bucketName, String key,
			String mimeType, String successActionStatus);

	/**
	 * 生成stsToken
	 * @param context 上下文
	 * @param bucketName 存储桶
	 * @param key 文件key
	 * @param action 操作
	 * @return stsToken
	 */
	Mono<STSToken> generateStsToken(final Context<String, String> context, String bucketName, final String key,
			final String action);

}
