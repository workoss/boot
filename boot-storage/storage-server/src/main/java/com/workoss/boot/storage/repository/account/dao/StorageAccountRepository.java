package com.workoss.boot.storage.repository.account.dao;

import com.workoss.boot.storage.repository.account.entity.StorageAccountEntity;
import com.workoss.boot.storage.model.AccountState;
import com.workoss.boot.storage.model.ThirdPlatformType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

/**
 * 账号查询
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

	Flux<StorageAccountEntity> findByAccountTypeAndStateAndTenantId(ThirdPlatformType accountType, AccountState state,
			String tenentId, Pageable pageable);

}
