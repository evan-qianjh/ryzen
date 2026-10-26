# 工程结构

- ryzen
    - ryzen-framework [框架]
        - ryzen-framework-common [通用]
        - ryzen-framework-gateway [网关]
        - ryzen-framework-http [HTTP]
        - ryzen-framework-security [安全]
        - ryzen-framework-service [服务]
        - ryzen-framework-servlet [Servlet]
        - ryzen-framework-stream [事件流]
        - ryzen-framework-token [令牌]
        - ryzen-framework-websocket [WebSocket]
    - ryzen-plugin [插件]
        - ryzen-plugin-aliyun [阿里云]
        - ryzen-plugin-aliyun-oss [阿里云OSS]
        - ryzen-plugin-aliyun-sms [阿里云短信]
        - ryzen-plugin-oss [OSS]
        - ryzen-plugin-sms [短信]
    - ryzen-gateway [网关]
        - ryzen-gateway-partner [合作方]
        - ryzen-gateway-oem-app [OEM APP]
        - ryzen-gateway-saas-app [SaaS APP]
        - ryzen-gateway-tenant-app [租户APP]
        - ryzen-gateway-user-app [用户APP]
        - ryzen-gateway-user-open [用户OpenAPI]
    - ryzen-user [用户]
        - ryzen-user-http [HTTP接口]
        - ryzen-user-ws [WS接口]
        - ryzen-user-job [定时任务]
        - ryzen-user-stream [事件处理]
        - ryzen-user-lib-entity [数据库实体]
        - ryzen-user-lib-model [开放模型]
        - ryzen-user-lib-service [业务代码]
    - ryzen-oem [OEM] （同ryzen-user）
    - ryzen-saas [SaaS]（同ryzen-user）
    - ryzen-tenant [租户]（同ryzen-user）

# 业务执行驱动方式

**按什么因素触发业务执行进行分类**

| 驱动方式 | 模块命名       | 说明                  | 事件角色      |
|----------|----------------|-----------------------|---------------|
| 请求驱动 | ryzen-*-http   | HTTP接口，遵循RESTful | 生产者        |
| 请求驱动 | ryzen-*-ws     | WebSocket接口         | 生产者        |
| 事件驱动 | ryzen-*-stream | 消费MQ消息            | 生产者/消费者 |
| 时间驱动 | ryzen-*-job    | 定时任务              | 生产者        |

# Controller前缀

| PATH                    | 说明                                         |
|-------------------------|----------------------------------------------|
| /cli/**                 | 开发/运维使用                                |
| /service/**             | 服务内部调用                                 |
| /partner/**             | 面向合作方 (如支付宝/微信支付回调、三方通知) |
| /user/app[/public]/**   | 面向用户APP接口                              |
| /user/open[/public]/**  | 面向用户OpenAPI接口                          |
| /tenant/app[/public]/** | 面向租户APP接口                              |
| /oem/app[/public]/**    | 面向OEM APP接口                              |
| /saas/app[/public]/**   | 面向SaaS APP接口                             |

# 域名规则

| 参与者 | 接入方式 | 域名                                                    | Controller前缀 | 备注       |
|--------|----------|---------------------------------------------------------|----------------|------------|
| 合作方 | API      | https://partner-api.qianjh.com/{service}/{partner}/xxx  | /partner       |            |
| 用户端 | APP      | https://user-appapi.qianjh.com/{service}[/public]/xxx   | /user/app      |            |
| 用户端 | Open API | https://user-openapi.qianjh.com/{service}[/public]/xxx  | /user/open     |            |
| 租户端 | APP      | https://tenant-appapi.qianjh.com/{service}[/public]/xxx | /tenant/app    |            |
| 租户端 | APP      | https://tenant.qianjh.com/api/{service}[/public]/xxx    | /tenant/app    | 无跨域方式 |
| OEM端  | APP      | https://oem-appapi.qianjh.com/{service}[/public]/xxx    | /oem/app       |            |
| OEM端  | APP      | https://oem.qianjh.com/api/{service}[/public]/xxx       | /oem/app       | 无跨域方式 |
| SaaS端 | APP      | https://saas-appapi.qianjh.com/{service}[/public]/xxx   | /saas/app      |            |
| SaaS端 | APP      | https://saas.qianjh.com/api/{service}[/public]/xxx      | /saas/app      | 无跨域方式 |

# TODO

- 接口鉴权
- 操作留痕
- open网关防止请求重放

## License

[MIT © Ryzen-2026](./LICENSE)
