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
package com.workoss.boot.util.model;

/**
 * @author workoss
 */
public enum ResultCode {

	/**
	 * 请求成功
	 */
	SUCCESS("0", "OK"),
	/**
	 * 参数校验错误
	 */
	VALID_ERROR("-1", "参数校验错误"),
	/**
	 * 服务异常
	 */
	SERVER_ERROR("-2", "服务异常");

	private final String code;

	private final String message;

	ResultCode(String code, String message) {
		this.code = code;
		this.message = message;
	}

	public String getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}

}
