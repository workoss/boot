/*
 * Copyright 2019-2022 workoss (https://www.workoss.com)
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
		return "StorageBucketInfo{" + "name='" + name + '\'' + ", owner='" + owner + '\'' + ", creationDate="
				+ creationDate + '}';
	}

}
