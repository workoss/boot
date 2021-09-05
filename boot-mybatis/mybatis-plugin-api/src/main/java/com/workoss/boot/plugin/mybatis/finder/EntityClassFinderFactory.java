package com.workoss.boot.plugin.mybatis.finder;

import com.workoss.boot.plugin.mybatis.util.Lazy;
import org.apache.ibatis.builder.annotation.ProviderContext;

import java.util.*;
import java.util.stream.Collectors;

public  class EntityClassFinderFactory {

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
		return CLASS_FINDER_LAZY.get().stream()
				.filter(entityClassFinder -> entityClassFinder.match(context))
				.findFirst();
	}
}
