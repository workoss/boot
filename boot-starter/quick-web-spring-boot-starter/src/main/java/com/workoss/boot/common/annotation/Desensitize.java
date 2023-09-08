package com.workoss.boot.common.annotation;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import java.lang.annotation.*;

/**
 * 脱敏服务
 *
 * @author workoss
 */
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotationsInside
@JsonSerialize(using = ToStringSerializer.class)
@Documented
public @interface Desensitize {


    String symbol() default "*";
}
