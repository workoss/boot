package com.workoss.boot.plugin.mybatis.finder;

import com.workoss.boot.plugin.mybatis.util.ObjectUtil;

/**
 * 默认finder 匹配
 *
 * @author workoss
 */
class JakartaClassFinderMatcher implements ClassFinderMatcher {

	@Override
	public int order() {
		return 999;
	}

	@Override
	public boolean match() {
		return ObjectUtil.isPresent("jakarta.persistence.Table", null);
	}

	@Override
	public EntityClassFinder instance() {
		return new JakartaEntityClassFinder();
	}


}
