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
package com.workoss.boot.web.advice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.workoss.boot.exception.QuickException;
import com.workoss.boot.model.ResultInfo;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * spring-boot 返回结果统一包装
 *
 * @author workoss
 */
@SuppressWarnings("ALL")
@RestControllerAdvice
public class GlobalResponseBodyAdvice implements ResponseBodyAdvice<Object> {

	private final ObjectMapper objectMapper;

	public GlobalResponseBodyAdvice(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	@Override
	public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
		String methodName = returnType.getMethod().getDeclaringClass().getName();
		return !methodName.contains("org.springframework.boot.actuate") && !methodName.contains("org.springdoc")
				&& !returnType.getGenericParameterType().equals(ResultInfo.class);
	}

	@Override
	public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
			Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request,
			ServerHttpResponse response) {
		response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
		if (body == null && returnType.getGenericParameterType().equals(String.class)) {
			return writeResponse(ResultInfo.success(null));
		}
		if (body instanceof String message) {
			return writeResponse(ResultInfo.success(message));
		}
		return ResultInfo.success(body);
	}

	private String writeResponse(ResultInfo resultInfo) {
		try {
			return objectMapper.writeValueAsString(resultInfo);
		}
		catch (JsonProcessingException e) {
			throw new QuickException("-1", "resultInfo返回失败");
		}
	}

}
