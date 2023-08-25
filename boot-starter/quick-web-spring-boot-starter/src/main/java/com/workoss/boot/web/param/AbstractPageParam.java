package com.workoss.boot.web.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

/**
 * 分页入参
 *
 * @author workoss
 */
@Getter
@Schema(name = "分页参数", description = "基础分页入参")
public abstract class AbstractPageParam {
    /**
     * 偏移量
     */
    @Schema(name = "offset", description = "分页偏移量 优先", example = "1")
    private Integer offset = 0;
    /**
     * 每页大小
     */
    @NotNull(message = "分页limit不能为空")
    @NotEmpty(message = "分页limit不能为空")
    @Size(min = 1, max = 500, message = "分页大小为1-500")
    @Schema(name = "limit", description = "分页每页大小", example = "10")
    private Integer limit = 10;
    /**
     * 第几页
     */
    @Schema(name = "pageNo", description = "第几页 跟offset二选一", example = "1")
    private Integer pageNo = 1;
    /**
     * 排序参数
     */
    @Schema(name = "sortBy", description = "分页排序", example = "order by modifyTime desc,age asc")
    private String sortBy;

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
        if (pageNo != null && this.offset == null) {
            this.offset = (pageNo - 1) * this.limit;
        }
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }
}
