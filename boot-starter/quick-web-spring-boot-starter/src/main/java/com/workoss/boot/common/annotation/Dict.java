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
@Target({ ElementType.FIELD, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotationsInside
@JsonSerialize(using = DictStdSerializer.class)
@Documented
public @interface Dict {

	/**
	 * 枚举类
	 * @return
	 */
	Class<? extends IEnum<?, String, ?>> enumType() default DefaultNullEnum.class;

	/**
	 * 默认 enumType.getName
	 * @return
	 */
	String keyName() default "";

	/**
	 * 默认值
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
	 * @return
	 */
	boolean remote() default false;

}
