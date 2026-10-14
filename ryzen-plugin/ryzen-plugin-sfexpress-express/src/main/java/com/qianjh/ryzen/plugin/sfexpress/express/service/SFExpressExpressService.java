package com.qianjh.ryzen.plugin.sfexpress.express.service;

import com.qianjh.ryzen.plugin.express.service.ExpressService;
import com.qianjh.ryzen.plugin.sfexpress.express.enums.Language;
import com.qianjh.ryzen.plugin.sfexpress.express.enums.SearchType;
import com.qianjh.ryzen.plugin.sfexpress.express.service.dto.OrderSearchRespDto;
import com.qianjh.ryzen.plugin.sfexpress.express.service.dto.SfResp;
import reactor.core.publisher.Mono;

/**
 * 顺丰物流服务
 */
public interface SFExpressExpressService extends ExpressService {

    Mono<SfResp<OrderSearchRespDto>> orderSearch(Long oemId, SearchType searchType, String orderId, String mainWaybillNo, Language language);

    Mono<SfResp<OrderSearchRespDto>> orderSearch(Long oemId, SearchType searchType, String orderId, String mainWaybillNo);
}
