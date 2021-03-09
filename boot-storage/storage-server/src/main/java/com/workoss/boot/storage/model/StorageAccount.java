package com.workoss.boot.storage.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class StorageAccount {

	/**
	 * 账号类别
	 */
	private ThirdPlatformType accountType;

	private String accessKey;

	/**
	 * 账号配置
	 */
	private String config;

	/**
	 * 授权策略模板
	 */
	private String policyTemplate;

	/**
	 * 状态 ON,OFF
	 */
	private AccountState state;

	/**
	 * 创建时间
	 */
	private LocalDateTime createTime;

	/**
	 * 修改时间
	 */
	private LocalDateTime modifyTime;

}
