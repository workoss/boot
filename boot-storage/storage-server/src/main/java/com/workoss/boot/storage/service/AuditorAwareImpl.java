package com.workoss.boot.storage.service;

import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * createBy lastModifyBy  获取当前登录人
 *
 * @author workoss
 */
@Component
public class AuditorAwareImpl implements AuditorAware<String> {
	@Override
	public Optional<String> getCurrentAuditor() {
		return Optional.empty();
	}
}
