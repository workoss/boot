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
package com.workoss.boot.util.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.databind.ser.std.NumberSerializer;

import java.io.IOException;

/**
 * 大数字型 序列化，js端 大数字超过范围会丢失精度
 *
 * @author workoss
 */
@JacksonStdImpl
public class BigNumberSerializer extends NumberSerializer {

	private static final Long MAX_JS_VALUE = 9007199254740991L;

	private static final Long MIN_JS_VALUE = -9007199254740991L;

	public static final BigNumberSerializer INSTANCE = new BigNumberSerializer(Number.class);

	/**
	 * @param rawType 类型
	 * @since 2.5
	 */
	public BigNumberSerializer(Class<? extends Number> rawType) {
		super(rawType);
	}

	@Override
	public void serialize(Number value, JsonGenerator g, SerializerProvider provider) throws IOException {
		// long超出js范围 返回字符串，否则数字
		if (value.longValue() < MAX_JS_VALUE && value.longValue() > MIN_JS_VALUE) {
			super.serialize(value, g, provider);
			return;
		}
		g.writeString(value.toString());
	}

}
