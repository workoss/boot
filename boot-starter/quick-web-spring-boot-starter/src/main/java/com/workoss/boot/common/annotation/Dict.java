package com.workoss.boot.common.annotation;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.workoss.boot.common.dict.DefaultNullEnum;
import com.workoss.boot.common.dict.DictStdSerializer;
import com.workoss.boot.model.IEnum;

import java.lang.annotation.*;

/**
 * 字典注解
 *
 * @author workoss
 */
@Inherited
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotationsInside
@JsonSerialize(using = DictStdSerializer.class)
@Documented
public @interface Dict {
    /**
     * 枚举类
     *
     * @return
     */
    Class<? extends IEnum<?, String, ?>> enumType() default DefaultNullEnum.class;

    /**
     * 默认 enumType.getName
     *
     * @return
     */
    String keyName() default "";

    /**
     * 默认值
     *
     * @return
     */
    String defaultValue() default "";

    /**
     * 枚举返回后面拼接
     * @return
     */
    String suffix() default "Desc";

    /**
     * 是否获取远程
     *
     * @return
     */
    boolean remote() default false;

}
