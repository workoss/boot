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

import java.util.ArrayList;
import java.util.List;

/**
 * 文件目录中文件列表信息
 *
 * @author workoss
 */
@SuppressWarnings("unused")
public class StorageFileInfoListing {

	private String nextToken;

	private Integer maxKeys;

	private String encodingType;

	private String prefix;

	private List<StorageFileInfo> fileInfos = new ArrayList<>();

	public StorageFileInfoListing addFileInfo(StorageFileInfo storageFileInfo) {
		this.fileInfos.add(storageFileInfo);
		return this;
	}

	public String getNextToken() {
		return nextToken;
	}

	public StorageFileInfoListing setNextToken(String nextToken) {
		this.nextToken = nextToken;
		return this;
	}

	public Integer getMaxKeys() {
		return maxKeys;
	}

	public StorageFileInfoListing setMaxKeys(Integer maxKeys) {
		this.maxKeys = maxKeys;
		return this;
	}

	public String getEncodingType() {
		return encodingType;
	}

	public StorageFileInfoListing setEncodingType(String encodingType) {
		this.encodingType = encodingType;
		return this;
	}

	public String getPrefix() {
		return prefix;
	}

	public StorageFileInfoListing setPrefix(String prefix) {
		this.prefix = prefix;
		return this;
	}

	public List<StorageFileInfo> getFileInfos() {
		return fileInfos;
	}

	public StorageFileInfoListing setFileInfos(List<StorageFileInfo> fileInfos) {
		this.fileInfos = fileInfos;
		return this;
	}

	@Override
	public String toString() {
		return "StorageFileInfoListing{" + "nextToken='" + nextToken + '\'' + ", maxKeys=" + maxKeys
				+ ", encodingType='" + encodingType + '\'' + ", prefix='" + prefix + '\'' + ", fileInfos=" + fileInfos
				+ '}';
	}

}
