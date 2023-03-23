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
package com.workoss.boot.plugin.mybatis.finder;

import com.workoss.boot.plugin.mybatis.util.ObjectUtil;

/**
 * 默认finder 匹配
 *
 * @author workoss
 */
public class JavaxClassFinderMatcher implements ClassFinderMatcher {

	@Override
	public int order() {
		return 999;
	}

	@Override
	public boolean match() {
		return ObjectUtil.isPresent("javax.persistence.Table", null);
	}

	@Override
	public EntityClassFinder instance() {
		return new JavaxEntityClassFinder();
	}

}
