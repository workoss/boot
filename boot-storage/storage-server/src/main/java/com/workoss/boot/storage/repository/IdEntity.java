package com.workoss.boot.storage.repository;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;

@Data
public class IdEntity {

	/**
	 * 数据id
	 */
	@Id
	@Column("id")
	private Long id;

}
