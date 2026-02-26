package com.salary.admin.common;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 通用分页结果封装类
 * * @param <T> 列表数据的数据类型
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "通用分页数据封装")
public class PageResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "总记录数")
    private Long total;

    @Schema(description = "列表数据")
    private List<T> list;

    @Schema(description = "当前页码")
    private Long pageNum;

    @Schema(description = "每页记录数")
    private Long pageSize;

    @Schema(description = "总页数")
    private Long totalPage;

    /**
     * 场景 1：直接将 MyBatis-Plus 的 IPage 对象转换为 PageResult (单表查询无 DTO 转换时)
     *
     * @param page MyBatis-Plus 的分页结果
     * @param <T>  实体类型
     * @return 统一分页结果
     */
    public static <T> PageResult<T> of(IPage<T> page) {
        if (page == null) {
            return new PageResult<>();
        }
        return PageResult.<T>builder()
                .total(page.getTotal())
                .list(page.getRecords())
                .pageNum(page.getCurrent())
                .pageSize(page.getSize())
                .totalPage(page.getPages())
                .build();
    }

    /**
     * 场景 2：支持类型转换的分页构造 (非常常用：将数据库实体 DO 转换为返回给前端的 DTO 时)
     *
     * @param page MyBatis-Plus 的分页结果 (包含原始分页信息)
     * @param list 转换后的新数据列表
     * @param <T>  原始实体类型
     * @param <R>  转换后的 DTO 类型
     * @return 统一分页结果
     */
    public static <T, R> PageResult<R> of(IPage<T> page, List<R> list) {
        if (page == null) {
            return new PageResult<>();
        }
        return PageResult.<R>builder()
                .total(page.getTotal())
                .list(list) // 使用转换后的列表替换原始 records
                .pageNum(page.getCurrent())
                .pageSize(page.getSize())
                .totalPage(page.getPages())
                .build();
    }
}
