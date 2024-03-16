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
package com.workoss.boot.storage.repository.account.dao;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.samskivert.mustache.Mustache;
import com.workoss.boot.storage.BaseSpringTest;
import com.workoss.boot.storage.model.AccountState;
import com.workoss.boot.storage.model.BaseStorageModel;
import com.workoss.boot.storage.model.ThirdPlatformType;
import com.workoss.boot.storage.repository.account.entity.StorageAccountEntity;
import com.workoss.boot.storage.util.MustacheTemplateUtil;
import com.workoss.boot.storage.util.ReactorUtil;
import com.workoss.boot.util.DateUtil;
import com.workoss.boot.util.json.JsonMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.CriteriaDefinition;
import org.springframework.data.relational.core.query.Query;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Signal;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
class StorageAccountRepositoryTest extends BaseSpringTest {

	@Autowired
	private StorageAccountRepository storageAccountRepository;

	@Autowired
	private DatabaseClient databaseClient;

	@Autowired
	private R2dbcEntityTemplate r2dbcEntityTemplate;

	@Autowired
	private Mustache.Compiler compiler;

	private static final Cache<String, Signal<? extends StorageAccountEntity>> ACCOUNT_CACHE = Caffeine.newBuilder()
		.initialCapacity(4)
		.maximumSize(50)
		.expireAfterWrite(Duration.ofSeconds(6))
		.removalListener((key, value, cause) -> {
			log.debug("【popeye】ACCOUNT_CACHE KEY：{} cause:{}", key, cause);
		})
		.build();

