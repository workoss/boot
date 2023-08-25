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
import com.workoss.boot.util.StringUtils;
import com.workoss.boot.util.exception.ExceptionUtils;
import com.workoss.boot.util.model.ResultCode;
import com.workoss.boot.util.model.ResultInfo;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Locale;
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

		return ResultInfo.result("-1", errorInfo);
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(ConstraintViolationException.class)
	public ResultInfo validationErrorHandler(ConstraintViolationException exception) {
		String errorInfo = exception.getConstraintViolations().stream().map(error -> {
			log.warn("[GLOBAL_EXCEPTION] 参数校验异常: {}:{}", error.getPropertyPath(), error.getMessage());
			return error.getMessage();
		}).collect(Collectors.joining(""));
		return ResultInfo.result("-1", errorInfo);
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(BindException.class)
	public ResultInfo bindingExceptionHandler(BindException exception) {
		String errorInfo = exception.getBindingResult().getAllErrors().stream().map(error -> {
			log.warn("[GLOBAL_EXCEPTION] 参数异常: {}:{}", error.getArguments(), error.getDefaultMessage());
			return error.getDefaultMessage();
		}).collect(Collectors.joining(""));
		return ResultInfo.result("-1", errorInfo);
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(IllegalArgumentException.class)
	public ResultInfo defaultException(IllegalArgumentException exception) {
		log.error("[GLOBAL_EXCEPTION] 参数异常:", exception);
		return ResultInfo.result("-1", exception.getMessage());
	}

	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler(QuickException.class)
	public ResultInfo serviceException(QuickException exception) {
		String errmsg = exception.getMsg();
		if (!ResultCode.SUCCESS.getCode().equalsIgnoreCase(exception.getCode()) && exception.getCover()) {
			Locale locale = LocaleContextHolder.getLocale();
			errmsg = messageSource.getMessage(exception.getCode(), null, errmsg, locale);
		}
		log.warn("[GLOBAL_EXCEPTION] 业务异常:{}", errmsg == null ? StringUtils.EMPTY : errmsg,
				ExceptionUtils.toShortString(exception, 2));
		return ResultInfo.result(exception.getCode(), errmsg);
	}

	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler(Exception.class)
	public ResultInfo serviceException(Exception exception) {
		log.warn("[GLOBAL_EXCEPTION]服务异常:", ExceptionUtils.toShortString(exception, 2));
		return ResultInfo.result("-2", exception.getMessage());
	}

	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler(Throwable.class)
	public ResultInfo serviceException(Throwable throwable) {
		log.warn("[GLOBAL_EXCEPTION] 服务异常:", ExceptionUtils.toShortString(throwable, 2));
		return ResultInfo.result("-2", throwable.getMessage());
	}

}
