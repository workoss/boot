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
package com.workoss.boot.storage.exception;

/**
 * 存储客户端找不到异常
 *
 * @author workoss
 */
public class StorageClientNotFoundException extends StorageException {

	public StorageClientNotFoundException(String s) {
		super(s);
	}

	public StorageClientNotFoundException(String errcode, String errorMsg) {
		super(errcode, errorMsg);
	}

	public StorageClientNotFoundException(String s, Throwable throwable) {
		super(s, throwable);
	}

	public StorageClientNotFoundException(Throwable throwable) {
		super(throwable);
	}

}
