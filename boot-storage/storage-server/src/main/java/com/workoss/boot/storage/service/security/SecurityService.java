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

	/**
	 * 获取html5上传签名（非临时）
	 * @param storage 基础参数
	 * @param key 文件key
	 * @param mimeType 文件类型
	 * @param successActionStatus 上传返回状态 200
	 * @return 签名参数
	 */
	Mono<UploadSign> generateUploadSign(BaseStorageModel storage, String key, String mimeType,
			String successActionStatus);

	/**
	 * 获取html5上传签名(临时stsToken方式)
	 * @param storage 基本参数
	 * @param key 文件key
	 * @param mimeType 文件类型
	 * @param successActionStatus 上传返回状态 200
	 * @return 签名参数
	 */
	Mono<UploadSign> generateUploadStsSign(BaseStorageModel storage, String key, String mimeType,
			String successActionStatus);

	/**
	 * 生成临时凭证 客户端使用
	 * @param storage 基本配置
	 * @param key 文件key
	 * @param action 操作
	 * @return stsToken签名
	 */
	Mono<STSToken> generateStsToken(BaseStorageModel storage, String key, String action);

}
