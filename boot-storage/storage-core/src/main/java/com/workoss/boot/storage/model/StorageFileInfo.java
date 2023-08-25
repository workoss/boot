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
package com.workoss.boot.storage.model;

import java.io.InputStream;
import java.util.Map;

/**
 * 存储文件信息
 *
 * @author workoss
 */
@SuppressWarnings("unused")
public class StorageFileInfo {

	private String bucketName;

	private String key;

	private String host;

	private Map<String, Object> metaData;

	private InputStream content;

	private String eTag;

	private Long lastModified;

	private Long size;

	private String owner;

	public StorageFileInfo() {
	}

	public String getBucketName() {
		return bucketName;
	}

	public StorageFileInfo setBucketName(String bucketName) {
		this.bucketName = bucketName;
		return this;
	}

	public String getKey() {
		return key;
	}

	public StorageFileInfo setKey(String key) {
		this.key = key;
		return this;
	}

	public String getHost() {
		return host;
	}

	public StorageFileInfo setHost(String host) {
		this.host = host;
		return this;
	}

	public Map<String, Object> getMetaData() {
		return metaData;
	}

	public StorageFileInfo setMetaData(Map<String, Object> metaData) {
		this.metaData = metaData;
		return this;
	}

	public InputStream getContent() {
		return content;
	}

	public StorageFileInfo setContent(InputStream content) {
		this.content = content;
		return this;
	}

	public String geteETag() {
		return eTag;
	}

	public StorageFileInfo setETag(String eTag) {
		this.eTag = eTag;
		return this;
	}

	public Long getLastModified() {
		return lastModified;
	}

	public StorageFileInfo setLastModified(Long lastModified) {
		this.lastModified = lastModified;
		return this;
	}

	public Long getSize() {
		return size;
	}

	public StorageFileInfo setSize(Long size) {
		this.size = size;
		return this;
	}

	public String getOwner() {
		return owner;
	}

	public StorageFileInfo setOwner(String owner) {
		this.owner = owner;
		return this;
	}

	@Override
	public String toString() {
		return "StorageFileInfo{" + "bucketName='" + bucketName + '\'' + ", key='" + key + '\'' + ", host='" + host
				+ '\'' + ", metaData=" + metaData + ", content=" + content + ", eTag='" + eTag + '\''
				+ ", lastModified=" + lastModified + ", size=" + size + '}';
	}

}
