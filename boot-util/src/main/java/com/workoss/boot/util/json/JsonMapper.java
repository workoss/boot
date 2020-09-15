/*
 * Copyright © 2020-2021 workoss (WORKOSS)
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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.core.type.ResolvedType;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.workoss.boot.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;

/**
 * @author workoss
 */
@SuppressWarnings("ALL")
public class JsonMapper {

	private static Logger logger = LoggerFactory.getLogger(JsonMapper.class);

	public static final JsonMapper INSTANCE = new JsonMapper(JsonInclude.Include.NON_NULL);

	private ObjectMapper mapper;

	private JsonFactory jsonFactory = new JsonFactory();

	public JsonMapper() {
		this(JsonInclude.Include.NON_NULL);
	}

	public JsonMapper(JsonInclude.Include include) {
		mapper = new ObjectMapper();
		// 设置输出时包含属性的风格
		if (include != null) {
			mapper.setSerializationInclusion(include);
		}
		mapper.configure(MapperFeature.PROPAGATE_TRANSIENT_MARKER, true);
		// 设置输入时忽略在JSON字符串中存在但Java对象实际没有的属性
		mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
	}

	/**
	 * 创建只输出非Null且非Empty(如List.isEmpty)的属性到Json字符串的Mapper.
	 * <p>
	 * 注意，要小心使用, 特别留意empty的情况.
	 * @return jsonmapper
	 */
	public static JsonMapper nonEmptyMapper() {
		return new JsonMapper(JsonInclude.Include.NON_EMPTY);
	}

	/**
	 * 默认的全部输出的Mapper, 区别于INSTANCE，可以做进一步的配置
	 * @return jsonmapper
	 */
	public static JsonMapper build() {
		return INSTANCE;
	}

	public static String toJSONString(Object object) {
		return build().toJson(object);
	}

	public static JsonNode parse(String json) {
		return build().readTree(json);
	}

	public static JsonNode parse(byte[] bytes) {
		return build().readTree(bytes);
	}

	public static <T> T parseObject(String json, Class<T> tClass) {
		return build().fromJson(json, tClass);
	}

	public static <T> T parseObject(byte[] bytes, Class<T> tClass) {
		return build().fromJson(bytes, tClass);
	}

	public static <T> T parseObject(String json, JavaType javaType) {
		return build().fromJson(json, javaType);
	}

	public static <T> List<T> parseArray(String json, Class<T> tClass) {
		return parseObject(json, build().buildCollectionType(ArrayList.class, tClass));
	}

	public String toJson(Object object) {
		try {
			return mapper.writeValueAsString(object);
		}
		catch (IOException e) {
			logger.warn("write to json string error:{}", object, e);
			return null;
		}
	}

	public Map<String, String> toMap(String jsonString) {
		return toMap(jsonString.getBytes(StandardCharsets.UTF_8));
	}

	public Map<String, String> toMap(byte[] bytes) {
		JsonNode jsonNode = readTree(bytes);
		Map<String, String> context = new HashMap<>();
		if (jsonNode.size() <= 0) {
			return context;
		}
		jsonNode.fieldNames().forEachRemaining(s -> {
			JsonNode node = jsonNode.get(s);
			if (!node.isArray() && !node.isObject()) {
				context.put(s, node.asText());
			}
		});
		return context;
	}

	public JsonNode readTree(String jsonStr) {
		try {
			return this.mapper.readTree(jsonStr);
		}
		catch (IOException var3) {
			logger.warn("readTree error:", var3);
			return null;
		}
	}

	public JsonNode readTree(byte[] bytes) {
		try {
			return mapper.readTree(bytes);
		}
		catch (IOException e) {
			logger.warn("readTree error:", e);
			return null;
		}
	}

	public JsonNode readTree(InputStream inputStream) {
		try {
			return mapper.readTree(inputStream);
		}
		catch (IOException e) {
			logger.warn("readTree error:", e);
			return null;
		}
	}

	public <T> T fromJson(byte[] bytes, Function<Map<String, String>, Class<T>> func) {
		if (bytes == null || bytes.length == 0) {
			return null;
		}
		JsonNode jsonNode = readTree(bytes);
		Map<String, String> context = new HashMap<>();
		if (jsonNode.size() <= 0) {
			return null;
		}
		jsonNode.fieldNames().forEachRemaining(s -> {
			JsonNode node = jsonNode.get(s);
			if (!node.isArray() && !node.isObject()) {
				context.put(s, node.asText());
			}
		});
		Class<?> tClass = func.apply(context);
		if (tClass == null) {
			throw new RuntimeException("没有找到目标类");
		}
		return (T) mapper.convertValue(jsonNode, tClass);
	}

