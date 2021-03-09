package com.workoss.boot.storage.model;

import lombok.Data;
import lombok.ToString;

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
		return "StorageFileInfoListing{" +
				"nextToken='" + nextToken + '\'' +
				", maxKeys=" + maxKeys +
				", encodingType='" + encodingType + '\'' +
				", prefix='" + prefix + '\'' +
				", fileInfos=" + fileInfos +
				'}';
	}
}
