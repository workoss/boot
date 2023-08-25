package com.workoss.boot.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author workoss
 */
@Data
public class PageModel<E> {

    private static final PageModel EMPTY = new PageModel<>();
    /**
     * 分页偏移量
     */
    private Integer offset;
    /**
     * 每页大小
     */
    private Integer limit;
    /**
     * 列表数据
     */
    private List<E> list = new ArrayList<>();
    /**
     * 分页偏移量
     */
    private String sortBy;
    /**
     * 数据量大小
     */
    private Integer count;

    public static <E> PageModel<E> empty() {
        return EMPTY;
    }
}
