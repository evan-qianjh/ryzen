package com.qianjh.ryzen.plugin.sfexpress.express.service;

import com.qianjh.ryzen.plugin.express.service.PluginExpressService;
import com.qianjh.ryzen.plugin.sfexpress.express.enums.Language;
import com.qianjh.ryzen.plugin.sfexpress.express.enums.SearchType;
import com.qianjh.ryzen.plugin.sfexpress.express.service.dto.OrderCreateReq;
import com.qianjh.ryzen.plugin.sfexpress.express.service.dto.OrderCreateResp;
import com.qianjh.ryzen.plugin.sfexpress.express.service.dto.OrderSearchResp;
import com.qianjh.ryzen.plugin.sfexpress.express.service.dto.SfResp;
import reactor.core.publisher.Mono;

/**
 * 顺丰物流服务
 */
public interface PluginSFExpressExpressService extends PluginExpressService {

    /**
     * 订单搜索
     *
     * @param oemId         OEM ID
     * @param searchType    搜索类型
     * @param orderId       订单号
     * @param mainWaybillNo 运单号
     * @return 结果
     */
    Mono<SfResp<OrderSearchResp>> orderSearch(Long oemId, SearchType searchType, String orderId, String mainWaybillNo);

    Mono<SfResp<OrderSearchResp>> orderSearch(Long oemId, SearchType searchType, String orderId, String mainWaybillNo, Language language);

    /**
     * 订单创建
     *
     * @param oemId OEM ID
     * @param body  报文
     * @return 结果
     */
    Mono<SfResp<OrderCreateResp>> orderCreate(Long oemId, OrderCreateReq body);

    Mono<SfResp<OrderCreateResp>> orderCreate(Long oemId, OrderCreateReq body, Language language);
}
