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
package com.workoss.boot.storage.service.security.impl;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.workoss.boot.storage.context.MapContext;
import com.workoss.boot.storage.exception.StorageException;
import com.workoss.boot.storage.model.AccountState;
import com.workoss.boot.storage.model.BaseStorageModel;
import com.workoss.boot.storage.model.STSToken;
import com.workoss.boot.storage.model.UploadSign;
import com.workoss.boot.storage.repository.account.dao.StorageAccountRepository;
import com.workoss.boot.storage.repository.account.entity.StorageAccountEntity;
import com.workoss.boot.storage.service.security.SecurityService;
import com.workoss.boot.storage.service.token.TokenHandlerFactory;
import com.workoss.boot.storage.util.MimeTypeUtil;
import com.workoss.boot.storage.util.ReactorUtil;
import com.workoss.boot.util.StringUtils;
import com.workoss.boot.util.json.JsonMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Signal;

import java.time.Duration;

/**
 * 对象存储签名授权
 *
 * @author workoss
 */
@Slf4j
@Service
public class SecurityServiceImpl implements SecurityService {

	private final StorageAccountRepository storageAccountRepository;

	private final TokenHandlerFactory tokenHandlerFactory;

	private static final Cache<String, Signal<? extends MapContext<String, String>>> ACCOUNT_CACHE = Caffeine
			.newBuilder().initialCapacity(4).maximumSize(50).expireAfterWrite(Duration.ofHours(1))
			.removalListener((key, value, cause) -> {
				log.debug("【popeye】ACCOUNT_CACHE KEY：{} cause:{}", key, cause);
			}).build();

	public SecurityServiceImpl(StorageAccountRepository storageAccountRepository,
			TokenHandlerFactory tokenHandlerFactory) {
		this.storageAccountRepository = storageAccountRepository;
		this.tokenHandlerFactory = tokenHandlerFactory;
	}

	protected Mono<MapContext<String, String>> getConfig(BaseStorageModel storage) {
		return ReactorUtil.createCacheMono(ACCOUNT_CACHE, storage.getStorageType().name(), (k) -> {
			PageRequest pageRequest = PageRequest.of(0, 1,
					Sort.by(Sort.Order.desc("modifyTime"), Sort.Order.desc("id")));
			return storageAccountRepository
					.findByAccountTypeAndStateAndTenantId(storage.getStorageType(), AccountState.ON,
							storage.getTenentId(), pageRequest)
					.defaultIfEmpty(new StorageAccountEntity()).last().flatMap(accountEntity -> {
						if (StringUtils.isBlank(accountEntity.getAccessKey())) {
							return Mono.just(MapContext.EMPTY);
						}
						MapContext<String, String> context = JsonMapper.parseObject(accountEntity.getConfig(),
								MapContext.class);
						context.put("policy", accountEntity.getPolicyTemplate());
						return Mono.just(context);
					}).doOnSuccess(context -> {
						log.debug("【popeye】ACCOUNT_CACHE KEY：{} LOAD FROM DB", storage.getStorageType());
					});
		});
	}

	@Override
	public Mono<UploadSign> generateUploadSign(BaseStorageModel storage, String key, String mimeType,
			String successActionStatus) {
		return getConfig(storage).flatMap(context -> {
			if (context.isEmpty()) {
				return Mono.error(new StorageException("00001"));
			}
			String finalMimeType = StringUtils.isBlank(mimeType) ? MimeTypeUtil.getMediaType(key) : mimeType;
			// 判断host是否为空
			return tokenHandlerFactory.getHandler(storage.getStorageType())
					.generateUploadSign(context, storage.getBucketName(), key, finalMimeType, successActionStatus)
					.flatMap(uploadSign -> {
						uploadSign.setStorageType(storage.getStorageType());
						uploadSign.setTenentId(storage.getTenentId());
						uploadSign.setBucketName(storage.getBucketName());
						uploadSign.setMimeType(finalMimeType);
						uploadSign.setSuccessActionStatus(successActionStatus);
						return Mono.just(uploadSign);
					});
		});
	}

	@Override
	public Mono<UploadSign> generateUploadStsSign(BaseStorageModel storage, String key, String mimeType,
			String successActionStatus) {
		return getConfig(storage).flatMap(context -> {
			if (context.isEmpty()) {
				return Mono.error(new StorageException("00001"));
			}
			String finalMimeType = StringUtils.isBlank(mimeType) ? MimeTypeUtil.getMediaType(key) : mimeType;
			// 判断host是否为空
			return tokenHandlerFactory.getHandler(storage.getStorageType())
					.generateUploadStsSign(context, storage.getBucketName(), key, finalMimeType, successActionStatus)
					.flatMap(uploadSign -> {
						uploadSign.setStorageType(storage.getStorageType());
						uploadSign.setTenentId(storage.getTenentId());
						uploadSign.setBucketName(storage.getBucketName());
						uploadSign.setMimeType(finalMimeType);
						uploadSign.setSuccessActionStatus(
								StringUtils.isBlank(successActionStatus) ? null : successActionStatus);
						return Mono.just(uploadSign);
					});
		});
	}

	@Override
	public Mono<STSToken> generateStsToken(BaseStorageModel storage, String key, String action) {
		// 先查询到账号信息 然后调用下层
		return getConfig(storage).flatMap(context -> {
			if (context.isEmpty()) {
				return Mono.error(new StorageException("00001"));
			}
			return tokenHandlerFactory.getHandler(storage.getStorageType())
					.generateStsToken(context, storage.getBucketName(), key, action).flatMap(stsToken -> {
						stsToken.setStorageType(storage.getStorageType());
						stsToken.setTenentId(storage.getTenentId());
						stsToken.setBucketName(storage.getBucketName());
						return Mono.just(stsToken);
					});
		});
	}

}
