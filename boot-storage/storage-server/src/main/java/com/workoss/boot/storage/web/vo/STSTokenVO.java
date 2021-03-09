package com.workoss.boot.storage.web.vo;

import com.workoss.boot.storage.model.ThirdPlatformType;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * 返回STS token 临时
 *
 * @author workoss
 */
@Data
public class STSTokenVO {

	private ThirdPlatformType storageType;

	/**
	 * accessKey accessId
	 */
	private String accessKey;

	/**
	 * secretKey
	 */
	private String secretKey;

	/**
	 * sts token
	 */
	private String stsToken;

	/**
	 * 过期时间
	 */
	@DateTimeFormat(pattern = "")
	private LocalDateTime expiration;

	/**
	 * endpoint
	 */
	private String endpoint;

}
