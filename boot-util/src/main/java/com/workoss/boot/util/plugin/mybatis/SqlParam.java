package com.workoss.boot.util.plugin.mybatis;

public class SqlParam {

    private int offset = 0;

    private int limit = 10;

    private int count;

    private int page = 1;

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

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
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
