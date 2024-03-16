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
package com.workoss.boot.extension;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author: workoss
 * @date: 2018-12-13 18:56
 * @version:
 */
public class ExtensionLoadTest {

	@Test
	public void test01() {
		Person person = ExtensionLoaderFactory.getMixExtension(Person.class, "A", null);
		Assertions.assertNotNull(person);

		Person person1 = ExtensionLoaderFactory.getFirstExtension(Person.class, "A", null);
		Assertions.assertNull(person1);

		Person person2 = ExtensionLoaderFactory.getSpiExtension(Person.class, "A", null);
		Assertions.assertNotNull(person2);

		Person person3 = ExtensionLoaderFactory.getExtension("spi", Person.class, "A", null);
		Assertions.assertNotNull(person3);

		// ExtensionLoader<Person> personExtensionLoader =
		// ExtensionLoaderUtil.getExtensionLoader(Person.class, null);
		// for (Map.Entry<String, ExtensionClass<Person>> stringExtensionClassEntry :
		// personExtensionLoader
		// .getAllExtensions().entrySet()) {
		// System.out.println(stringExtensionClassEntry.getKey() + ":" +
		// stringExtensionClassEntry.getValue());
		// }
		// ExtensionLoader<ExtensionFactory> factoryExtensionLoader = ExtensionLoaderUtil
		// .getExtensionLoader(ExtensionFactory.class, null);
		// System.out.println(factoryExtensionLoader.getAllExtensions());
	}

}
