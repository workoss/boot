package com.workoss.boot.util.xml;

import com.workoss.boot.util.StringUtils;
import com.workoss.boot.util.ApplyClassFunc;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.stream.*;
import javax.xml.stream.events.XMLEvent;
import java.io.StringReader;
import java.util.*;
import java.util.function.Function;

public class XmlMapper {

    private final JaxbContextContainer jaxbContexts = new JaxbContextContainer();

    private static final XMLInputFactory inputFactory = StaxUtils.createDefensiveInputFactory();

    private final Function<Unmarshaller, Unmarshaller> unmarshallerProcessor = Function.identity();

    private static final XmlMapper xmlMapper = new XmlMapper();

    public static XmlMapper build() {
        return xmlMapper;
    }

    public Map<String, String> toMap(String xml) throws XMLStreamException {
        if (StringUtils.isBlank(xml)) {
            return null;
        }
        return xmlToMap(xml);
    }

    private Map<String, String> xmlToMap(String xml) throws XMLStreamException {
        StringReader stringReader = new StringReader(xml);
        XMLEventReader reader = inputFactory.createXMLEventReader(stringReader);
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
        XMLEventReader reader = inputFactory.createXMLEventReader(stringReader);
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
            if (startKey != null && value != null) {
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
            } else {
                JAXBElement<?> jaxbElement = unmarshaller.unmarshal(eventReader, tClass);
                return (T) jaxbElement.getValue();
            }
        } catch (JAXBException e) {
            throw new RuntimeException("Invalid JAXB configuration", e);
        } catch (XMLStreamException e) {
            throw new RuntimeException("XMLStreamException", e);
        }
    }

    public <T> T unmarshal(String xml, Class<T> tClass) {
        if (tClass == null) {
            throw new RuntimeException("反序列化对象 不能为空");
        }
        try {
            StringReader stringReader = new StringReader(xml);
            XMLStreamReader reader = inputFactory.createXMLStreamReader(stringReader);
            Unmarshaller unmarshaller = initUnmarshaller(tClass);
            if (tClass.isAnnotationPresent(XmlRootElement.class)) {
                return (T) unmarshaller.unmarshal(reader);
            } else {
                JAXBElement<?> jaxbElement = unmarshaller.unmarshal(reader, tClass);
                return (T) jaxbElement.getValue();
            }
        } catch (JAXBException e) {
            throw new RuntimeException("Invalid JAXB configuration", e);
        } catch (XMLStreamException e) {
            throw new RuntimeException("XMLStreamException", e);
        }
    }


    private Unmarshaller initUnmarshaller(Class<?> outputClass) throws JAXBException, JAXBException {
        Unmarshaller unmarshaller = jaxbContexts.createUnmarshaller(outputClass);
        return this.unmarshallerProcessor.apply(unmarshaller);
    }
}
