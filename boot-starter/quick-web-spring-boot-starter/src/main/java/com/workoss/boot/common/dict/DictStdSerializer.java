package com.workoss.boot.common.dict;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.workoss.boot.common.annotation.Dict;
import com.workoss.boot.model.IEnum;
import com.workoss.boot.util.EnumUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * @author workoss
 */
@Slf4j
public class DictStdSerializer extends StdSerializer<Object> implements ContextualSerializer {

    private Dict dict;

    private String propertyName;

    public DictStdSerializer() {
        super(Object.class);
        log.info("[DICT] dict init");
    }

    protected DictStdSerializer(Dict dict, String propertyName) {
        super(Object.class);
        this.dict = dict;
        this.propertyName = propertyName;
        log.info("[DICT] dict enumType:{} keyName:{} defaultValue:{} remote:{}", dict.enumType(), dict.keyName(),
                dict.defaultValue(), dict.remote());
    }

    @Override
    public void serialize(Object value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        if (value == null || dict == null) {
            return;
        }

        if (value.getClass().isAssignableFrom(Enum.class)){
            gen.writeString(((Enum)value).name());
            return;
        }

        if (value instanceof Integer val) {
            gen.writeNumber(val);
        } else if (value instanceof String val) {
            gen.writeString(val);
        } else if (value instanceof Short val) {
            gen.writeNumber(val.intValue());
        }
        if (dict.enumType() == DefaultNullEnum.class){
            return;
        }
        IEnum<?, String, ?> byCode = EnumUtil.getByCode(dict.enumType(), value);
        if (byCode == null) {
            return;
        }
        gen.writeFieldName(propertyName + dict.suffix());
        gen.writeString(byCode.getDesc());
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property) throws JsonMappingException {
        Dict annotation = property.getAnnotation(Dict.class);
        return new DictStdSerializer(annotation, property.getName());
    }


}
