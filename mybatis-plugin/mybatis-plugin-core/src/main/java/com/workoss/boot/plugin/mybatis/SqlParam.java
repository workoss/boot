/*
 * The MIT License
 * Copyright © 2020-2021 workoss
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.workoss.boot.plugin.mybatis;

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
