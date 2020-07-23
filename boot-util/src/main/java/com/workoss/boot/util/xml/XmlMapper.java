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
package com.workoss.boot.util.xml;

import com.workoss.boot.util.StringUtils;
import com.workoss.boot.util.ApplyClassFunc;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.namespace.QName;
import javax.xml.stream.*;
import javax.xml.stream.events.XMLEvent;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;

/**
 * @author workoss
 */
public class XmlMapper {

	private final JaxbContextContainer jaxbContexts = new JaxbContextContainer();

	private static final XMLInputFactory INPUT_FACTORY = StaxUtils.createDefensiveInputFactory();

	private final Function<Unmarshaller, Unmarshaller> unmarshallerProcessor = Function.identity();

	private final Function<Marshaller, Marshaller> marshallerProcessor = Function.identity();

	private static final XmlMapper XML_MAPPER = new XmlMapper();

	public static XmlMapper build() {
		return XML_MAPPER;
	}

	public static <T> T parseObject(String xml, Class<T> tClass) {
		return XML_MAPPER.unmarshal(xml, tClass);
	}

	public static String toXmlString(Object object) {
		return XML_MAPPER.toXml(object, object.getClass(), StandardCharsets.UTF_8.name(), true);
	}

	public Map<String, String> toMap(String xml) throws XMLStreamException {
		if (StringUtils.isBlank(xml)) {
			return null;
		}
		return xmlToMap(xml);
	}

	private Map<String, String> xmlToMap(String xml) throws XMLStreamException {
		StringReader stringReader = new StringReader(xml);
		XMLEventReader reader = INPUT_FACTORY.createXMLEventReader(stringReader);
		Map<String, String> context = new LinkedHashMap<>();
		String startKey = null;
		while (reader.hasNext()) {
			XMLEvent xmlEvent = (XMLEvent) reader.next();
			startKey = calcXmlEvent(context, startKey, xmlEvent);
		}
		return context;
	}

	private Map<String, String> xmlToMap(String xml, List<XMLEvent> xmlEvents) throws XMLStreamException {
		StringReader stringReader = new StringReader(xml);
		XMLEventReader reader = INPUT_FACTORY.createXMLEventReader(stringReader);
		Map<String, String> context = new LinkedHashMap<>();
		String startKey = null;
		while (reader.hasNext()) {
			XMLEvent xmlEvent = (XMLEvent) reader.next();
			xmlEvents.add(xmlEvent);
			startKey = calcXmlEvent(context, startKey, xmlEvent);
		}
		return context;
	}

	private String calcXmlEvent(Map<String, String> context, String startKey, XMLEvent xmlEvent) {
		if (xmlEvent.isStartElement()) {
			startKey = xmlEvent.asStartElement().getName().getLocalPart();
		}
		if (xmlEvent.isCharacters()) {
			String value = xmlEvent.asCharacters().getData();
			if (startKey != null && StringUtils.isNotBlank(value)) {
				context.put(startKey, value);
			}
		}
		if (xmlEvent.isEndElement()) {
			startKey = null;
		}
		return startKey;
	}

	public <T> T unmarshal(String xml, ApplyClassFunc<T> function) {
		try {
			List<XMLEvent> xmlEvents = new ArrayList<>();
			Class<T> tClass = function.apply(xmlToMap(xml, xmlEvents));
			if (tClass == null) {
				throw new RuntimeException("没有反序列化的对象");
			}
			Unmarshaller unmarshaller = initUnmarshaller(tClass);
			XMLEventReader eventReader = StaxUtils.createXMLEventReader(xmlEvents);
			if (tClass.isAnnotationPresent(XmlRootElement.class)) {
				return (T) unmarshaller.unmarshal(eventReader);
			}
			else {
				JAXBElement<?> jaxbElement = unmarshaller.unmarshal(eventReader, tClass);
				return (T) jaxbElement.getValue();
			}
		}
		catch (JAXBException e) {
			throw new RuntimeException("Invalid JAXB configuration", e);
		}
		catch (XMLStreamException e) {
			throw new RuntimeException("XMLStreamException", e);
		}
	}

	public <T> T unmarshal(String xml, Class<T> tClass) {
		if (tClass == null) {
			throw new RuntimeException("反序列化对象 不能为空");
		}
		try {
			StringReader stringReader = new StringReader(xml);
			XMLStreamReader reader = INPUT_FACTORY.createXMLStreamReader(stringReader);
			Unmarshaller unmarshaller = initUnmarshaller(tClass);
			if (tClass.isAnnotationPresent(XmlRootElement.class)) {
				return (T) unmarshaller.unmarshal(reader);
			}
			else {
				JAXBElement<?> jaxbElement = unmarshaller.unmarshal(reader, tClass);
				return (T) jaxbElement.getValue();
			}
		}
		catch (JAXBException e) {
			throw new RuntimeException("Invalid JAXB configuration", e);
		}
		catch (XMLStreamException e) {
			throw new RuntimeException("XMLStreamException", e);
		}
	}

	public String toXml(Collection<?> root, String rootName, Class clazz, String encoding, Boolean jaxbFragment) {
		CollectionWrapper wrapper = new CollectionWrapper();
		wrapper.collection = root;
		JAXBElement<CollectionWrapper> wrapperElement = new JAXBElement<CollectionWrapper>(new QName(rootName),
				CollectionWrapper.class, wrapper);
		return toXml(wrapperElement, clazz, encoding, jaxbFragment);
	}

	public String toXml(Object root, Class clazz, String encoding, Boolean jaxbFragment) {
		Marshaller marshaller = null;
		try {
			StringWriter writer = new StringWriter();
			marshaller = initMarshaller(clazz);
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			if (StringUtils.isNotBlank(encoding)) {
				marshaller.setProperty(Marshaller.JAXB_ENCODING, encoding);
			}
			if (jaxbFragment != null) {
				marshaller.setProperty(Marshaller.JAXB_FRAGMENT, jaxbFragment);
			}
			marshaller.marshal(root, writer);
			return writer.toString();
		}
		catch (JAXBException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 封装Root Element 是 Collection的情况.
	 */
	public static class CollectionWrapper {

		@XmlAnyElement
		protected Collection<?> collection;

	}

	private Unmarshaller initUnmarshaller(Class<?> outputClass) throws JAXBException, JAXBException {
		Unmarshaller unmarshaller = jaxbContexts.createUnmarshaller(outputClass);
		return this.unmarshallerProcessor.apply(unmarshaller);
	}

	private Marshaller initMarshaller(Class<?> tClass) throws JAXBException {
		Marshaller marshaller = jaxbContexts.createMarshaller(tClass);
		return this.marshallerProcessor.apply(marshaller);
	}

}
