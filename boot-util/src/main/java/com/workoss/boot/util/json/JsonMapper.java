/*
 * The MIT License
 * Copyright © 2020-2021 workoss
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.workoss.boot.util.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.core.type.ResolvedType;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.workoss.boot.util.ApplyClassFunc;
import com.workoss.boot.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	 */
	public static JsonMapper nonEmptyMapper() {
		return new JsonMapper(JsonInclude.Include.NON_EMPTY);
	}

	/**
	 * 默认的全部输出的Mapper, 区别于INSTANCE，可以做进一步的配置
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

	/**
	 * Object可以是POJO，也可以是Collection或数组。 如果对象为Null, 返回"null". 如果集合为空集合, 返回"[]".
	 */
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

	public <T> T fromJson(byte[] bytes, ApplyClassFunc func) {
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
	 * 反序列化POJO或简单Collection如List<String>.
	 * <p>
	 * 如果JSON字符串为Null或"null"字符串, 返回Null. 如果JSON字符串为"[]", 返回空集合.
	 * <p>
	 * 如需反序列化复杂Collection如List<MyBean>, 请使用fromJson(String, JavaType)
	 *
	 * @see #fromJson(String, JavaType)
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
	 * 反序列化复杂Collection如List<Bean>, contructCollectionType()或contructMapType()构造类型,
	 * 然后调用本函数.
	 *
	 * @see #(Class, Class...)
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
	 */
	public JavaType buildCollectionType(Class<? extends Collection> collectionClass, Class<?> elementClass) {
		return mapper.getTypeFactory().constructCollectionType(collectionClass, elementClass);
	}

	/**
	 * 构造Map类型.
	 */
	public JavaType buildMapType(Class<? extends Map> mapClass, Class<?> keyClass, Class<?> valueClass) {
		return mapper.getTypeFactory().constructMapType(mapClass, keyClass, valueClass);
	}

	/**
	 * 当JSON里只含有Bean的部分属性時，更新一個已存在Bean，只覆盖該部分的属性.
	 */
	public void update(String jsonString, Object object) {
		try {
			mapper.readerForUpdating(object).readValue(jsonString);
		}
		catch (JsonProcessingException e) {
			logger.warn("update json string:" + jsonString + " to object:" + object + " error.", e);
		}
		catch (IOException e) {
			logger.warn("update json string:" + jsonString + " to object:" + object + " error.", e);
		}
	}

	/**
	 * 輸出JSONP格式數據.
	 */
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
	 */
	public ObjectMapper getMapper() {
		return mapper;
	}

}
