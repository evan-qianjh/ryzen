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
    - ryzen-gateway [网关]
        - ryzen-gateway-oem-app [OEM APP网关]
        - ryzen-gateway-partner [合作方网关]
        - ryzen-gateway-saas-app [SaaS APP网关]
        - ryzen-gateway-tenant-app [租户APP网关]
        - ryzen-gateway-user-app [用户APP网关]
        - ryzen-gateway-user-open [用户OpenAPI网关]
    - ryzen-plugin [插件]
        - ryzen-plugin-alipay [插件-支付宝]
        - ryzen-plugin-alipay-app [插件-APP]
        - ryzen-plugin-aliyun [插件-阿里云]
        - ryzen-plugin-aliyun-oss [插件-阿里云OSS]
        - ryzen-plugin-app [插件-APP]
        - ryzen-plugin-oss [插件-OSS]
        - ryzen-plugin-sms [插件-短信]
    - ryzen-service [服务]
        - ryzen-service-oem [OEM服务]
        - ryzen-service-saas [SaaS服务]
        - ryzen-service-tenant [租户服务]
        - ryzen-service-user [用户服务]

# Service Controller
- /cli/**                       面向CLI（开发运维使用）
- /service[/public]/**          面向服务（内部调用）
- /partner/**                   面向合作方 (如支付宝、微信回调、通知)
- /user/app[/public]/**        面向用户-APP
- /user/open[/public]/**       面向用户-OpenAPI
- /oem/app[/public]/**              面向OEM-APP
- /tenant/app[/public]/**           面向租户-APP
- /saas/app[/public]/**             面向SaaS-APP


# 域名规则
| 参与者 | 接入方式 | 域名                                                      | service前缀 | 备注 |
|---|---|---------------------------------------------------------|---|---|
| 合作端 | API | https://partner-api.qianjh.com/{service}/{partner}/xxx  | /partner | |
| 用户端 | APP | https://user-appapi.qianjh.com/{service}[/public]/xxx   | /user/app | |
| 用户端 | Open API | https://user-openapi.qianjh.com/{service}[/public]/xxx  | /user/open | |
| 租户端 | APP | https://tenant-appapi.qianjh.com/{service}[/public]/xxx | /tenant/app | |
| 租户端 | APP | https://tenant.qianjh.com/api/{service}[/public]/xxx    | /tenant/app | 无跨域方式 |
| OEM端 | APP | https://oem-appapi.qianjh.com/{service}[/public]/xxx       | /oem/app | |
| OEM端 | APP | https://oem.qianjh.com/api/{service}[/public]/xxx       | /oem/app | 无跨域方式 |
| SaaS端 | APP | https://saas-appapi.qianjh.com/{service}[/public]/xxx      | /saas/app | |
| SaaS端 | APP | https://saas.qianjh.com/api/{service}[/public]/xxx      | /saas/app | 无跨域方式 |

# TODO
- 接口鉴权
- 操作留痕

## License

[MIT © Ryzen-2026](./LICENSE)
