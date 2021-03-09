package com.workoss.boot.storage.model;


import java.io.InputStream;
import java.util.Map;

/**
 * 存储文件信息
 *
 * @author workoss
 */
public class StorageFileInfo {

	private String bucketName;

	private String key;

	private String host;

	private Map<String, Object> metaData;

	private InputStream content;

	private String eTag;

	private Long lastModified;

	public StorageFileInfo() {
	}

	public String getBucketName() {
		return bucketName;
	}

	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public Map<String, Object> getMetaData() {
		return metaData;
	}

	public void setMetaData(Map<String, Object> metaData) {
		this.metaData = metaData;
	}

	public InputStream getContent() {
		return content;
	}

	public void setContent(InputStream content) {
		this.content = content;
	}

	public String geteETag() {
		return eTag;
	}

	public void setETag(String eTag) {
		this.eTag = eTag;
	}

	public Long getLastModified() {
		return lastModified;
	}

	public void setLastModified(Long lastModified) {
		this.lastModified = lastModified;
	}

	@Override
	public String toString() {
		return "StorageFileInfo{" +
				"bucketName='" + bucketName + '\'' +
				", key='" + key + '\'' +
				", host='" + host + '\'' +
				", metaData=" + metaData +
				", content=" + content +
				", eTag='" + eTag + '\'' +
				", lastModified=" + lastModified +
				'}';
	}
}
