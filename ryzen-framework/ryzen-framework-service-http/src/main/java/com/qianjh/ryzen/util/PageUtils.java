package com.qianjh.ryzen.util;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qianjh.ryzen.api.dto.CursorPage;
import com.qianjh.ryzen.api.dto.OffsetPage;

import java.util.List;

/**
 * 分页工具
 *
 * @author QianJH
 */
public final class PageUtils {

    /**
     * 位移分页
     *
     * @param entityPage 实体分页
     * @param dtoRecords DTO记录
     * @param <ENTITY>   实体泛型
     * @param <DTO>      DTO范型
     * @return 分页结果
     */
    public static <ENTITY, DTO> OffsetPage<DTO> wrap(Page<ENTITY> entityPage, List<DTO> dtoRecords) {
        return OffsetPage.<DTO>builder()
                .records(dtoRecords)
                .current(entityPage.getCurrent())
                .size(entityPage.getSize())
                .total(entityPage.getTotal())
                .pages(entityPage.getPages())
                .build();
    }

    /**
     * 游标分页
     *
     * @param dtoRecords DTO记录
     * @param <DTO>      DTO范型
     * @return 分页结果
     */
    public static <DTO> CursorPage<DTO> wrap(List<DTO> dtoRecords, Integer limit) {
        boolean hasNext = dtoRecords != null && dtoRecords.size() == limit;
        return CursorPage.<DTO>builder()
                .records(dtoRecords)
                .hasNext(hasNext)
                .build();
    }
}