	protected Mono<StorageAccountEntity> getConfig(BaseStorageModel storage) {
		return ReactorUtil.createCacheMono(ACCOUNT_CACHE, storage.getStorageType().name(), (k) -> {
			System.out.println("====" + k);
			PageRequest pageRequest = PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "createTime"));
			return storageAccountRepository
				.findByAccountTypeAndState(storage.getStorageType(), AccountState.OFF, pageRequest)
				.defaultIfEmpty(new StorageAccountEntity())
				.last()
				.doOnSuccess(storageAccountEntity -> {
					log.info("【popeye】ACCOUNT_CACHE KEY：{} LOAD FROM DB", k);
				});
		});
	}

	@Test
	void testCache() throws InterruptedException {
		String contextString = "{\"access_key\":\"\",\"secret_key\":\"A0QuGrDAmJvghrDFvbFK9l8GaGbWFZHkMCQGRege\",\"region\":\"cn-south-1\",\"agency_name\":\"obs_token\",\"domain_name\":\"workoss\"}";
		Map<String, String> map = JsonMapper.parseObject(contextString, Map.class);
		map.put("session_name", "popeye");
		// {"auth":{"identity":{"methods":["assume_role"],"assume_role":{"agency_name":"obs_token","domain_name":"workoss","duration_seconds":3600,"session_user":{"name":"alias"}},"policy":{"Version":"1.1","Statement":[{"Action":["obs:*"],"Effect":"Allow","Resource":["obs:*:*:*:*"]}]}}}}
		String policy = "{\"Version\":\"1.1\",\"Statement\":[{\"Action\":[\"obs:{{#action}}{{action}}{{/action}}{{^action}}*{{/action}}\"],\"Effect\":\"Allow\",\"Resource\":[\"obs:*:*:*:*\"]}]}}}";
		map.put("policy", MustacheTemplateUtil.render(policy, map));
		String reqBody = "{\"auth\":{\"identity\":{\"methods\":[\"assume_role\"],\"assume_role\":{\"agency_name\":\"{{agency_name}}\",\"domain_name\":\"{{domain_name}}\",\"duration_seconds\":900,\"session_user\":{\"name\":\"{{session_name}}\"}},\"policy\":{{policy}}}";
		String string = MustacheTemplateUtil.render(reqBody, map);
		System.out.println(string);
		// BaseStorageModel storageModel = new BaseStorageModel();
		// storageModel.setStorageType(ThirdPlatformType.OBS);
		// for (int i = 0; i < 10; i++) {
		// getConfig(storageModel).log().flatMap(storageAccountEntity -> {
		// System.err.println(storageAccountEntity.toString());
		// return Mono.just(storageAccountEntity);
		// }).subscribe();
		// Thread.sleep(3000);
		// }

	}

	@Test
	void testQuery() {

		PageRequest pageRequest = PageRequest.of(0, 1);

		// Criteria criteria = Criteria.empty();
		// criteria = criteria.where("account_type").is(ThirdPlatformType.OBS);
		// criteria = criteria.and("state").is(AccountState.OFF);

		StorageAccountEntity defaultEntity = new StorageAccountEntity();
		defaultEntity.setModifyTime(DateUtil.getCurrentDateTime());

		storageAccountRepository.findByAccountTypeAndState(ThirdPlatformType.OBS, AccountState.OFF, pageRequest)
			// r2dbcEntityTemplate.select(StorageAccountEntity.class)
			// .matching(Query.query(criteria).sort(Sort.by(Sort.Direction.DESC,"createTime")).limit(1).offset(0)).all()
			.log()
			.defaultIfEmpty(defaultEntity)
			.last()
			.flatMap(storageAccountEntity -> {
				System.err.println("--" + storageAccountEntity.toString());
				return Mono.just(storageAccountEntity);
			})
			.subscribe();
	}

	@Test
	void testDataClient() throws InterruptedException {

		// databaseClient.sql().bind()
		r2dbcEntityTemplate.count(Query.query(CriteriaDefinition.empty()), StorageAccountEntity.class).doOnNext(num -> {
			System.out.println(Thread.currentThread().getName() + ":" + num);
		}).flatMapMany(num -> {
			return r2dbcEntityTemplate.select(StorageAccountEntity.class)
				.matching(Query.query(CriteriaDefinition.empty()).limit(10).offset(0))
				.all();
		}).doOnNext(storageAccountEntity -> {
			System.out.println(Thread.currentThread().getName() + ":" + storageAccountEntity.toString());
		}).subscribe();

		Thread.sleep(2000);
	}

	// @Transactional(rollbackFor = {Exception.class})
	@Test
	void save() {
		List<StorageAccountEntity> list = new ArrayList<>();
		for (int i = 0; i < 100; i++) {
			StorageAccountEntity accountEntity = new StorageAccountEntity();
			accountEntity.setAccessKey("8ACZFOMKJUAAY1XQWKXR");
			accountEntity.setAccountType(ThirdPlatformType.OBS);
			accountEntity.setState(AccountState.ON);
			accountEntity.setCreateTime(DateUtil.getCurrentDateTime());
			accountEntity.setCreateTime(DateUtil.getCurrentDateTime());
			list.add(accountEntity);
		}
		storageAccountRepository.saveAll(list).as(StepVerifier::create).expectNextCount(100).verifyComplete();
		// Pageable pageable = PageRequest.of(0,10,
		// Sort.by(Sort.Order.asc("createTime")));
		// Flux<StorageAccountEntity> flux =
		// storageAccountRepository.findByAccountType(ThirdPlatformType.OBS,pageable);
		// System.out.println(flux.count().block());
		// flux
		// .log()
		// .take(10)
		// .doOnNext(storageAccountEntity -> {
		// System.out.println(storageAccountEntity.getId());
		// })
		// .subscribe();

		// storageAccountRepository.findById(0L)
		// .as(StepVerifier::create)
		// .expectNextCount(1)
		// .verifyComplete();
		// Pageable pageable = PageRequest.of(0,10);
		// storageAccountRepository.findByAccountType(ThirdPlatformType.OBS, pageable)
		// .log()
		// .take(10)
		// .doOnNext(storageAccountEntity -> {
		// System.out.println(storageAccountEntity.getId());
		// })
		// .subscribe();
	}

}
