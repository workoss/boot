package com.workoss.boot.web.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 分页返回VO
 *
 * @author workoss
 */
@Schema(name = "pageVO",description = "分页返回")
@Data
public class PageVO<E> {
    /**
     * 分页偏移量
     */
    @Schema(name = "offset", description = "分页偏移量")
    private Integer offset;
    /**
     * 每页大小
     */
    @Schema(name = "limit", description = "每页大小")
    private Integer limit;
    /**
     * 列表数据
     */
    @Schema(name = "data", description = "列表数据")
    private List<E> list = new ArrayList<>();
    /**
     * 分页偏移量
     */
    @Schema(name = "offset", description = "分页偏移量")
    private String sortBy;
    /**
     * 数据量大小
     */
    @Schema(name = "count", description = "数据量大小")
    private Integer count;

    public static <E> PageVO<E> empty(){
        return new PageVO<>();
    }

}
