package com.workoss.boot.util.xml;

import com.workoss.boot.util.StringUtils;
import org.springframework.util.xml.StaxUtils;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.stream.StreamFilter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.StringReader;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public class XmlMapper {

    private final JaxbContextContainer jaxbContexts = new JaxbContextContainer();

    private static final XMLInputFactory inputFactory = StaxUtils.createDefensiveInputFactory();

    private final Function<Unmarshaller, Unmarshaller> unmarshallerProcessor = Function.identity();

    private static final XmlMapper xmlMapper = new XmlMapper();

    public static XmlMapper build(){
        return xmlMapper;
    }

    public  Map<String, String> toMap(String xml) throws XMLStreamException {
        if (StringUtils.isBlank(xml)){
            return null;
        }
        StringReader stringReader = new StringReader(xml);
        XMLStreamReader reader = inputFactory.createXMLStreamReader(stringReader);
        return xmlToMap(reader);
    }

    private  Map<String, String> xmlToMap(XMLStreamReader reader) throws XMLStreamException {
        Map<String, String> context = new LinkedHashMap<>();
        String startKey = null;
        while (reader.hasNext()) {
            if (reader.isStartElement()) {
                startKey = reader.getName().getLocalPart();
            }
            if (reader.isCharacters()) {
                String value = reader.getText();
                if (startKey != null && value != null) {
                    context.put(startKey, value);
                }
            }
            if (reader.isEndElement()) {
                startKey = null;
            }
        }
        return context;
    }


    public  <T> T unmarshal(String xml, XmlToClassFunction<T> function) {
        if (function == null) {
            throw new RuntimeException("function 不能为空");
        }
        try {
            StringReader stringReader = new StringReader(xml);
            XMLStreamReader reader = inputFactory.createXMLStreamReader(stringReader);
            Map<String, String> context = xmlToMap(reader);
            Class<T> tClass = function.apply(context);
            if (tClass == null){
                throw new RuntimeException("没有反序列化的对象");
            }
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
        Unmarshaller unmarshaller =jaxbContexts.createUnmarshaller(outputClass);
        return this.unmarshallerProcessor.apply(unmarshaller);
    }
}
