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
package com.workoss.boot.exception;

import com.workoss.boot.util.exception.BootException;

/**
 * 通用异常
 *
 * @author workoss
 */
public class QuickException extends BootException {

	private Boolean cover = false;

	public QuickException(String code) {
		this(code, null, false);
	}

	public QuickException(String code, Throwable throwable) {
		this("-2", throwable.toString(), false);
	}

	public QuickException(String code, String msg) {
		this(code, msg, false);
	}

	public QuickException(String code, String msg, Boolean cover) {
		super(code, msg);
		this.cover = (cover == null ? false : cover);
	}

	public Boolean getCover() {
		return cover;
	}

	public void setCover(Boolean cover) {
		this.cover = cover;
	}

}
