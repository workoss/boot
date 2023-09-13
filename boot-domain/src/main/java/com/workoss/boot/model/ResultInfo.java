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

import java.beans.Transient;
import java.util.Collections;
import java.util.List;

/**
 * 结果消息
 *
 * @author workoss
 */
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
	private Object result;

	public ResultInfo() {
	}

	public ResultInfo(String code, String msg, Object result) {
		this.code = code;
		this.msg = msg;
		this.result = result;
	}

	public String getCode() {
		return code;
	}

	public ResultInfo code(String code) {
		this.code = code;
		return this;
	}

	public String getMsg() {
		return msg;
	}

	public ResultInfo msg(String msg) {
		this.msg = msg;
		return this;
	}

	public Object getResult() {
		return result;
	}

	public ResultInfo result(Object result) {
		this.result = result;
		return this;
	}

	@Transient
	public boolean isSuccess() {
		return ResultCode.SUCCESS.getCode().equals(this.code);
	}

	public static ResultInfo success(String key, Object value) {
		return result(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(),
				Collections.singletonMap(key, value));
	}

	public static ResultInfo success(Object result) {
		return result(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), result);
	}

	public static ResultInfo result(String code, String msg) {
		return result(code, msg, null);
	}

	public static ResultInfo result(String code, String msg, Object result) {
		if (result == null) {
			new ResultInfo(code, msg, result);
		}
		if (result instanceof String) {
			return new ResultInfo(code, msg, Collections.singletonMap("message", result));
		}
		else if (result instanceof List) {
			return new ResultInfo(code, msg, Collections.singletonMap("list", result));
		}
		return new ResultInfo(code, msg, result);
	}

}