	public <T> T fromJson(byte[] bytes, Class<T> clazz) {
		if (bytes == null || bytes.length == 0) {
			return null;
		}
		try {
			return mapper.readValue(bytes, clazz);
		}
		catch (IOException e) {
			logger.warn("parse json bytes error:", e);
			return null;
		}
	}

	public <T> T fromJson(byte[] bytes, JavaType javaType) {
		if (bytes == null || bytes.length == 0) {
			return null;
		}
		try {
			return mapper.readValue(bytes, javaType);
		}
		catch (IOException e) {
			logger.warn("parse json bytes error:", e);
			return null;
		}
	}

	public <T> List<T> fromJson(String jsonString, ResolvedType resolvedType) {
		if (StringUtils.isEmpty(jsonString)) {
			return null;
		}
		try (JsonParser parser = jsonFactory.createParser(jsonString)) {
			MappingIterator<T> mappingIterator = mapper.readValues(parser, resolvedType);
			if (!mappingIterator.hasNext()) {
				return null;
			}
			return mappingIterator.readAll();
		}
		catch (IOException e) {
			logger.warn("parse json string error:" + jsonString, e);
			return null;
		}
	}

	/**
	 * 反序列化POJO或简单Collection如List.
	 * <p>
	 * 如果JSON字符串为Null或"null"字符串, 返回Null. 如果JSON字符串为"[]", 返回空集合.
	 * <p>
	 * 如需反序列化复杂Collection如List, 请使用fromJson(String, JavaType)
	 * @param jsonString json字符串
	 * @param clazz 类
	 * @param <T> 泛型
	 * @return 实例
	 */
	public <T> T fromJson(String jsonString, Class<T> clazz) {
		if (StringUtils.isEmpty(jsonString)) {
			return null;
		}

		try {
			return mapper.readValue(jsonString, clazz);
		}
		catch (IOException e) {
			logger.warn("parse json string error:" + jsonString, e);
			return null;
		}
	}

	/**
	 * 反序列化复杂Collection如List, contructCollectionType()或contructMapType()构造类型, 然后调用本函数.
	 * @param jsonString json字符串
	 * @param javaType javaType
	 * @param <T> 泛型
	 * @return 实例
	 */
	public <T> T fromJson(String jsonString, JavaType javaType) {
		if (StringUtils.isEmpty(jsonString)) {
			return null;
		}

		try {
			return (T) mapper.readValue(jsonString, javaType);
		}
		catch (IOException e) {
			logger.warn("parse json string error:" + jsonString, e);
			return null;
		}
	}

	/**
	 * 构造Collection类型.
	 * @param collectionClass 集合类型
	 * @param elementClass 集合元素类型
	 * @return javaType
	 */
	public JavaType buildCollectionType(Class<? extends Collection> collectionClass, Class<?> elementClass) {
		return mapper.getTypeFactory().constructCollectionType(collectionClass, elementClass);
	}

	public JavaType buildMapType(Class<? extends Map> mapClass, Class<?> keyClass, Class<?> valueClass) {
		return mapper.getTypeFactory().constructMapType(mapClass, keyClass, valueClass);
	}

	/**
	 * 当JSON里只含有Bean的部分属性時，更新一個已存在Bean，只覆盖該部分的属性.
	 * @param jsonString json字符串
	 * @param object obj
	 */
	public void update(String jsonString, Object object) {
		try {
			mapper.readerForUpdating(object).readValue(jsonString);
		}
		catch (JsonProcessingException e) {
			logger.warn("update json string:" + jsonString + " to object:" + object + " error.", e);
		}
	}

	public String toJsonP(String functionName, Object object) {
		return toJson(new JSONPObject(functionName, object));
	}

	/**
	 * 設定是否使用Enum的toString函數來讀寫Enum, 為False時時使用Enum的name()函數來讀寫Enum, 默認為False.
	 * 注意本函數一定要在Mapper創建後, 所有的讀寫動作之前調用.
	 */
	public void enableEnumUseToString() {
		mapper.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
		mapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
	}

	/**
	 * 取出Mapper做进一步的设置或使用其他序列化API.
	 * @return objectMapper
	 */
	public ObjectMapper getMapper() {
		return mapper;
	}

}
