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
package com.workoss.boot.storage.repository.account.dao;

import com.workoss.boot.storage.repository.account.entity.StorageAccountEntity;
import com.workoss.boot.storage.model.AccountState;
import com.workoss.boot.storage.model.ThirdPlatformType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

/**
 * 账号查询 repository
 *
 * @author workoss
 */
@Repository
public interface StorageAccountRepository extends R2dbcRepository<StorageAccountEntity, Long> {

	/**
	 * 通过类型+状态查询分页查询
	 * @param accountType 账号类型
	 * @param state 状态
	 * @param pageable 分页
	 * @return 1页
	 */
	Flux<StorageAccountEntity> findByAccountTypeAndState(ThirdPlatformType accountType, AccountState state,
			Pageable pageable);

	/**
	 * 通过账号类型，状态，租户id查询
	 * @param accountType 账号类别
	 * @param state 状态
	 * @param tenentId 租户ID
	 * @param pageable 分页参数
	 * @return 账号配置信息
	 */
	Flux<StorageAccountEntity> findByAccountTypeAndStateAndTenantId(ThirdPlatformType accountType, AccountState state,
			String tenentId, Pageable pageable);

}
