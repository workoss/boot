/*
 * Copyright 2019-2024 workoss (https://www.workoss.com)
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
package com.workoss.boot.plugin.mybatis.example;

import com.workoss.boot.annotation.lang.Nullable;
import com.workoss.boot.util.Assert;
import com.workoss.boot.util.ObjectUtil;

import java.util.*;
import java.util.function.Function;

public interface ExampleMatcher {

	static ExampleMatcher matching() {
		return matchingAll();
	}

	static ExampleMatcher matchingAny() {
		return new TypedExampleMatcher().withMode(MatchMode.ANY);
	}

	static ExampleMatcher matchingAll() {
		return new TypedExampleMatcher().withMode(MatchMode.ALL);
	}

	ExampleMatcher withIgnorePaths(String... ignoredPaths);

	ExampleMatcher withStringMatcher(StringMatcher defaultStringMatcher);

	default ExampleMatcher withIgnoreCase() {
		return withIgnoreCase(true);
	}

	ExampleMatcher withIgnoreCase(boolean defaultIgnoreCase);

	default ExampleMatcher withMatcher(String propertyPath,
			MatcherConfigurer<GenericPropertyMatcher> matcherConfigurer) {

		Assert.hasText(propertyPath, "PropertyPath must not be empty!");
		Assert.notNull(matcherConfigurer, "MatcherConfigurer must not be empty!");

		GenericPropertyMatcher genericPropertyMatcher = new GenericPropertyMatcher();
		matcherConfigurer.configureMatcher(genericPropertyMatcher);

		return withMatcher(propertyPath, genericPropertyMatcher);
	}

	ExampleMatcher withMatcher(String propertyPath, GenericPropertyMatcher genericPropertyMatcher);

	ExampleMatcher withTransformer(String propertyPath, PropertyValueTransformer propertyValueTransformer);

	ExampleMatcher withIgnoreCase(String... propertyPaths);

	default ExampleMatcher withIncludeNullValues() {
		return withNullHandler(NullHandler.INCLUDE);
	}

	default ExampleMatcher withIgnoreNullValues() {
		return withNullHandler(NullHandler.IGNORE);
	}

	ExampleMatcher withNullHandler(NullHandler nullHandler);

	NullHandler getNullHandler();

	StringMatcher getDefaultStringMatcher();

	boolean isIgnoreCaseEnabled();

	default boolean isIgnoredPath(String path) {
		return getIgnoredPaths().contains(path);
	}

	Set<String> getIgnoredPaths();

	PropertySpecifiers getPropertySpecifiers();

	default boolean isAllMatching() {
		return getMatchMode().equals(MatchMode.ALL);
	}

	default boolean isAnyMatching() {
		return getMatchMode().equals(MatchMode.ANY);
	}

	MatchMode getMatchMode();

	enum NullHandler {

		INCLUDE, IGNORE

	}

	interface MatcherConfigurer<T> {

		void configureMatcher(T matcher);

	}

	class GenericPropertyMatcher {

		@Nullable
		StringMatcher stringMatcher = null;

		@Nullable
		Boolean ignoreCase = null;

		PropertyValueTransformer valueTransformer = NoOpPropertyValueTransformer.INSTANCE;

		public GenericPropertyMatcher() {
		}

		public static GenericPropertyMatcher of(StringMatcher stringMatcher, boolean ignoreCase) {
			return new GenericPropertyMatcher().stringMatcher(stringMatcher).ignoreCase(ignoreCase);
		}

		public static GenericPropertyMatcher of(StringMatcher stringMatcher) {
			return new GenericPropertyMatcher().stringMatcher(stringMatcher);
		}

		public GenericPropertyMatcher ignoreCase() {

			this.ignoreCase = true;
			return this;
		}

		public GenericPropertyMatcher ignoreCase(boolean ignoreCase) {

			this.ignoreCase = ignoreCase;
			return this;
		}

		public GenericPropertyMatcher caseSensitive() {

			this.ignoreCase = false;
			return this;
		}

		public GenericPropertyMatcher contains() {

			this.stringMatcher = StringMatcher.CONTAINING;
			return this;
		}

		public GenericPropertyMatcher endsWith() {

			this.stringMatcher = StringMatcher.ENDING;
			return this;
		}

		public GenericPropertyMatcher startsWith() {

			this.stringMatcher = StringMatcher.STARTING;
			return this;
		}

		public GenericPropertyMatcher exact() {

			this.stringMatcher = StringMatcher.EXACT;
			return this;
		}

		public GenericPropertyMatcher storeDefaultMatching() {

			this.stringMatcher = StringMatcher.DEFAULT;
			return this;
		}

		public GenericPropertyMatcher regex() {

			this.stringMatcher = StringMatcher.REGEX;
			return this;
		}

		public GenericPropertyMatcher stringMatcher(StringMatcher stringMatcher) {

			Assert.notNull(stringMatcher, "StringMatcher must not be null!");
			this.stringMatcher = stringMatcher;
			return this;
		}

		public GenericPropertyMatcher transform(PropertyValueTransformer propertyValueTransformer) {

			Assert.notNull(propertyValueTransformer, "PropertyValueTransformer must not be null!");
			this.valueTransformer = propertyValueTransformer;
			return this;
		}

		protected boolean canEqual(final Object other) {
			return other instanceof GenericPropertyMatcher;
		}

		@Override
		public boolean equals(Object o) {

			if (this == o) {
				return true;
			}

			if (!(o instanceof GenericPropertyMatcher)) {
				return false;
			}

			GenericPropertyMatcher that = (GenericPropertyMatcher) o;

			if (stringMatcher != that.stringMatcher)
				return false;

			if (!ObjectUtil.nullSafeEquals(ignoreCase, that.ignoreCase)) {
				return false;
			}

			return ObjectUtil.nullSafeEquals(valueTransformer, that.valueTransformer);
		}

		@Override
		public int hashCode() {
			int result = ObjectUtil.nullSafeHashCode(stringMatcher);
			result = 31 * result + ObjectUtil.nullSafeHashCode(ignoreCase);
			result = 31 * result + ObjectUtil.nullSafeHashCode(valueTransformer);
			return result;
		}

	}

	class GenericPropertyMatchers {

		public static GenericPropertyMatcher ignoreCase() {
			return new GenericPropertyMatcher().ignoreCase();
		}

		public static GenericPropertyMatcher caseSensitive() {
			return new GenericPropertyMatcher().caseSensitive();
		}

		public static GenericPropertyMatcher contains() {
			return new GenericPropertyMatcher().contains();
		}

		public static GenericPropertyMatcher endsWith() {
			return new GenericPropertyMatcher().endsWith();

		}

		public static GenericPropertyMatcher startsWith() {
			return new GenericPropertyMatcher().startsWith();
		}

		public static GenericPropertyMatcher exact() {
			return new GenericPropertyMatcher().exact();
		}

		public static GenericPropertyMatcher storeDefaultMatching() {
			return new GenericPropertyMatcher().storeDefaultMatching();
		}

		public static GenericPropertyMatcher regex() {
			return new GenericPropertyMatcher().regex();
		}

	}

	enum StringMatcher {

		DEFAULT,

		EXACT,

		STARTING,

		ENDING,

		CONTAINING,

		REGEX;

	}

	interface PropertyValueTransformer extends Function<Optional<Object>, Optional<Object>> {

	}

	enum NoOpPropertyValueTransformer implements ExampleMatcher.PropertyValueTransformer {

		INSTANCE;

		@Override
		@SuppressWarnings("null")
		public Optional<Object> apply(Optional<Object> source) {
			return source;
		}

	}

	class PropertySpecifier {

		private final String path;

		private final @Nullable StringMatcher stringMatcher;

		private final @Nullable Boolean ignoreCase;

		private final PropertyValueTransformer valueTransformer;

		PropertySpecifier(String path) {

			Assert.hasText(path, "Path must not be null/empty!");
			this.path = path;

			this.stringMatcher = null;
			this.ignoreCase = null;
			this.valueTransformer = NoOpPropertyValueTransformer.INSTANCE;
		}

		private PropertySpecifier(String path, @Nullable StringMatcher stringMatcher, @Nullable Boolean ignoreCase,
				PropertyValueTransformer valueTransformer) {
			this.path = path;
			this.stringMatcher = stringMatcher;
			this.ignoreCase = ignoreCase;
			this.valueTransformer = valueTransformer;
		}

		public PropertySpecifier withStringMatcher(StringMatcher stringMatcher) {

			Assert.notNull(stringMatcher, "StringMatcher must not be null!");
			return new PropertySpecifier(this.path, stringMatcher, this.ignoreCase, this.valueTransformer);
		}

		public PropertySpecifier withIgnoreCase(boolean ignoreCase) {
			return new PropertySpecifier(this.path, this.stringMatcher, ignoreCase, this.valueTransformer);
		}

		public PropertySpecifier withValueTransformer(PropertyValueTransformer valueTransformer) {

			Assert.notNull(valueTransformer, "PropertyValueTransformer must not be null!");
			return new PropertySpecifier(this.path, this.stringMatcher, this.ignoreCase, valueTransformer);
		}

		public String getPath() {
			return path;
		}

		@Nullable
		public StringMatcher getStringMatcher() {
			return stringMatcher;
		}

		@Nullable
		public Boolean getIgnoreCase() {
			return ignoreCase;
		}

		public PropertyValueTransformer getPropertyValueTransformer() {
			return valueTransformer == null ? NoOpPropertyValueTransformer.INSTANCE : valueTransformer;
		}

		public Optional<Object> transformValue(Optional<Object> source) {
			return getPropertyValueTransformer().apply(source);
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object o) {

			if (this == o) {
				return true;
			}

			if (!(o instanceof PropertySpecifier)) {
				return false;
			}

			PropertySpecifier that = (PropertySpecifier) o;

			if (!ObjectUtil.nullSafeEquals(path, that.path)) {
				return false;
			}

			if (stringMatcher != that.stringMatcher)
				return false;

			if (!ObjectUtil.nullSafeEquals(ignoreCase, that.ignoreCase)) {
				return false;
			}

			return ObjectUtil.nullSafeEquals(valueTransformer, that.valueTransformer);
		}

		@Override
		public int hashCode() {
			int result = ObjectUtil.nullSafeHashCode(path);
			result = 31 * result + ObjectUtil.nullSafeHashCode(stringMatcher);
			result = 31 * result + ObjectUtil.nullSafeHashCode(ignoreCase);
			result = 31 * result + ObjectUtil.nullSafeHashCode(valueTransformer);
			return result;
		}

		protected boolean canEqual(final Object other) {
			return other instanceof PropertySpecifier;
		}

	}

	class PropertySpecifiers {

		private final Map<String, PropertySpecifier> propertySpecifiers = new LinkedHashMap<>();

		PropertySpecifiers() {
		}

		PropertySpecifiers(PropertySpecifiers propertySpecifiers) {
			this.propertySpecifiers.putAll(propertySpecifiers.propertySpecifiers);
		}

		public void add(PropertySpecifier specifier) {

			Assert.notNull(specifier, "PropertySpecifier must not be null!");
			propertySpecifiers.put(specifier.getPath(), specifier);
		}

		public boolean hasSpecifierForPath(String path) {
			return propertySpecifiers.containsKey(path);
		}

		public PropertySpecifier getForPath(String path) {
			return propertySpecifiers.get(path);
		}

		public boolean hasValues() {
			return !propertySpecifiers.isEmpty();
		}

		public Collection<PropertySpecifier> getSpecifiers() {
			return propertySpecifiers.values();
		}

		@Override
		public boolean equals(Object o) {

			if (this == o) {
				return true;
			}

			if (!(o instanceof PropertySpecifiers)) {
				return false;
			}

			PropertySpecifiers that = (PropertySpecifiers) o;
			return ObjectUtil.nullSafeEquals(propertySpecifiers, that.propertySpecifiers);
		}

		@Override
		public int hashCode() {
			return ObjectUtil.nullSafeHashCode(propertySpecifiers);
		}

	}

	enum MatchMode {

		ALL, ANY;

	}

}
