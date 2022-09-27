/*
 * Copyright 2019-2022 workoss (https://www.workoss.com)
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

import com.workoss.boot.plugin.mybatis.util.Lazy;
import org.apache.ibatis.builder.annotation.ProviderContext;

import java.util.*;
import java.util.stream.Collectors;

public class EntityClassFinderFactory {

	private static final Lazy<List<EntityClassFinder>> CLASS_FINDER_LAZY = Lazy.of(() -> getServiceLoader());

	static List<EntityClassFinder> getServiceLoader() {
		Iterator<ClassFinderMatcher> iterator = ServiceLoader.load(ClassFinderMatcher.class).iterator();

		List<ClassFinderMatcher> classFinderMatchers = new ArrayList<>();
		if (iterator.hasNext()) {
			ClassFinderMatcher next = iterator.next();
			if (next.match()) {
				classFinderMatchers.add(next);
			}
		}
		return classFinderMatchers.stream().sorted(Comparator.comparingInt(ClassFinderMatcher::order))
				.map(ClassFinderMatcher::instance).collect(Collectors.toList());
	}

	public static Optional<EntityClassFinder> getClassFinder(ProviderContext context) {
		return CLASS_FINDER_LAZY.get().stream().filter(entityClassFinder -> entityClassFinder.match(context))
				.findFirst();
	}

}
