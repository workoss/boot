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
package com.workoss.boot.plugin.mybatis;

/**
 * SqlParam
 *
 * @author workoss
 */
public class SqlParam {

	private int offset = 0;

	private int limit = 10;

	private String sortBy;

	private boolean shouldCount = false;

	private boolean shouldPage = true;

	public SqlParam() {
	}

	public SqlParam(int offset, int limit, String sortBy, boolean shoudlPage, boolean shouldCount) {
		this.offset = offset;
		this.limit = limit;
		this.sortBy = sortBy;
		this.shouldPage = shoudlPage;
		this.shouldCount = shouldCount;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public String getSortBy() {
		return sortBy;
	}

	public void setSortBy(String sortBy) {
		this.sortBy = sortBy;
	}

	public boolean getShouldCount() {
		return shouldCount;
	}

	public void setShouldCount(boolean shouldCount) {
		this.shouldCount = shouldCount;
	}

	public boolean getShouldPage() {
		return shouldPage;
	}

	public void setShouldPage(boolean shouldPage) {
		this.shouldPage = shouldPage;
	}

}
