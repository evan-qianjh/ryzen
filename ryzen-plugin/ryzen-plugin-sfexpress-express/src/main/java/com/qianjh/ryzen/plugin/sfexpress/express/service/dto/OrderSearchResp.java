package com.qianjh.ryzen.plugin.sfexpress.express.service.dto;

import java.util.List;

public record OrderSearchResp(
        String orderId,
        String origincode, // 原寄地区域代码，可用 于顺丰电子运单标签打印
        String destcode, // 目的地区域代码，可用 于顺丰电子运单标签打印
        String filterResult, // 筛单结果： 1：人工确认 2：可收派 3：不可以收派
        List<ReturnExtraInfo> returnExtraInfoList, // 返回信息扩展属性
        List<waybillNoInfo> waybillNoInfoList, // 顺丰运单号
        List<RouteLabelInfo> routeLabelInfo // 路由标签数据
) {

    public record ReturnExtraInfo(
            String attrName, // 扩展属性名
            String attrVal // 扩展属性值
    ) {
    }

    public record waybillNoInfo(
            String waybillType, // 运单号类型 1：母单 2 :子单 3 : 签回单
            String waybillNo // 运单号
    ) {
    }

    public record RouteLabelInfo(
            String code,
            // 0000（接口参数异常） 0010（其它异常） 0001（xml解析异常） 0002（字段校验异常） 0003（票数节点超出最大值， 批量请求最大票数为100票） 0004（RLS获取路由标签的必要 字段为空） 1000 成功
            String message, // code为0XXX时的错误消息
            RouteLabelData routeLabelData // code为0XXX时的错误消息
    ) {

        public record RouteLabelData(
                String waybillNo, // 运单号
                String sourceTransferCode, // 原寄地中转场
                String sourceCityCode, // 原寄地城市代码
                String sourceDeptCode, // 原寄地网点代码
                String sourceTeamCode, // 原寄地单元区域
                String destCityCode, // 目的地城市代码,eg:755
                String destDeptCode, // 目的地网点代码,eg:755AQ
                String destDeptCodeMapping, // 目的地网点代码映射码
                String destTeamCode, // 目的地单元区域,eg:001
                String destTeamCodeMapping, // 目的地单元区域映射码
                String destTransferCode, // 目的地中转场
                String destRouteLabel,
                // 打单时的路由标签信息如果是大网的路由标签，这里的值是目的地网点代码，如果是同城配的路由标签，这里的值是根据同城配的设置映射出来的值，不同的配置结果会不一样，不能根据-符号切分（如：上海同城配，可能是：集散点-目的地网点-接驳点，也有可能是目的地网点代码-集散点-接驳点）
                String twoDimensionCode,
                // 二维码根据规则生成字符串信息,格式为MMM={‘k1’:’（目的地中转场代码）’,‘k2’:’（目的地原始网点代码）’,‘k3’:’（目的地单元区域）’,‘k4’:’（附件通过三维码（express_type_code、 limit_type_code、 cargo_type_code）映射时效类型）’,‘k5’:’（运单号）’，‘k6’:’（AB标识）’}
                String proCode, // 时效类型:值为二维码中的K4
                String printIcon,
                // 打印图标根据托寄物判断需要打印的图标(重货,蟹类,生鲜,易碎，Z标)?返回值有8位，每一位只有0和1两种，0表示按运单默认的规则，1表示显示。后面两位默认0备用。顺序如下：重货,蟹类,生鲜,易碎,医药类,Z标,0,0如：00000000表示不需要打印重货，蟹类，生鲜，易碎,医药,Z标,备用,备用
                String abFlag, // AB标
                String errMsg, // 查询出现异常时返回信息。返回代码：0 系统异常1 未找到运单
                String destPortCode, // 目的地口岸代码
                String destCountry, // 目的国别(国别代码如：JP)
                String destPostCode, // 目的地邮编
                String goodsValueTotal, // 总价值(保留两位小数，数字类型，可补位)
                String currencySymbol, // 币种
                String goodsNumber,
                String codingMapping,
                String proName
        ) {
        }
    }
}
