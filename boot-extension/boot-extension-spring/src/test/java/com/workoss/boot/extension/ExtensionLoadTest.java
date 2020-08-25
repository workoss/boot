/*
 * The MIT License
 * Copyright © 2020-2021 workoss
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.workoss.boot.extension;

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
		System.out.println(person);

		Person person1 = ExtensionLoaderFactory.getFirstExtension(Person.class, "A", null);
		System.out.println(person1);

		Person person2 = ExtensionLoaderFactory.getSpiExtension(Person.class, "A", null);
		System.out.println(person2);

		Person person3 = ExtensionLoaderFactory.getExtension("spi", Person.class, "A", null);
		System.out.println(person3);

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
