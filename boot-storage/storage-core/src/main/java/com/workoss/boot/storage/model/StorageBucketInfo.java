package com.workoss.boot.storage.model;


import java.util.Date;

/**
 * 桶信息
 *
 * @author workoss
 */

public class StorageBucketInfo {

	private String name;

	private String owner;

	private Date creationDate;

	public StorageBucketInfo() {
	}

	public StorageBucketInfo(String name, String owner, Date creationDate) {
		this.name = name;
		this.owner = owner;
		this.creationDate = creationDate;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	@Override
	public String toString() {
		return "StorageBucketInfo{" +
				"name='" + name + '\'' +
				", owner='" + owner + '\'' +
				", creationDate=" + creationDate +
				'}';
	}
}
