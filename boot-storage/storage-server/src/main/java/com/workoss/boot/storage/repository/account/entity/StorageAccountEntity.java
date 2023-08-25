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
package com.workoss.boot.storage.repository.account.entity;

import com.workoss.boot.storage.model.AccountState;
import com.workoss.boot.storage.model.ThirdPlatformType;
import com.workoss.boot.storage.repository.TenantEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

/**
 * 存储账号管理
 *
 * @author workoss
 */
@EqualsAndHashCode(callSuper = true)
@ToString
@Data
@Table("storage_account")
public class StorageAccountEntity extends TenantEntity {

	/**
	 * 账号类别
	 */
	@Column("account_type")
	private ThirdPlatformType accountType;

	@Column("access_key")
	private String accessKey;

	/**
	 * 账号配置
	 */
	@Column("config")
	private String config;

	/**
	 * 授权策略模板
	 */
	@Column("policy_template")
	private String policyTemplate;

	/**
	 * 状态 ON,OFF
	 */
	@Column("state")
	private AccountState state;

	/**
	 * 创建时间
	 */
	@CreatedDate
	@Column("create_time")
	private LocalDateTime createTime;

	/**
	 * 修改时间
	 */
	@LastModifiedDate
	@Column("modify_time")
	private LocalDateTime modifyTime;

}
