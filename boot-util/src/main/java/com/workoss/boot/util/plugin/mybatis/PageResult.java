package com.workoss.boot.util.plugin.mybatis;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @Description: 分页工具类page对象
 * @Author: luanfeng
 * @Date: 2017/8/11 8:10
 * @Version: 1.0.0
 */
public class PageResult<E> extends ArrayList<E> implements Closeable {

    private int offset = 0;

    private int limit = 10;

    private int count = 0;

    private int pageNo = 1;

    private String sortBy;

    private boolean shouldCount=false;


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

    public int getPageNo() {
        if (this.limit>0){
            return this.offset/this.limit+1;
        }
        return 1;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
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

    @Override
    public void close() throws IOException {
        SqlHelper.clearSqlParam();
    }

}
