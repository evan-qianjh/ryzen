package com.qianjh.ryzen.plugin.sfexpress.express.service.dto;

import lombok.Builder;

import java.util.List;

@Builder(toBuilder = true)
public record OrderCreateResp(
        String orderId, // 客户订单号
        String originCode, // 原寄地区域代码，可用于顺丰 电子运单标签打印
        String destCode, // 目的地区域代码，可用于顺丰 电子运单标签打印
        String filterResult, // 筛单结果： 1：人工确认 2：可收派 3：不可以收派
        String remark,// 如果filter_result=3时为必填， 不可以收派的原因代码： 1：收方超范围 2：派方超范围 3：其它原因 高峰管控提示信息 【数字】：【高峰管控提示信息】 (如 4：温馨提示 ，1：春运延时)
        String url, // 二维码URL （用于CX退货操作的URL）
        String paymentLink, // 用于第三方支付运费的URL
        String isUpstairs, // 是否送货上楼 1:是
        String isSpecialWarehouseService, // true 包含特殊仓库增值服务
//        List<serviceList> serviceList, // 下单补充的增值服务信息
//        List<returnExtraInfoList> returnExtraInfoList, // 返回信息扩展属性
        List<WaybillNoInfo> waybillNoInfoList, // 顺丰运单号
        List<RouteLabelInfo> routeLabelInfo, // 路由标签，除少量特殊场景用户外，其余均会返回
        String scenePlanCode // 组件服务编码
) {

    @Builder(toBuilder = true)
    public record WaybillNoInfo(
            String waybillType, // 运单号类型1：母单 2 :子单 3 : 签回单*waybillNoInfoList有值时必传
            String waybillNo, // 运单号
            String boxNo, // 箱号
            String length, // 长(cm)
            String width, // 宽(cm)
            String height, // 高(cm)
            String weight // 重量(kg)
    ){}

    @Builder(toBuilder = true)
    public record RouteLabelInfo(
            String code, // 返回调用结果，1000：调用成功； 其他调用失败
            String message, // 失败异常描述
            RouteLabelData routeLabelData // 路由标签数据详细数据，除少量特殊场景用户外，其余均会返回
    ) {

        @Builder(toBuilder = true)
        public record RouteLabelData(
                String waybillNo // 运单号
                // TODO
        ) {}
    }
}
