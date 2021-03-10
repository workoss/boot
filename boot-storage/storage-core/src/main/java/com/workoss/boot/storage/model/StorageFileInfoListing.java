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
package com.workoss.boot.storage.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 文件目录中文件列表信息
 *
 * @author workoss
 */
public class StorageFileInfoListing {

	private String nextToken;

	private Integer maxKeys;

	private String encodingType;

	private String prefix;

	private List<StorageFileInfo> fileInfos = new ArrayList<>();

	public void addFileInfo(StorageFileInfo storageFileInfo) {
		this.fileInfos.add(storageFileInfo);
	}

	public String getNextToken() {
		return nextToken;
	}

	public void setNextToken(String nextToken) {
		this.nextToken = nextToken;
	}

	public Integer getMaxKeys() {
		return maxKeys;
	}

	public void setMaxKeys(Integer maxKeys) {
		this.maxKeys = maxKeys;
	}

	public String getEncodingType() {
		return encodingType;
	}

	public void setEncodingType(String encodingType) {
		this.encodingType = encodingType;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public List<StorageFileInfo> getFileInfos() {
		return fileInfos;
	}

	public void setFileInfos(List<StorageFileInfo> fileInfos) {
		this.fileInfos = fileInfos;
	}

	@Override
	public String toString() {
		return "StorageFileInfoListing{" + "nextToken='" + nextToken + '\'' + ", maxKeys=" + maxKeys
				+ ", encodingType='" + encodingType + '\'' + ", prefix='" + prefix + '\'' + ", fileInfos=" + fileInfos
				+ '}';
	}

}
