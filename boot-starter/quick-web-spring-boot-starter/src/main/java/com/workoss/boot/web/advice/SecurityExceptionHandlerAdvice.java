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

import com.workoss.boot.model.ResultCode;
import com.workoss.boot.model.ResultInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@SuppressWarnings("ALL")
@Slf4j
@RestControllerAdvice
public class SecurityExceptionHandlerAdvice {

	private final MessageSource messageSource;

	public SecurityExceptionHandlerAdvice(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	@ExceptionHandler(AccessDeniedException.class)
	public ResultInfo accessDeniedException(AccessDeniedException exception) {
		log.warn("[ACCESS_EXCEPTION] 数据异常:", exception);
		ResultCode resultCode = ResultCode.AUTHORIZATION_ERROR;
		return ResultInfo.data(resultCode.getCode(), messageSource.getMessage(resultCode.getCode(), null,
				resultCode.getMessage(), LocaleContextHolder.getLocale()));
	}

}
