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
package com.workoss.boot.model;

import lombok.Getter;

import java.beans.Transient;
import java.util.Collections;
import java.util.List;

/**
 * 结果消息
 *
 * @author workoss
 */
@Getter
@SuppressWarnings("unused")
public class ResultInfo {

	/**
	 * 错误编码
	 */
	private String code = ResultCode.SUCCESS.getCode();

	/**
	 * 异常消息
	 */
	private String msg;

	/**
	 * 其他数据
	 */
	private Object data;

	public ResultInfo() {
	}

	public ResultInfo(String code, String msg, Object data) {
		this.code = code;
		this.msg = msg;
		this.data = data;
	}

	public ResultInfo code(String code) {
		this.code = code;
		return this;
	}

	public ResultInfo msg(String msg) {
		this.msg = msg;
		return this;
	}

	public ResultInfo data(Object data) {
		this.data = data;
		return this;
	}

	@Transient
	public boolean isSuccess() {
		return ResultCode.SUCCESS.getCode().equals(this.code);
	}

	public static ResultInfo success(String key, Object value) {
		return data(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(),
				Collections.singletonMap(key, value));
	}

	public static ResultInfo success(Object result) {
		return data(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), result);
	}

	public static ResultInfo data(String code, String msg) {
		return data(code, msg, null);
	}

	public static ResultInfo data(String code, String msg, Object data) {
		if (data == null) {
			new ResultInfo(code, msg, null);
		}
		if (data instanceof String) {
			return new ResultInfo(code, msg, Collections.singletonMap("message", data));
		}
		else if (data instanceof List) {
			return new ResultInfo(code, msg, Collections.singletonMap("list", data));
		}
		return new ResultInfo(code, msg, data);
	}

}
