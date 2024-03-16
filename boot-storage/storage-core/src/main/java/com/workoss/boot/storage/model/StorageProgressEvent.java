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
package com.workoss.boot.storage.model;

/**
 * 进度事件
 *
 * @author workoss
 */
@SuppressWarnings("unused")
public class StorageProgressEvent {

	private final long bytes;

	private final String eventType;

	private final long bytesTransferred;

	public StorageProgressEvent(long bytes, String eventType, long bytesTransferred) {
		this.bytes = bytes;
		this.eventType = eventType;
		this.bytesTransferred = bytesTransferred;
	}

	public long getBytes() {
		return bytes;
	}

	public String getEventType() {
		return eventType;
	}

	public long getBytesTransferred() {
		return bytesTransferred;
	}

}
