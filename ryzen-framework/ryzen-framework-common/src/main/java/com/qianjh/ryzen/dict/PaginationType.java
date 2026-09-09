package com.qianjh.ryzen.dict;

/**
 * 分页方式
 * @author QianJH
 */
public enum PaginationType {
    /**
     * 基于游标的分页
     * SELECT * FROM articles WHERE id < :lastId ORDER BY id DESC LIMIT 10;
     */
    CURSOR,
    /**
     * 基于页码的分页
     * SELECT * FROM articles ORDER BY id DESC LIMIT 10 OFFSET 30;
     * -- 等价于 LIMIT pageSize OFFSET (pageIndex - 1) * pageSize
     */
    OFFSET
}
