/*
 * Copyright 2019-2023 workoss (https://www.workoss.com)
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
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.ResolvedType;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.workoss.boot.util.Assert;
import com.workoss.boot.util.exception.BootException;
import com.workoss.boot.util.reflect.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
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

	private static final JsonFactory jsonFactory = new JsonFactory();

	public JsonMapper() {
		this(JsonInclude.Include.NON_NULL);
	}

	public JsonMapper(JsonInclude.Include include) {
		mapper = new ObjectMapper();
		// 设置输出时包含属性的风格
		if (include != null) {
			mapper.setSerializationInclusion(include);
		}
		mapper.setDateFormat(new DateTimeFormat());
		mapper.configure(MapperFeature.PROPAGATE_TRANSIENT_MARKER, true);
		// 设置输入时忽略在JSON字符串中存在但Java对象实际没有的属性
		mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		// long(雪花算法ID) js丢失精度
		SimpleModule bigNumberModule = new SimpleModule();
		bigNumberModule.addSerializer(Long.class, BigNumberSerializer.INSTANCE);
		bigNumberModule.addSerializer(Long.TYPE, BigNumberSerializer.INSTANCE);
		bigNumberModule.addSerializer(BigInteger.class, BigNumberSerializer.INSTANCE);
		mapper.registerModule(bigNumberModule);
		// 判断是否存在 LocalDateTime 若是有 增加序列号 反序列化
		boolean existsJsr310 = ClassUtils
			.isPresent("com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer", null);
		if (existsJsr310) {
			mapper.registerModule(new Java8DateTimeModule().getModule());
		}
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

	public static byte[] toJSONBytes(Object object) {
		return build().toJsonBytes(object);
	}

	public static JsonNode parse(String json) {
		return build().readTree(json);
	}

	public static JsonNode parse(byte[] bytes) {
		return build().readTree(bytes);
	}

	public static JsonNode parse(InputStream inputStream) {
		return build().readTree(inputStream);
	}

	public static <T> T parseObject(String json, Class<T> tClass) {
		return build().fromJson(json, tClass);
	}

	public static <T> T parseObject(InputStream inputStream, Class<T> tClass) {
		return build().fromJson(inputStream, tClass);
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

	public static <T> T convertValue(Object fromValue, Class<T> toValueType) {
		return build().getMapper().convertValue(fromValue, toValueType);
	}

	public static <T> T convertValue(Object fromValue, JavaType javaType) {
		return build().getMapper().convertValue(fromValue, javaType);
	}

	public static <T> T convertValue(Object fromValue, TypeReference<T> toValueTypeRef) {
		return build().getMapper().convertValue(fromValue, toValueTypeRef);
	}

	public String toJson(Object object) {
		try {
			return mapper.writeValueAsString(object);
		}
		catch (IOException e) {
			throw new BootException(e);
		}
	}

	public byte[] toJsonBytes(Object object) {
		try {
			return mapper.writeValueAsBytes(object);
		}
		catch (IOException e) {
			throw new BootException(e);
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
		Assert.hasLength(jsonStr, "jsonStr not null");
		try {
			return this.mapper.readTree(jsonStr);
		}
		catch (IOException e) {
			throw new BootException(e);
		}
	}

	public JsonNode readTree(byte[] bytes) {
		try {
			return mapper.readTree(bytes);
		}
		catch (IOException e) {
			throw new BootException(e);
		}
	}

	public JsonNode readTree(InputStream inputStream) {
		try {
			return mapper.readTree(inputStream);
		}
		catch (IOException e) {
			throw new BootException(e);
		}
	}

	public <T> T fromJson(byte[] bytes, Function<Map<String, String>, Class<T>> func) {
		Assert.notNull(bytes, "bytes not null");
		Assert.notNull(func, "clazz not null");
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

	public <T> T fromJson(InputStream inputStream, Class<T> clazz) {
		Assert.notNull(inputStream, "inputStream not null");
		Assert.notNull(clazz, "clazz not null");
		try {
			return mapper.readValue(inputStream, clazz);
		}
		catch (IOException e) {
			throw new BootException(e);
		}
	}

	public <T> T fromJson(byte[] bytes, Class<T> clazz) {
		Assert.notNull(bytes, "bytes not null");
		Assert.notNull(clazz, "clazz not null");
		try {
			return mapper.readValue(bytes, clazz);
		}
		catch (IOException e) {
			throw new BootException(e);
		}
	}

	public <T> T fromJson(byte[] bytes, JavaType javaType) {
		Assert.notNull(bytes, "bytes not null");
		Assert.notNull(javaType, "javaType not null");
		try {
			return mapper.readValue(bytes, javaType);
		}
		catch (IOException e) {
			throw new BootException(e);
		}
	}

	public <T> List<T> fromJson(String jsonString, ResolvedType resolvedType) {
		Assert.hasLength(jsonString, "jsonString not empty");
		Assert.notNull(resolvedType, "resolvedType not null");
		try (JsonParser parser = jsonFactory.createParser(jsonString)) {
			MappingIterator<T> mappingIterator = mapper.readValues(parser, resolvedType);
			if (!mappingIterator.hasNext()) {
				return null;
			}
			return mappingIterator.readAll();
		}
		catch (IOException e) {
			throw new BootException(e);
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
		Assert.hasLength(jsonString, "jsonString not empty");
		Assert.notNull(clazz, "class not null");
		try {
			return mapper.readValue(jsonString, clazz);
		}
		catch (IOException e) {
			throw new BootException(e);
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
		Assert.hasLength(jsonString, "jsonString not empty");
		Assert.notNull(javaType, "javaType not null");
		try {
			return (T) mapper.readValue(jsonString, javaType);
		}
		catch (IOException e) {
			throw new BootException(e);
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
		Assert.hasLength(jsonString, "jsonString not empty");
		Assert.notNull(object, "object not null");
		try {
			mapper.readerForUpdating(object).readValue(jsonString);
		}
		catch (JsonProcessingException e) {
			throw new BootException(e);
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


	public static boolean isJson(String text){
		if (text == null){
			return false;
		}
		text = text.trim();
		if (text.startsWith("{") && text.endsWith("}")){
			return true;
		}
		if (text.startsWith("[") && text.endsWith("]")){
			return true;
		}

		return false;
	}

	public static <T>T getNodeValue(JsonNode node,String property,Function<JsonNode,T> transFunc){
		if (node == null || node.hasNonNull(property)){
			return null;
		}
		if (transFunc == null){
			return null;
		}
		return transFunc.apply(node);
	}

}
