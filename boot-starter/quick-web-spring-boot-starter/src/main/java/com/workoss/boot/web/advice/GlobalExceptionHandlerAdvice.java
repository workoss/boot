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
package com.workoss.boot.web.advice;

import com.workoss.boot.exception.QuickException;
import com.workoss.boot.model.ResultCode;
import com.workoss.boot.model.ResultInfo;
import com.workoss.boot.util.StringUtils;
import com.workoss.boot.util.exception.ExceptionUtils;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.MyBatisSystemException;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@SuppressWarnings("ALL")
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandlerAdvice {

	private final MessageSource messageSource;

	public GlobalExceptionHandlerAdvice(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResultInfo validationErrorHandler(MethodArgumentNotValidException exception) {
		String errorInfo = exception.getBindingResult().getAllErrors().stream().map(error -> {
			log.warn("[GLOBAL_EXCEPTION] 参数校验异常: {}", error);
			return error.getDefaultMessage();
		}).collect(Collectors.joining(";"));

		return ResultInfo.data("-1", errorInfo);
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(ConstraintViolationException.class)
	public ResultInfo validationErrorHandler(ConstraintViolationException exception) {
		String errorInfo = exception.getConstraintViolations().stream().map(error -> {
			log.warn("[GLOBAL_EXCEPTION] 参数校验异常: {}:{}", error.getPropertyPath(), error.getMessage());
			return error.getMessage();
		}).collect(Collectors.joining(""));
		return ResultInfo.data("-1", errorInfo);
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(BindException.class)
	public ResultInfo bindingExceptionHandler(BindException exception) {
		String errorInfo = exception.getBindingResult().getAllErrors().stream().map(error -> {
			log.warn("[GLOBAL_EXCEPTION] 参数异常: {}:{}", error.getArguments(), error.getDefaultMessage());
			return error.getDefaultMessage();
		}).collect(Collectors.joining(""));
		return ResultInfo.data("-1", errorInfo);
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(IllegalArgumentException.class)
	public ResultInfo defaultException(IllegalArgumentException exception) {
		log.error("[GLOBAL_EXCEPTION] 参数异常:", exception);
		return ResultInfo.data("-1", exception.getMessage());
	}

	@ResponseStatus(HttpStatus.OK)
	@ExceptionHandler(QuickException.class)
	public ResultInfo serviceException(QuickException exception) {
		String errmsg = exception.getMsg();
		if (!ResultCode.SUCCESS.getCode().equalsIgnoreCase(exception.getCode()) && exception.getCover()) {
			errmsg = messageSource.getMessage(exception.getCode(), null, errmsg, LocaleContextHolder.getLocale());
		}
		if (StringUtils.isBlank(errmsg)) {
			errmsg = "QuickException";
		}
		log.warn("[GLOBAL_EXCEPTION] 业务异常:{}", errmsg);
		return ResultInfo.data(exception.getCode(), errmsg);
	}

	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler(MyBatisSystemException.class)
	public ResultInfo mybatisException(MyBatisSystemException exception) {
		log.error("[GLOBAL_EXCEPTION] 数据异常:", exception);
		return ResultInfo.data("-3", messageSource.getMessage("-3", null, "数据异常", LocaleContextHolder.getLocale()));
	}

	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler({ Throwable.class })
	public ResultInfo serviceException(Throwable exception) {
		String msg = ExceptionUtils.toString(exception);
		log.warn("[GLOBAL_EXCEPTION] 服务异常:{}", msg);
		return ResultInfo.data("-2", messageSource.getMessage("-2", null, msg, LocaleContextHolder.getLocale()));
	}

}
