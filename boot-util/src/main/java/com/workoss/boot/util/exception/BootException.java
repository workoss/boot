/*
 * Copyright 2019-2021 workoss (https://www.workoss.com)
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
package com.workoss.boot.util.exception;

/**
 * boot异常
 *
 * @author workoss
 */
public class BootException extends RuntimeException {

	private String errcode;

	private String errmsg;

	public BootException() {
	}

	public BootException(String errcode) {
		super("errcode:" + errcode);
		this.errcode = errcode;
	}

	public BootException(String errcode, String errmsg) {
		super("errcode:" + errcode + ",errmsg:" + errmsg);
		this.errcode = errcode;
		this.errmsg = errmsg;
	}

	public BootException(Throwable throwable) {
		super("errmsg:" + throwable.getMessage(), throwable);
	}

	public BootException(String errcode, Throwable throwable) {
		super("errcode:" + errcode, throwable);
		this.errcode = errcode;
	}

	public String getErrcode() {
		return this.errcode;
	}

	public String getErrmsg() {
		return this.errmsg;
	}

	@Override
	public String toString() {
		return "{\"errcode\":\"" + this.errcode + "\",\"errmsg\":\"" + this.errmsg + "\"}";
	}

}
