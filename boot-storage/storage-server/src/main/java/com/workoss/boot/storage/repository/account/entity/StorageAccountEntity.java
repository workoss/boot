package com.workoss.boot.storage.repository.account.entity;

import com.workoss.boot.storage.model.AccountState;
import com.workoss.boot.storage.model.ThirdPlatformType;
import com.workoss.boot.storage.repository.TenantEntity;
import lombok.Data;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

/**
 * 存储账号管理
 *
 * @author workoss
 */
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
