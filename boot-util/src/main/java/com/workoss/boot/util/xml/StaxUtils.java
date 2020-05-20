package com.workoss.boot.util.xml;

import com.workoss.boot.util.StreamUtils;
import com.workoss.boot.util.StringUtils;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.stream.*;
import javax.xml.stream.events.XMLEvent;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public class StaxUtils {
    public static InputStream emptyInput() {
        return new ByteArrayInputStream(StreamUtils.EMPTY_CONTENT);
    }

    private static final XMLInputFactory inputFactory = createDefensiveInputFactory();
    private static final XMLResolver NO_OP_XML_RESOLVER =
            (publicID, systemID, base, ns) -> StreamUtils.emptyInput();


    public static XMLInputFactory createDefensiveInputFactory() {
        return createDefensiveInputFactory(XMLInputFactory::newInstance);
    }

    public static <T extends XMLInputFactory> T createDefensiveInputFactory(Supplier<T> instanceSupplier) {
        T inputFactory = instanceSupplier.get();
        inputFactory.setProperty(XMLInputFactory.SUPPORT_DTD, false);
        inputFactory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
        inputFactory.setXMLResolver(NO_OP_XML_RESOLVER);
        return inputFactory;
    }


}
