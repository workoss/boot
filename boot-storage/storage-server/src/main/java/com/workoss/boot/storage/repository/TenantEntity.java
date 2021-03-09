package com.workoss.boot.storage.repository;

import lombok.Data;
import org.springframework.data.relational.core.mapping.Column;

@Data
public class TenantEntity extends IdEntity {

	@Column("tenant_id")
	private String tenantId;

}
