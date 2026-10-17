package com.qianjh.ryzen.plugin.sfexpress.express.service.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder(toBuilder = true)
public record OrderCreateReq(
        String orderId, // 客户订单号不可重复，重复使用订单号时将返回首次成功获取的运单号
        CustomsInfo customsInfo, // 报关信息
        List<CargoDetail> cargoDetails, // 托寄物信息
        String cargoDesc, // 托寄物类型描述,如： 文件，电子产品，衣服等，托寄物名称请使用cargoDetails传值
        List<ExtraInfo> extraInfoList, // 扩展属性
        List<Service> serviceList, // 增值服务信息
        List<ContactInfo> contactInfoList, // 收寄双方信息
        String monthlyCard, // 顺丰月结卡号 月结支付时传值，现结不可传值；沙箱联调可使用测试月结卡号7551234567（非正式，无须绑定，仅支持联调使用）
        Integer payMethod, // 付款方式，支持以下值： 1:寄方付 2:收方付（monthlyCard不可传入月结） 3:第三方付
        Integer expressTypeId, // 快件产品类别，支持附录《快件产品类别表》的产品编码值，仅可使用与顺丰销售约定的快件产品。
        Integer parcelQty, // 包裹数，一个包裹对应一个运单号；若包裹数大于1，则返回一个母运单号和N-1个子运单号
        BigDecimal totalLength, // 客户订单货物总长，单位厘米， 精确到小数点后3位， 包含子母件
        BigDecimal totalWidth, // 客户订单货物总宽，单位厘米， 精确到小数点后3位， 包含子母件
        BigDecimal totalHeight, // 客户订单货物总高，单位厘米， 精确到小数点后3位， 包含子母件
        BigDecimal totalVolume, // 订单货物总体积，单位立方厘米, 精确到小数点后3位，会用于计抛 (是否计抛具体商务沟通中 双方约定)
        BigDecimal totalWeight, // 订单货物总重量（郑州空港海关必填）， 若为子母件必填， 单位千克， 精确到小数点后3位，如果提供此值， 必须>0 (子母件需>6)
        BigDecimal totalNetWeight, // 商品总净重
        String sendStartTm, // 要求上门取件开始时间， 格式： YYYY-MM-DD HH24:MM:SS， 示例： 2012-7-30 09:30:00 ，若该字段没有赋值，默认开始时间为当前时间，（可配合上门取件截止时间pickupAppointEndTime扩展字段备注进行下发，若没有给截止时间则系统默认1小时的截止时间）
        String pickupAppointEndTime, // 上门揽收截止时间，格式: 2023-07-04 12:01:02（预约单传预约截止时间，不赋值默认按当前时间下发，1小时内取件）
        // 要求上门取件开始时间， 格式： YYYY-MM-DD HH24:MM:SS， 示例： 2012-7-30 09:30:00 ，若该字段没有赋值，默认开始时间为当前时间，（可配合上门取件截止时间pickupAppointEndTime扩展字段备注进行下发，若没有给截止时间则系统默认1小时的截止时间）
        Integer isDocall,
        // 支持以下值： 1：要求 0：不要求。为1时系统分配揽收任务给到小哥，小哥需要按照预约时间上门揽收，一般用于高时效产品。 为0时是集收场景，发货商家提前与网点小哥约定每天揽收时段，一般用于集收场景。
        Integer isSignBack, // 是否返回签回单 （签单返还）的运单号， 支持以下值： 1：要求 0：不要求
        String custReferenceNo, // 客户参考编码：如客户原始订单号
        Integer temperatureRange, // 温度范围类型，当 express_type为12 医药温控件 时必填，支持以下值： 1:冷藏 3：冷冻
        String orderSource, // 订单平台类型 （对于平台类客户， 如果需要在订单中 区分订单来源， 则可使用此字段） 天猫:tmall， 拼多多：pinduoduo， 京东 : jd 等平台类型编码
        String remark, // 备注
        Integer isOneselfPickup, // 快件自取，支持以下值： 1：客户同意快件自取 0：客户不同意快件自取
        String filterField, // 筛单特殊字段用来人工筛单
        Integer isReturnQRCode, // 是否返回用来退货业务的 二维码URL， 支持以下值： 1：返回二维码 0：不返回二维码
        String specialDeliveryTypeCode, // 当EXPRESS_TYPE=235 2：极效前置单（当日达） 5：极效前置小时达 当EXPRESS_TYPE=265 4：预售电标
        String specialDeliveryValue, // 特殊派件具体表述 证件类型: 证件后8位如： 1:09296231（1 表示身份证， 暂不支持其他证件）
        String merchantPayOrderNo, // 商户支付订单号
        Integer isReturnSignBackRoutelabel, // 是否返回签回单路由标签： 默认0， 1：返回路由标签， 0：不返回
        Integer isReturnRoutelabel, // 是否返回路由标签： 默认1， 1：返回路由标签， 0：不返回；除部分特殊用户外，其余用户都默认返回
        Integer isUnifiedWaybillNo, // 是否使用国家统一面单号 1：是（建议使用，返回SF开头的15位运单号）， 0：否(特殊诉求才用，返回12位的运单号)
        String podModelAddress, // 签单返还范本地址
        String inProcessWaybillNo, // 头程运单号（郑州空港海关必填）
        Integer isGenWaybillNo, // 是否需求分配运单号1：分配 0：不分配（若带单号下单，请传值0）
        String scenePlanCode
        // 组件服务编码，特定业务场景使用该字段与快件产品类别 expressTypeId 二选一，组件服务编码需提前与客户经理沟通确定。特殊说明：商圈店配可通过expressTypeId=10000下单，scenePlanCode可不传值
) {

    @Builder(toBuilder = true)
    public record CustomsInfo(
            String declaredValue, // 客户订单货物总声明价值， 包含子母件，精确到小数点 后3位。如果是跨境件，则必填
            String declaredValueCurrency, // 货物声明价值币别，跨境 件报关需要填写 参照附录币别代码附件
            String customsBatchs, // 报关批次
            String taxPayMethod, // 税金付款方式，支持以下值： 1:寄付 2：到付 3. 第三方付
            String taxSettleAccounts, // 税金结算账号
            String paymentTool, // 支付工具
            String paymentNumber, // 支付号码
            String orderName, // 客户订单下单人姓名
            String tax // 税款
    ) {
    }

    @Builder(toBuilder = true)
    public record ExtraInfo(
            String attrName, // 扩展字段说明：attrName为字段定义， 具体如下表，value存在attrVal
            String attrVal // 扩展字段值
    ) {
    }

    @Builder(toBuilder = true)
    public record Service(
            String name, // 增值服务名，如COD等
            String value, // 增值服务扩展属性
            String value1, // 增值服务扩展属性
            String value2, // 增值服务扩展属性
            String value3, // 增值服务扩展属性
            String value4 // 增值服务扩展属性
    ) {
    }

    @Builder(toBuilder = true)
    public record ContactInfo(
            Integer contactType, // 地址类型： 1，寄件方信息 2，到件方信息
            String company, // 公司名称
            String contact, // 联系人
            String tel, // 联系电话（tel和mobile字段必填其中一个）
            String mobile, // 手机（tel和mobile字段必填其中一个）
            String country, // 国家或地区代码 例如：内地件CN 香港852
            String province, // 所在省级行政区名称，必须是标准的省级行政区名称如：北 京、广东省、广西壮族自治区等；此字段影响原寄地代码识 别，建议尽可能传该字段的值
            String city, // 所在地级行政区名称，必须是标准的城市称谓 如：北京市、 深圳市、大理白族自治州等； 此字段影响原寄地代码识别， 建议尽可能传该字段的值
            String county, // 所在县/区级行政区名称，必须 是标准的县/区称谓，如：福田区，南涧彝族自治县、准格尔旗等
            String address,
            // 详细地址，若有四级行政区划，如镇/街道等信息可拼接至此字段，格式样例：镇/街道+详细地址。若province/city 字段的值不传，此字段必须包含省市信息，避免影响原寄地代码识别，如：广东省深圳市福田区新洲十一街万基商务大厦10楼；此字段地址必须详细，否则会影响目的地中转识别；
            String postCode, // 邮编，跨境件必填（中国内地， 港澳台互寄除外）
            String email, // 邮箱地址
            String taxNo, // 税号
            String contactRemark, // 联系人属性（01：个人件，02：公司件,跨境件或国际件需要)
            String certType, // 证件类型(跨境件或国际件需要)，参考 《证件类型说明》
            String certNo // 证件号码(跨境件或国际件需要)，参考《证件类型说明》
    ) {
    }

    @Builder(toBuilder = true)
    public record CargoDetail(
            String name, // 货物名称，如果需要生成电子 运单，则为必填
            Integer count, // 货物数量 跨境件报关需要填写
            String unit, // 货物单位，如：个、台、本， 跨境件报关需要填写
            BigDecimal weight, // 订单货物单位重量，包含子母件， 单位千克，精确到小数点后3位 跨境件报关需要填写
            BigDecimal amount, // 货物单价，精确到小数点后3位， 跨境件报关需要填写
            String currency, // 货物单价的币别
            String sourceArea, // 原产地国别， 跨境件报关需要填写
            String productRecordNo, // 货物产品国检备案编号
            String goodPrepardNo, // 商品海关备案号
            String taxNo, // 商品行邮税号
            String hsCode, // 海关编码
            String goodsCode, // 商品编号
            String brand, // 货物品牌
            String specifications, // 货物规格型号
            String manufacturer, // 生产厂家
            BigDecimal shipmentWeight, // 托寄物毛重
            BigDecimal length, // 托寄物长
            BigDecimal width, // 托寄物宽
            BigDecimal height, // 托寄物高
            BigDecimal volume, // 托寄物长
            BigDecimal cargoDeclaredValue, // 托寄物声明价值（杭州口岸必填）
            String declaredValueDeclaredCurrency, // 托寄物声明价值币别（杭州口岸必填）
            String cargoId, // 货物id（逆向物流）
            Integer intelligentInspection, // 智能验货标识(1-是,0-否) （逆向物流）
            String snCode, // 货物标识码（逆向物流）
            String stateBarCode // 国条码
    ) {
    }


}
