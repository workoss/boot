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
package com.workoss.boot.util.xml;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * jaxbContextContainer
 *
 * @author workoss
 */
public final class JaxbContextContainer {

	private final ConcurrentMap<Class<?>, JAXBContext> jaxbContexts = new ConcurrentHashMap<>(64);

	public Marshaller createMarshaller(Class<?> clazz) throws JAXBException {
		JAXBContext jaxbContext = getJaxbContext(clazz);
		return jaxbContext.createMarshaller();
	}

	public Unmarshaller createUnmarshaller(Class<?> clazz) throws JAXBException {
		JAXBContext jaxbContext = getJaxbContext(clazz);
		return jaxbContext.createUnmarshaller();
	}

	private JAXBContext getJaxbContext(Class<?> clazz) {
		return this.jaxbContexts.computeIfAbsent(clazz, key -> {
			try {
				return JAXBContext.newInstance(clazz);
			}
			catch (JAXBException ex) {
				throw new RuntimeException("Could not create JAXBContext for class [" + clazz + "]: " + ex.getMessage(),
						ex);
			}
		});
	}

}
