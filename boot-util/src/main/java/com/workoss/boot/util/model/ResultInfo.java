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
package com.workoss.boot.util.model;

import java.beans.Transient;

/**
 * 结果消息
 *
 * @author workoss
 */
public class ResultInfo {

	/**
	 * 错误编码
	 */
	private String code = ResultCode.SUCCESS.getCode();

	/**
	 * 异常消息
	 */
	private String message;

	/**
	 * 其他数据
	 */
	private Object data;

	public ResultInfo() {
	}

	public ResultInfo(String code, String message, Object data) {
		this.code = code;
		this.message = message;
		this.data = data;
	}

	public String getCode() {
		return code;
	}

	public ResultInfo code(String code) {
		this.code = code;
		return this;
	}

	public String getMessage() {
		return message;
	}

	public ResultInfo message(String message) {
		this.message = message;
		return this;
	}

	public Object getData() {
		return data;
	}

	public ResultInfo data(Object data) {
		this.data = data;
		return this;
	}

	@Transient
	public boolean isSuccess() {
		return ResultCode.SUCCESS.getCode().equals(this.code);
	}

	public static ResultInfo success() {
		return success(null);
	}

	public static ResultInfo success(Object data) {
		return result(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), data);
	}

	public static ResultInfo result(String code, String message) {
		return result(code, message, null);
	}

	public static ResultInfo result(String code, String message, Object data) {
		return new ResultInfo(code, message, data);
	}

}
